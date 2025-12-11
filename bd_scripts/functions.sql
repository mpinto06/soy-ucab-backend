-- MIGUEL PINTO
-- STORED PROCEDURES
CREATE OR REPLACE PROCEDURE registrar_voto_encuesta(
    p_correo_miembro VARCHAR,
    p_id_publicacion VARCHAR,
    p_correo_autor_encuesta VARCHAR,
    p_nueva_opcion VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_opcion_anterior VARCHAR;
    v_fecha_fin TIMESTAMPTZ;
    v_check INTEGER; -- Variable auxiliar para validar existencia de opción
BEGIN
    -- Obtener fecha de fin de la encuesta
    SELECT fecha_hora_fin INTO v_fecha_fin
    FROM Encuesta
    WHERE id_publicacion = p_id_publicacion 
      AND correo_autor = p_correo_autor_encuesta;

    -- Si 'v_fecha_fin' es NULL, la encuesta no existe
    IF NOT FOUND THEN
        RAISE EXCEPTION 'La encuesta no existe.';
    END IF;

    IF v_fecha_fin IS NOT NULL AND v_fecha_fin < CURRENT_TIMESTAMP THEN
        RAISE EXCEPTION 'No puedes votar, la encuesta ya finalizó.';
    END IF;

    -- Se verifica que la opción exista
    SELECT 1 INTO v_check
    FROM Opcion
    WHERE id_publicacion = p_id_publicacion
      AND correo_autor = p_correo_autor_encuesta
      AND texto_opcion = p_nueva_opcion;
      
    IF NOT FOUND THEN
        RAISE EXCEPTION 'La opción "%" no existe en esta encuesta.', p_nueva_opcion;
    END IF;

    -- Buscamos si el usuario ya votó
    SELECT texto_opcion INTO v_opcion_anterior
    FROM Vota
    WHERE correo_miembro = p_correo_miembro
      AND id_publicacion = p_id_publicacion
      AND correo_autor_encuesta = p_correo_autor_encuesta;

    -- Si el usuario ya votó, actualizamos su voto
    IF FOUND THEN
        -- Si intenta votar por la misma opción, no hacemos nada
        IF v_opcion_anterior = p_nueva_opcion THEN
            RAISE NOTICE 'El usuario ya tiene registrado este voto.';
            RETURN;
        END IF;

        -- Restamos un voto a la opción vieja
        UPDATE Opcion 
        SET total_votos = total_votos - 1
        WHERE id_publicacion = p_id_publicacion
          AND correo_autor = p_correo_autor_encuesta
          AND texto_opcion = v_opcion_anterior;

        -- Cambiamos la nueva opción en vota
        UPDATE Vota
        SET texto_opcion = p_nueva_opcion,
            fecha_voto = CURRENT_TIMESTAMP
        WHERE correo_miembro = p_correo_miembro
          AND id_publicacion = p_id_publicacion
          AND correo_autor_encuesta = p_correo_autor_encuesta
          AND texto_opcion = v_opcion_anterior; -- Importante especificar cuál fila vieja actualizar

    -- Es el primer voto del usuario
    ELSE
        INSERT INTO Vota (correo_miembro, id_publicacion, correo_autor_encuesta, texto_opcion)
        VALUES (p_correo_miembro, p_id_publicacion, p_correo_autor_encuesta, p_nueva_opcion);
    END IF;

    -- Sumamos un voto a la nueva opción elegida
    UPDATE Opcion 
    SET total_votos = total_votos + 1
    WHERE id_publicacion = p_id_publicacion
      AND correo_autor = p_correo_autor_encuesta
      AND texto_opcion = p_nueva_opcion;

END;
$$;

-- FUNCIONES
CREATE OR REPLACE FUNCTION son_amigos(usuario_a VARCHAR, usuario_b VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    -- Retorna TRUE si existe una fila en Es_Amigo (en cualquier dirección) con estado 'aceptada'
    RETURN EXISTS (
        SELECT 1 FROM Es_Amigo
        WHERE estado = 'aceptada'
        AND (
            (correo_persona1 = usuario_a AND correo_persona2 = usuario_b)
            OR
            (correo_persona1 = usuario_b AND correo_persona2 = usuario_a)
        )
    );
END;
$$ LANGUAGE plpgsql STABLE;

-- (Necesito esta función para el reporte de Detalle de Miembro)
CREATE OR REPLACE FUNCTION calcular_meses_periodo(
    p_id_periodo VARCHAR, 
    p_correo_persona VARCHAR
)
RETURNS INTEGER AS $$
DECLARE
    v_fecha_inicio DATE;
    v_fecha_fin DATE;
    v_fecha_calculo DATE;
BEGIN
    -- Buscamos las fechas del periodo especificado
    SELECT fecha_inicio, fecha_fin 
    INTO v_fecha_inicio, v_fecha_fin
    FROM Periodo
    WHERE id_periodo = p_id_periodo AND correo_persona = p_correo_persona;

    -- Validación: Si no encuentra el registro, retornamos NULL
    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    -- Si fecha_fin es NULL, usamos la fecha de hoy (CURRENT_DATE)
    -- Si tiene fecha, usamos esa.
    v_fecha_calculo := COALESCE(v_fecha_fin, CURRENT_DATE);

    -- Cálculo matemático (Años * 12 + Meses) usando la función age()
    RETURN (EXTRACT(YEAR FROM age(v_fecha_calculo, v_fecha_inicio)) * 12) + 
           EXTRACT(MONTH FROM age(v_fecha_calculo, v_fecha_inicio));
END;
$$ LANGUAGE plpgsql;


-- TRIGGERS
--Actualiza el contador de likes de una publicación
CREATE OR REPLACE FUNCTION actualizar_contador_likes()
RETURNS TRIGGER AS $$
BEGIN
    -- Si alguien le dio like a una publicación
    IF (TG_OP = 'INSERT') THEN
        UPDATE Publicacion
        SET total_likes = total_likes + 1
        WHERE id_publicacion = NEW.id_publicacion 
          AND correo_autor = NEW.correo_autor_pub;
        RETURN NEW;
    
    -- Si alguien quita el like a una publicación
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE Publicacion
        SET total_likes = total_likes - 1
        WHERE id_publicacion = OLD.id_publicacion 
          AND correo_autor = OLD.correo_autor_pub;
        RETURN OLD;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_likes_update
AFTER INSERT OR DELETE ON Me_Gusta
FOR EACH ROW
EXECUTE FUNCTION actualizar_contador_likes();


-- Actualiza el contador de comentarios de una publicación
CREATE OR REPLACE FUNCTION actualizar_contador_comentarios()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE Publicacion
        SET total_comen = total_comen + 1
        WHERE id_publicacion = NEW.id_publicacion 
          AND correo_autor = NEW.correo_autor_pub;
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE Publicacion
        SET total_comen = total_comen - 1
        WHERE id_publicacion = OLD.id_publicacion 
          AND correo_autor = OLD.correo_autor_pub;
        RETURN OLD;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_comentarios_update
AFTER INSERT OR DELETE ON Comenta
FOR EACH ROW
EXECUTE FUNCTION actualizar_contador_comentarios();


-- CONSTRAINTS

-- Solo Amigos se pueden validar
ALTER TABLE Valida
ADD CONSTRAINT chk_solo_amigos_pueden_validar
CHECK (
    correo_validador <> correo_validado 
    AND
    son_amigos(correo_validador, correo_validado)
);

-- Publicación exclusivas en los grupos
ALTER TABLE Publicacion
ADD CONSTRAINT fk_solo_miembros_pueden_publicar
FOREIGN KEY (id_grupo, correo_autor)
REFERENCES Pertenece (nombre_grupo, correo_miembro);


-- Restricción para evitar orden invertido en Es_Amigo
ALTER TABLE Es_Amigo
ADD CONSTRAINT chk_orden_correos 
CHECK (correo_persona1 < correo_persona2);


-- Restricción para evitar que un usuario se siga a sí mismo
ALTER TABLE Sigue
ADD CONSTRAINT chk_no_auto_seguimiento 
CHECK (correo_seguidor <> correo_seguido);