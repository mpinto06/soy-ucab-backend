-- 1. Constraints

-- Oferta_Trabajo: Ubicación requerida si es Presencial o Semi-Presencial
ALTER TABLE Oferta_Trabajo
ADD CONSTRAINT check_ubicacion_requerida
CHECK (
    (modalidad IN ('Presencial', 'Semi-Presencial') AND ubicacion IS NOT NULL AND TRIM(ubicacion) <> '')
    OR
    (modalidad = 'Remoto')
);

-- Evento: Validación de ubicación/url según modalidad
ALTER TABLE Evento
ADD CONSTRAINT check_modalidad_evento_valida
CHECK (
    (modalidad = 'presencial' AND ubicacion IS NOT NULL AND TRIM(ubicacion) <> '')
    OR
    (modalidad = 'virtual' AND url_conferencia IS NOT NULL AND TRIM(url_conferencia) <> '')
);

-- Persona: Fecha nacimiento válida (en el pasado)
ALTER TABLE Persona
ADD CONSTRAINT check_fecha_nacimiento_valida
CHECK (fecha_nacimiento < CURRENT_DATE);


-- 2. Stored Procedures

CREATE OR REPLACE PROCEDURE aplicar_a_oferta(
    p_correo_aplicante VARCHAR,
    p_correo_publicador VARCHAR,
    p_nombre_cargo VARCHAR,
    p_nombre_archivo VARCHAR,
    p_archivo_cv BYTEA,
    p_texto_aplicante TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estado_oferta estado_oferta;
    v_existe_aplicacion BOOLEAN;
BEGIN
    -- 1. Verificar si la oferta existe y su estado
    SELECT estado_oferta INTO v_estado_oferta
    FROM Oferta_Trabajo
    WHERE correo_publicador = p_correo_publicador AND nombre_cargo = p_nombre_cargo;

    IF v_estado_oferta IS NULL THEN
        RAISE EXCEPTION 'La oferta de trabajo no existe.';
    END IF;

    IF v_estado_oferta <> 'abierta' THEN
        RAISE EXCEPTION 'No se puede aplicar: La oferta de trabajo no está abierta (Estado: %)', v_estado_oferta;
    END IF;

    -- 2. Verificar si el usuario ya aplicó
    SELECT EXISTS (
        SELECT 1 FROM Aplica
        WHERE correo_aplicante = p_correo_aplicante
          AND correo_publicador = p_correo_publicador
          AND nombre_cargo = p_nombre_cargo
    ) INTO v_existe_aplicacion;

    IF v_existe_aplicacion THEN
        RAISE EXCEPTION 'El usuario % ya se ha postulado a esta oferta previamente.', p_correo_aplicante;
    END IF;

    -- 3. Insertar la postulación (Con manejo de excepcion por si concurrencia o fallo de check)
    BEGIN
        INSERT INTO Aplica (
            correo_aplicante, correo_publicador, nombre_cargo, 
            nombre_archivo, archivo_cv, texto_aplicante
        ) VALUES (
            p_correo_aplicante, p_correo_publicador, p_nombre_cargo, 
            p_nombre_archivo, p_archivo_cv, p_texto_aplicante
        );
        RAISE NOTICE 'Postulación exitosa para % en el cargo %', p_correo_aplicante, p_nombre_cargo;
    EXCEPTION WHEN unique_violation THEN
        RAISE EXCEPTION 'El usuario % ya se ha postulado a esta oferta previamente (Error detectado al insertar).', p_correo_aplicante;
    END;
END;
$$;


-- 3. Functions

-- Función: Obtener Feed Priorizado

CREATE OR REPLACE FUNCTION obtener_feed_priorizado(
    p_correo_usuario VARCHAR,
    p_limite INTEGER DEFAULT 20
)
RETURNS TABLE (
    id_pub VARCHAR,
    autor VARCHAR,
    grupo VARCHAR,
    texto_preview TEXT,
    fecha TIMESTAMPTZ,
    razon_prioridad TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id_publicacion,
        p.correo_autor,
        p.id_grupo,
        LEFT(p.texto_pub, 50) || '...',
        p.fecha_hora,
        CASE
            -- Prioridad 1: Publicaciones de grupos donde soy miembro
            WHEN EXISTS (SELECT 1 FROM Pertenece pert WHERE pert.correo_miembro = p_correo_usuario AND pert.nombre_grupo = p.id_grupo) THEN 'Grupo que sigues'
            -- Prioridad 2: Publicaciones de amigos
            WHEN EXISTS (SELECT 1 FROM Es_Amigo ea WHERE (ea.correo_persona1 = p_correo_usuario AND ea.correo_persona2 = p.correo_autor) OR (ea.correo_persona2 = p_correo_usuario AND ea.correo_persona1 = p.correo_autor)) THEN 'Amigo'
            -- Prioridad 3: Publicaciones de personas que sigo
            WHEN EXISTS (SELECT 1 FROM Sigue s WHERE s.correo_seguidor = p_correo_usuario AND s.correo_seguido = p.correo_autor) THEN 'Siguiendo'
            ELSE 'General'
        END as razon
    FROM Publicacion p
    WHERE 
        -- Filtro para no mostrar mis propios posts en el feed principal si se desea
        p.correo_autor <> p_correo_usuario
    ORDER BY 
        CASE
            WHEN EXISTS (SELECT 1 FROM Pertenece pert WHERE pert.correo_miembro = p_correo_usuario AND pert.nombre_grupo = p.id_grupo) THEN 1
            WHEN EXISTS (SELECT 1 FROM Es_Amigo ea WHERE (ea.correo_persona1 = p_correo_usuario AND ea.correo_persona2 = p.correo_autor) OR (ea.correo_persona2 = p_correo_usuario AND ea.correo_persona1 = p.correo_autor)) THEN 2
            WHEN EXISTS (SELECT 1 FROM Sigue s WHERE s.correo_seguidor = p_correo_usuario AND s.correo_seguido = p.correo_autor) THEN 3
            ELSE 4
        END ASC,
        p.fecha_hora DESC
    LIMIT p_limite;
END;
$$ LANGUAGE plpgsql;

-- 4. Triggers

-- Función del trigger para verificar conflicto de agenda
CREATE OR REPLACE FUNCTION verificar_conflicto_agenda()
RETURNS TRIGGER AS $$
DECLARE
    v_inicio_nuevo TIMESTAMPTZ;
    v_fin_nuevo TIMESTAMPTZ;
    v_estado_actual estado_evento;
    v_evento_conflicto VARCHAR;
BEGIN
    -- Obtener fechas y estado del evento al que se intenta asistir
    SELECT fecha_inicio, fecha_fin, estado_evento 
    INTO v_inicio_nuevo, v_fin_nuevo, v_estado_actual
    FROM Evento WHERE nombre_evento = NEW.nombre_evento;

    -- 1. Validar estado del evento
    IF v_estado_actual NOT IN ('publicado', 'en curso') THEN
         RAISE EXCEPTION 'No se puede asistir al evento "%" porque su estado es %. (Debe estar publicado o en curso)', NEW.nombre_evento, v_estado_actual;
    END IF;

    -- Si el evento no tiene fechas definidas, no hay conflicto posible
    IF v_inicio_nuevo IS NULL OR v_fin_nuevo IS NULL THEN
        RETURN NEW;
    END IF;

    -- 2. Buscar conflictos (incluyendo fechas)
    SELECT e.nombre_evento, e.fecha_inicio, e.fecha_fin 
    INTO v_evento_conflicto, v_inicio_nuevo, v_fin_nuevo 
    FROM Asiste a
    JOIN Evento e ON a.nombre_evento = e.nombre_evento
    WHERE a.correo_miembro = NEW.correo_miembro
      AND e.nombre_evento <> NEW.nombre_evento 
      AND (e.fecha_inicio < v_fin_nuevo AND e.fecha_fin > v_inicio_nuevo)
    LIMIT 1;

    IF v_evento_conflicto IS NOT NULL THEN
        RAISE EXCEPTION 'Conflicto de agenda: El usuario ya asiste al evento "%" que ocurre de % a %.', 
        v_evento_conflicto, v_inicio_nuevo, v_fin_nuevo;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger definition
CREATE TRIGGER trigger_verificar_agenda
BEFORE INSERT ON Asiste
FOR EACH ROW
EXECUTE FUNCTION verificar_conflicto_agenda();


--Constrints
-- Intenta insertar una oferta presencial con ubicación NULL. Debe fallar.
-- INSERT INTO Oferta_Trabajo (correo_publicador, nombre_cargo, tipo_cargo, modalidad, ubicacion) VALUES ('google@empresa.com', 'Enginner', 'Jornada Completa', 'Presencial', '');


-- Intenta insertar una persona que nace en el futuro (2050). Debe fallar.
-- INSERT INTO Persona (correo_electronico, primer_nombre, primer_apellido, fecha_nacimiento, sexo, ubicacion_geografica) VALUES ('miguel@ucab.edu.ve', 'Miguel', 'Martinez', '2050-01-01', 'M', 'Caracas');


-- Intenta insertar un evento virtual sin URL. Debe fallar.
-- INSERT INTO Evento (nombre_evento, correo_organizador, descripcion, modalidad, estado_evento, ubicacion, fecha_inicio, fecha_fin) VALUES ('Evento Malo', 'informatica@ucab.edu.ve', 'Sin URL', 'virtual', 'publicado', NULL, NOW(), NOW());

--Stored Procedure
--CALL aplicar_a_oferta('miguel@ucab.edu.ve', 'google@empresa.com', 'SRE Engineer', 'cv.pdf', '\x00', 'Hola');

--Function
--SELECT * FROM obtener_feed_priorizado('miguel@ucab.edu.ve');

--Triggger
--INSERT INTO Evento (nombre_evento, correo_organizador, descripcion, modalidad, estado_evento, ubicacion, fecha_inicio, fecha_fin) 
--VALUES ('Charla Python', 'informatica@ucab.edu.ve', 'Charla avanzada', 'presencial', 'publicado', 'Caracas', '2025-12-01 10:00:00', '2025-12-01 12:00:00');


--INSERT INTO Asiste (correo_miembro, nombre_evento) 
--VALUES ('miguel@ucab.edu.ve', 'Charla Python');













