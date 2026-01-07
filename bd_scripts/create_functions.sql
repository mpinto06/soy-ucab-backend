-- CREACIÓN DE ROLES (Igual que antes)
CREATE ROLE arquitecto_db WITH LOGIN PASSWORD 'admin_seguro_123' CREATEDB BYPASSRLS;
CREATE ROLE app_backend WITH LOGIN PASSWORD 'desarrollador_123';
CREATE ROLE auditor_externo WITH LOGIN PASSWORD 'invitado_789';

-- PERMISOS GLOBALES
GRANT ALL PRIVILEGES ON DATABASE "soyucab" TO arquitecto_db;
GRANT CONNECT ON DATABASE "soyucab" TO app_backend;
GRANT CONNECT ON DATABASE "soyucab" TO auditor_externo;

-- Para el Backend (Desarrollador)
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_backend;

-- Para el Auditor
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT SELECT ON TABLES TO auditor_externo;

-- Para el Arquitecto
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT ALL ON TABLES TO arquitecto_db;

-- LA FUNCIÓN DE SEGURIDAD
CREATE OR REPLACE FUNCTION get_current_user_email() RETURNS VARCHAR AS $$
BEGIN
    RETURN current_setting('app.current_email', true);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ENUMS 
CREATE TYPE privacidad_msg AS ENUM ('cualquiera', 'solo_amigos', 'nadie');
CREATE TYPE tipo_sexo AS ENUM ('M', 'F');
CREATE TYPE nivel_carrera AS ENUM ('pregrado', 'posgrado');
CREATE TYPE tipo_entidad AS ENUM ('facultad', 'escuela', 'libre');
CREATE TYPE extension_imagen AS ENUM ('jpg', 'jpeg', 'png');
CREATE TYPE extension_certificado AS ENUM ('jpg', 'jpeg', 'png', 'pdf');
CREATE TYPE extension_carta AS ENUM ('doc', 'docx', 'pdf');
CREATE TYPE rol_sistema AS ENUM ('superadmin', 'moderador', 'usuario_estandar');

-- ==========================================
-- TABLAS PRINCIPALES
-- ==========================================

CREATE TABLE Miembro (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    hash_contrasena VARCHAR(255) NOT NULL,
    encabezado_perfil VARCHAR(255),
    archivo_foto BYTEA,
    formato_foto extension_imagen,
    nombre_archivo_foto VARCHAR(50),
    rol_acceso rol_sistema DEFAULT 'usuario_estandar' NOT NULL,
    privacidad_mensajes privacidad_msg DEFAULT 'cualquiera',
    notif_publicaciones BOOLEAN DEFAULT TRUE,
    notif_eventos BOOLEAN DEFAULT TRUE,
    notif_seguidores BOOLEAN DEFAULT TRUE,
    notif_amigos BOOLEAN DEFAULT TRUE
);

CREATE TABLE Persona (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    primer_nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50),
    primer_apellido VARCHAR(50) NOT NULL,
    segundo_apellido VARCHAR(50),
    fecha_nacimiento DATE,
    sexo tipo_sexo,
    ubicacion_geografica VARCHAR(255),
    FOREIGN KEY (correo_electronico) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Organizacion (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    nombre_organizacion VARCHAR(255) UNIQUE NOT NULL,   
    descripcion_org VARCHAR(255),
    FOREIGN KEY (correo_electronico) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Dependencia_UCAB (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    tipo_entidad_institucional tipo_entidad NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Organizacion(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Organizacion_Asociada (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    RIF VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Organizacion(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Facultad (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (correo_electronico) REFERENCES Dependencia_UCAB(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Escuela (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    correo_facultad VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Dependencia_UCAB(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_facultad) REFERENCES Facultad(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Carrera (
    nombre_carrera VARCHAR(255) PRIMARY KEY,
    nivel_carrera nivel_carrera NOT NULL,
    correo_electronico VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Escuela(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Notificacion (
    id_notificacion VARCHAR(50),
    correo_destinatario VARCHAR(255),
    texto_notificacion VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id_notificacion, correo_destinatario),
    FOREIGN KEY (correo_destinatario) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

-- ==========================================
-- PERIODOS Y CV
-- ==========================================

CREATE TYPE tipo_cargo_exp AS ENUM ('Jornada Completa', 'Jornada Parcial', 'Contrato', 'Pasantía', 'Voluntariado', 'Miembro');
CREATE TYPE modalidad_trabajo AS ENUM ('Remoto', 'Semi-Presencial', 'Presencial');
CREATE TYPE estado_oferta AS ENUM ('abierta', 'cerrada');

CREATE TABLE Periodo (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    descripcion_periodo TEXT,
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (correo_persona) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE,
    CHECK (fecha_inicio <> fecha_fin)
);

CREATE TABLE Periodo_Educativo (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    id_carrera VARCHAR(255),
    archivo_certificado BYTEA,
    formato_archivo extension_carta,
    nombre_estudio VARCHAR(255),
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona) ON UPDATE CASCADE,
    FOREIGN KEY (id_carrera) REFERENCES Carrera(nombre_carrera) ON UPDATE CASCADE
);

CREATE TABLE Periodo_Experiencia (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    correo_organizacion VARCHAR(255) NOT NULL,
    tipo_cargo tipo_cargo_exp NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    archivo_carta BYTEA,
    formato_archivo extension_certificado,
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona) ON UPDATE CASCADE,
    FOREIGN KEY (correo_organizacion) REFERENCES Organizacion(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Habilidad (
    nombre_habilidad VARCHAR(50) PRIMARY KEY
);

CREATE TABLE Demuestra (
    nombre_habilidad VARCHAR(50),
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    PRIMARY KEY (nombre_habilidad, id_periodo, correo_persona),
    FOREIGN KEY (nombre_habilidad) REFERENCES Habilidad(nombre_habilidad) ON UPDATE CASCADE,
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona) ON UPDATE CASCADE
);

CREATE TABLE Interes (
    nombre_interes VARCHAR(50) PRIMARY KEY
);

CREATE TABLE Expresa (
    nombre_interes VARCHAR(50),
    correo_miembro VARCHAR(255),
    PRIMARY KEY (nombre_interes, correo_miembro),
    FOREIGN KEY (nombre_interes) REFERENCES Interes(nombre_interes) ON UPDATE CASCADE,
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

-- ==========================================
-- OFERTAS DE TRABAJO
-- ==========================================

CREATE TABLE Oferta_Trabajo (
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    descripcion_cargo VARCHAR(255),
    tipo_cargo tipo_cargo_exp,
    fecha_publicacion DATE DEFAULT CURRENT_DATE,
    modalidad modalidad_trabajo,
    ubicacion VARCHAR(255),
    estado_oferta estado_oferta DEFAULT 'abierta',
    PRIMARY KEY (correo_publicador, nombre_cargo),
    FOREIGN KEY (correo_publicador) REFERENCES Organizacion(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Aplica (
    correo_aplicante VARCHAR(255),
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    nombre_archivo VARCHAR(50),
    fecha_aplicacion DATE DEFAULT CURRENT_DATE,
    archivo_cv BYTEA,
    texto_aplicante TEXT,
    PRIMARY KEY (correo_aplicante, correo_publicador, nombre_cargo),
    FOREIGN KEY (correo_aplicante) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo) ON UPDATE CASCADE
);

CREATE TABLE Guarda (
    correo_persona VARCHAR(255),
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    PRIMARY KEY (correo_persona, correo_publicador, nombre_cargo),
    FOREIGN KEY (correo_persona) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo) ON UPDATE CASCADE
);

CREATE TABLE Etiqueta (
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    nombre_carrera VARCHAR(255),
    PRIMARY KEY (correo_publicador, nombre_cargo, nombre_carrera),
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo) ON UPDATE CASCADE,
    FOREIGN KEY (nombre_carrera) REFERENCES Carrera(nombre_carrera) ON UPDATE CASCADE
);

-- ==========================================
-- RED SOCIAL Y COMUNICACIÓN
-- ==========================================

CREATE TYPE estado_amistad AS ENUM ('pendiente', 'aceptada', 'rechazada');
CREATE TYPE rol_grupo AS ENUM ('administrador', 'moderador', 'participante');
CREATE TYPE tipo_grupo AS ENUM ('Publico', 'Privado', 'Secreto');
CREATE TYPE estado_msg AS ENUM ('no_recibido', 'leido', 'recibido');
CREATE TYPE modalidad_evento AS ENUM ('virtual', 'presencial');
CREATE TYPE estado_evento AS ENUM ('borrador', 'publicado', 'en curso', 'finalizado', 'archivado');
CREATE TYPE extension_multimedia AS ENUM ('mp4', 'jpeg', 'jpg', 'png', 'pdf');
CREATE TYPE extension_publicacion AS ENUM ('mp4', 'jpeg', 'jpg', 'png');

CREATE TABLE Es_Amigo (
    correo_persona1 VARCHAR(255),
    correo_persona2 VARCHAR(255),
    estado estado_amistad DEFAULT 'pendiente',
    fecha_solicitud DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (correo_persona1, correo_persona2),
    FOREIGN KEY (correo_persona1) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_persona2) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Sigue (
    correo_seguidor VARCHAR(255),
    correo_seguido VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_seguidor, correo_seguido),
    FOREIGN KEY (correo_seguidor) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_seguido) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Valida (
    correo_validador VARCHAR(255),
    correo_validado VARCHAR(255),
    descripcion_relacion TEXT,
    PRIMARY KEY (correo_validador, correo_validado),
    FOREIGN KEY (correo_validador) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_validado) REFERENCES Persona(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Grupo (
    nombre_grupo VARCHAR(255) PRIMARY KEY,
    descripcion VARCHAR(255),
    tipo_grupo tipo_grupo,
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    correo_creador VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_creador) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Pertenece (
    nombre_grupo VARCHAR(255),
    correo_miembro VARCHAR(255),
    rol_en_grupo rol_grupo DEFAULT 'participante',
    fecha_ingreso DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (nombre_grupo, correo_miembro),
    FOREIGN KEY (nombre_grupo) REFERENCES Grupo(nombre_grupo) ON UPDATE CASCADE,
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Publicacion (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    id_grupo VARCHAR(255),
    texto_pub VARCHAR(280),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    total_likes INTEGER DEFAULT 0,
    total_comen INTEGER DEFAULT 0,
    PRIMARY KEY (id_publicacion, correo_autor),
    FOREIGN KEY (correo_autor) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (id_grupo) REFERENCES Grupo(nombre_grupo) ON UPDATE CASCADE
);

CREATE TABLE Archivo_Publicacion (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    nombre_archivo VARCHAR(50),
    formato_archivo extension_publicacion,
    archivo BYTEA,
    PRIMARY KEY (id_publicacion, correo_autor, nombre_archivo, formato_archivo),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor) ON UPDATE CASCADE
);

CREATE TABLE Trata_Sobre (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    nombre_interes VARCHAR(50),
    PRIMARY KEY (id_publicacion, correo_autor, nombre_interes),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor) ON UPDATE CASCADE,
    FOREIGN KEY (nombre_interes) REFERENCES Interes(nombre_interes) ON UPDATE CASCADE
);

CREATE TABLE Me_Gusta (
    id_publicacion VARCHAR(50),
    correo_autor_pub VARCHAR(255),
    correo_miembro VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_publicacion, correo_autor_pub, correo_miembro),
    FOREIGN KEY (id_publicacion, correo_autor_pub) REFERENCES Publicacion(id_publicacion, correo_autor) ON UPDATE CASCADE,
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Comenta (
    id_comentario VARCHAR(50),
    id_publicacion VARCHAR(50),
    correo_autor_pub VARCHAR(255),
    correo_miembro VARCHAR(255),
    texto_comentario TEXT,
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_comentario, id_publicacion, correo_autor_pub, correo_miembro),
    FOREIGN KEY (id_publicacion, correo_autor_pub) REFERENCES Publicacion(id_publicacion, correo_autor) ON UPDATE CASCADE,
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Mensaje (
    id_mensaje VARCHAR(50),
    correo_emisor VARCHAR(255),
    correo_receptor VARCHAR(255),
    texto VARCHAR(280),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    estado_mensaje estado_msg DEFAULT 'no_recibido',
    PRIMARY KEY (id_mensaje, correo_emisor, correo_receptor),
    FOREIGN KEY (correo_emisor) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (correo_receptor) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Archivo_Mensaje (
    id_mensaje VARCHAR(50),
    correo_emisor VARCHAR(255),
    correo_receptor VARCHAR(255),
    nombre_archivo VARCHAR(255),
    archivo BYTEA,
    formato extension_multimedia,
    PRIMARY KEY (nombre_archivo, id_mensaje, correo_emisor, correo_receptor, formato),
    FOREIGN KEY (id_mensaje, correo_emisor, correo_receptor) 
    REFERENCES Mensaje(id_mensaje, correo_emisor, correo_receptor) ON UPDATE CASCADE
);

CREATE TABLE Encuesta (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    fecha_hora_fin TIMESTAMPTZ,
    PRIMARY KEY (id_publicacion, correo_autor),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Opcion (
    id_publicacion VARCHAR(50), 
    correo_autor VARCHAR(255),
    texto_opcion VARCHAR(100),
    total_votos INTEGER DEFAULT 0,
    PRIMARY KEY (id_publicacion, correo_autor, texto_opcion),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Encuesta(id_publicacion, correo_autor) ON UPDATE CASCADE
);

CREATE TABLE Vota (
    correo_miembro VARCHAR(255),
    id_publicacion VARCHAR(50),
    correo_autor_encuesta VARCHAR(255),
    texto_opcion VARCHAR(100),
    fecha_voto TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, id_publicacion, correo_autor_encuesta, texto_opcion),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (id_publicacion, correo_autor_encuesta, texto_opcion) 
    REFERENCES Opcion(id_publicacion, correo_autor, texto_opcion) ON UPDATE CASCADE
);

-- ==========================================
-- EVENTOS
-- ==========================================

CREATE TABLE Evento (
    nombre_evento VARCHAR(255) PRIMARY KEY,
    correo_organizador VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    modalidad modalidad_evento,
    fecha_inicio TIMESTAMPTZ,
    fecha_fin TIMESTAMPTZ,
    ubicacion VARCHAR(255),
    estado_evento estado_evento,
    url_conferencia VARCHAR(255),
    FOREIGN KEY (correo_organizador) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE
);

CREATE TABLE Asiste (
    correo_miembro VARCHAR(255),
    nombre_evento VARCHAR(255),
    fecha_confirmacion TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, nombre_evento),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (nombre_evento) REFERENCES Evento(nombre_evento) ON UPDATE CASCADE
);

CREATE TABLE Muestra_Interes (
    correo_miembro VARCHAR(255),
    nombre_evento VARCHAR(255),
    fecha_interes TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, nombre_evento),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico) ON UPDATE CASCADE,
    FOREIGN KEY (nombre_evento) REFERENCES Evento(nombre_evento) ON UPDATE CASCADE
);

ALTER TABLE Miembro ENABLE ROW LEVEL SECURITY;
ALTER TABLE Mensaje ENABLE ROW LEVEL SECURITY;
ALTER TABLE Publicacion ENABLE ROW LEVEL SECURITY;
ALTER TABLE Grupo ENABLE ROW LEVEL SECURITY;

-- Lectura: Pública
CREATE POLICY "Ver perfiles" ON Miembro FOR SELECT
USING (true);

-- Solo el propio usuario o el administrador
CREATE POLICY "Editar propio perfil" ON Miembro FOR UPDATE
USING (
    correo_electronico = get_current_user_email() 
    OR 
    EXISTS (SELECT 1 FROM Miembro WHERE correo_electronico = get_current_user_email() AND rol_acceso = 'superadmin')
);

-- Solo emisor o receptor pueden ver los mensajes
CREATE POLICY "Privacidad de mensajes" ON Mensaje
USING (
    correo_emisor = get_current_user_email() 
    OR 
    correo_receptor = get_current_user_email()
);

-- Lectura: Todos pueden leer publicaciones que no pertenecen a un grupo
CREATE POLICY "Leer publicaciones" ON Publicacion FOR SELECT
USING (
    id_grupo IS NULL
    OR
    EXISTS (
        SELECT 1 FROM Pertenece 
        WHERE Pertenece.nombre_grupo = Publicacion.id_grupo 
        AND Pertenece.correo_miembro = get_current_user_email()
    )   
    OR 
    EXISTS (
        SELECT 1 FROM Grupo
        WHERE Grupo.nombre_grupo = Publicacion.id_grupo
        AND Grupo.tipo_grupo = 'Publico'
    )
    OR 
    EXISTS (SELECT 1 FROM Miembro WHERE correo_electronico = get_current_user_email() AND rol_acceso = 'superadmin')
);

-- Insertar: Cualquier usuario registrado
CREATE POLICY "Crear publicacion" ON Publicacion FOR INSERT 
WITH CHECK (correo_autor = get_current_user_email());

-- Borrar/Editar: El dueño de la publicación O un Moderador/Superadmin del sistema
CREATE POLICY "Modificar publicacion" ON Publicacion FOR DELETE
USING (
    correo_autor = get_current_user_email()
    OR
    EXISTS (
        SELECT 1 FROM Miembro 
        WHERE correo_electronico = get_current_user_email() 
        AND rol_acceso IN ('superadmin', 'moderador')
    )
);




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




-- KENETH FUNG
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



--Trigger

CREATE OR REPLACE FUNCTION notificar_nuevo_comentario()
RETURNS TRIGGER AS $$
DECLARE
    v_autor_publicacion VARCHAR(255); -- Para almacenar el correo del autor
    v_texto_notificacion VARCHAR(255);
    v_id_notificacion VARCHAR(50);
BEGIN
    -- 1. Obtener el correo del autor de la publicación (correo_autor)
    -- Se usa id_publicacion y correo_autor_pub para la FK en Comenta
    SELECT P.correo_autor
    INTO v_autor_publicacion
    FROM Publicacion P
    WHERE P.id_publicacion = NEW.id_publicacion AND P.correo_autor = NEW.correo_autor_pub;

    -- Si el autor de la publicación es el mismo que el que comenta, no se notifica (opcional, pero buena práctica)
    IF v_autor_publicacion = NEW.correo_miembro THEN
        RETURN NEW;
    END IF;

    -- 2. Construir el texto de la notificación
    v_texto_notificacion := 'Tu publicación ha sido comentada por: ' || NEW.correo_miembro;

    -- 3. Generar un ID para la notificación usando el timestamp y un valor aleatorio para mayor unicidad
    v_id_notificacion := CAST(EXTRACT(EPOCH FROM NOW()) AS VARCHAR(20)) || LPAD(CAST(FLOOR(RANDOM() * 1000) AS VARCHAR(3)), 3, '0');

    -- 4. Insertar la nueva notificación en la tabla Notificacion
    INSERT INTO Notificacion (
        id_notificacion, 
        correo_destinatario, 
        texto_notificacion, 
        fecha_hora, 
        leida
    )
    VALUES (
        v_id_notificacion,
        v_autor_publicacion, -- Destinatario: Autor de la publicación
        v_texto_notificacion,
        NOW(),
        FALSE
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_comenta_notificacion
AFTER INSERT ON Comenta
FOR EACH ROW
EXECUTE FUNCTION notificar_nuevo_comentario();











--Store Procedure

CREATE OR REPLACE PROCEDURE manejar_miembro_grupo(
    p_nombre_grupo VARCHAR(255),
    p_correo_miembro VARCHAR(255),
    p_accion VARCHAR(10), -- 'ANADIR' o 'ELIMINAR'
    -- El tipo rol_grupo debe ser referenciado como string 'rol_grupo' o VARCHAR/TEXT en los parámetros de entrada.
    p_rol_en_grupo rol_grupo DEFAULT 'participante'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_conteo_admins INTEGER;
    -- Se usa el tipo ENUM directamente, ya que está definido en el esquema.
    v_rol_a_eliminar rol_grupo;
BEGIN
    -- Verificar si el grupo existe
    IF NOT EXISTS (SELECT 1 FROM Grupo WHERE nombre_grupo = p_nombre_grupo) THEN
        RAISE EXCEPTION 'Error: El grupo % no existe.', p_nombre_grupo;
    END IF;

    -- Verificar si el miembro existe
    IF NOT EXISTS (SELECT 1 FROM Miembro WHERE correo_electronico = p_correo_miembro) THEN
        RAISE EXCEPTION 'Error: El miembro % no existe.', p_correo_miembro;
    END IF;

    IF UPPER(p_accion) = 'ANADIR' THEN
        -- Corregidos nombres de columnas (nombre_grupo, correo_miembro)
        INSERT INTO Pertenece (nombre_grupo, correo_miembro, rol_en_grupo, fecha_ingreso)
        VALUES (p_nombre_grupo, p_correo_miembro, p_rol_en_grupo, CURRENT_DATE)
        -- Corregidos nombres de columnas en ON CONFLICT
        ON CONFLICT (nombre_grupo, correo_miembro) DO UPDATE
        SET rol_en_grupo = p_rol_en_grupo, fecha_ingreso = CURRENT_DATE;

    ELSIF UPPER(p_accion) = 'ELIMINAR' THEN
        
        -- Corregidos nombres de columnas (nombre_grupo, correo_miembro)
        SELECT rol_en_grupo INTO v_rol_a_eliminar
        FROM Pertenece
        WHERE nombre_grupo = p_nombre_grupo AND correo_miembro = p_correo_miembro;

        IF v_rol_a_eliminar IS NULL THEN
            RAISE EXCEPTION 'El miembro % no pertenece al grupo %.', p_correo_miembro, p_nombre_grupo;
        END IF;

        -- Restricción: 'Todo grupo debe tener al menos un administrador'
        IF v_rol_a_eliminar = 'administrador' THEN
            SELECT COUNT(*) INTO v_conteo_admins
            FROM Pertenece
            WHERE 
                nombre_grupo = p_nombre_grupo AND 
                rol_en_grupo = 'administrador' AND
                correo_miembro != p_correo_miembro; -- Contar otros administradores

            IF v_conteo_admins < 1 THEN
                RAISE EXCEPTION 'Restricción de Grupo: No se puede eliminar al último administrador del grupo %.', p_nombre_grupo;
            END IF;
        END IF;
        
        -- Corregidos nombres de columnas
        DELETE FROM Pertenece
        WHERE nombre_grupo = p_nombre_grupo AND correo_miembro = p_correo_miembro;

    ELSE
        RAISE EXCEPTION 'Acción inválida: %. Debe ser ''ANADIR'' o ''ELIMINAR''.', p_accion;
    END IF;

    -- Se elimina el COMMIT explícito, ya que PostgreSQL maneja las transacciones automáticamente en los procedimientos.
END;
$$;





-- VALENTINA
--FUNCTION

CREATE OR REPLACE FUNCTION recomendar_ofertas(
    p_correo_persona VARCHAR(255)
)
RETURNS TABLE (
    nombre_cargo VARCHAR(255),
    correo_publicador VARCHAR(255),
    nombre_organizacion VARCHAR(255),
    coincidencia_carreras BIGINT,
    fecha_publicacion DATE
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    WITH CarrerasDelMiembro AS (
        -- 1. Obtener todas las carreras que la persona ha estudiado (a través de Periodo_Educativo)
        SELECT 
            id_carrera
        FROM 
            Periodo P
        JOIN 
            Periodo_Educativo PE ON P.id_periodo = PE.id_periodo AND P.correo_persona = PE.correo_persona
        WHERE 
            P.correo_persona = p_correo_persona
    )
    SELECT
        OT.nombre_cargo,
        OT.correo_publicador,
        ORG.nombre_organizacion,
        COUNT(DISTINCT E.nombre_carrera) AS coincidencia_carreras,
        OT.fecha_publicacion
    FROM
        Oferta_Trabajo OT
    JOIN
        Organizacion ORG ON OT.correo_publicador = ORG.correo_electronico
    JOIN
        Etiqueta E ON OT.correo_publicador = E.correo_publicador AND OT.nombre_cargo = E.nombre_cargo
    JOIN
        CarrerasDelMiembro CDM ON E.nombre_carrera = CDM.id_carrera
    WHERE
        -- 2. Asegurar que la oferta esté activa
        OT.estado_oferta = 'abierta'
        -- 3. Excluir ofertas a las que ya ha aplicado (Opcional, pero útil)
        AND NOT EXISTS (
            SELECT 1
            FROM Aplica A
            WHERE 
                A.correo_aplicante = p_correo_persona
                AND A.correo_publicador = OT.correo_publicador
                AND A.nombre_cargo = OT.nombre_cargo
        )
    GROUP BY
        OT.nombre_cargo,
        OT.correo_publicador,
        ORG.nombre_organizacion,
        OT.fecha_publicacion
    ORDER BY
        coincidencia_carreras DESC,  -- Priorizar las ofertas con más carreras coincidentes
        OT.fecha_publicacion DESC   -- Luego, las más recientes
    LIMIT 15; -- Mostrar un máximo de 15 recomendaciones
END;
$$;


--USO FUNCTION

--SELECT * FROM recomendar_ofertas('persona@ucab.edu.ve');




--CONSTRAINT 

-- para todo periodo, debe existir un periodo_educativo o periodo_experiencia, pero no ambos

CREATE OR REPLACE FUNCTION verificar_tipo_periodo_unico()
RETURNS TRIGGER AS $$
DECLARE
    -- Contadores para verificar la existencia en ambas tablas.
    v_conteo_educativo INTEGER;
    v_conteo_experiencia INTEGER;
BEGIN
    -- Contar cuántas veces la clave primaria compuesta existe en Periodo_Educativo
    SELECT COUNT(*)
    INTO v_conteo_educativo
    FROM Periodo_Educativo
    WHERE id_periodo = NEW.id_periodo AND correo_persona = NEW.correo_persona;

    -- Contar cuántas veces la clave primaria compuesta existe en Periodo_Experiencia
    SELECT COUNT(*)
    INTO v_conteo_experiencia
    FROM Periodo_Experiencia
    WHERE id_periodo = NEW.id_periodo AND correo_persona = NEW.correo_persona;

    -- La suma total de referencias debe ser exactamente 1. 
    -- Si es 2, significa que existe en ambas tablas, lo cual viola la exclusividad.
    IF (v_conteo_educativo + v_conteo_experiencia) > 1 THEN
        RAISE EXCEPTION 'Restricción de Período Violada: El período con id %s del miembro %s ya está registrado como el otro tipo de período (Educativo o Experiencia). Solo puede ser uno de los dos.', 
            NEW.id_periodo, NEW.correo_persona;
    END IF;

    -- Si la suma es 1 (solo existe en la tabla que disparó el trigger), la operación es válida.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplica la restricción a Periodo_Educativo
CREATE CONSTRAINT TRIGGER tr_periodo_educativo_unico
AFTER INSERT OR UPDATE ON Periodo_Educativo
DEFERRABLE INITIALLY DEFERRED -- Crucial para manejar transacciones complejas
FOR EACH ROW
EXECUTE FUNCTION verificar_tipo_periodo_unico();

-- Aplica la restricción a Periodo_Experiencia
CREATE CONSTRAINT TRIGGER tr_periodo_experiencia_unico
AFTER INSERT OR UPDATE ON Periodo_Experiencia
DEFERRABLE INITIALLY DEFERRED -- Crucial para manejar transacciones complejas
FOR EACH ROW
EXECUTE FUNCTION verificar_tipo_periodo_unico();

--Fecha Inicio menor que fecha fin

ALTER TABLE Periodo
ADD CONSTRAINT check_fecha_inicio_menor_que_fin
CHECK (
    fecha_inicio <= fecha_fin
    -- Se permite fecha_fin NULL (periodo en curso)
    OR fecha_fin IS NULL
);


--numeros no negativos 
ALTER TABLE Publicacion
ADD CONSTRAINT check_contadores_no_negativos
CHECK (
    total_likes >= 0 AND total_comen >= 0
);
