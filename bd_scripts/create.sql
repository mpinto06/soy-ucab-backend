-- CREACIÓN DE ROLES
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

CREATE TYPE privacidad_msg AS ENUM ('cualquiera', 'solo_amigos', 'nadie');
CREATE TYPE tipo_sexo AS ENUM ('M', 'F');
CREATE TYPE nivel_carrera AS ENUM ('pregrado', 'posgrado');
CREATE TYPE tipo_entidad AS ENUM ('facultad', 'escuela', 'libre');
CREATE TYPE extension_imagen AS ENUM ('jpg', 'jpeg', 'png');
CREATE TYPE extension_certificado AS ENUM ('jpg', 'jpeg', 'png', 'pdf');
CREATE TYPE extension_carta AS ENUM ('doc', 'docx', 'pdf');
CREATE TYPE rol_sistema AS ENUM ('superadmin', 'moderador', 'usuario_estandar');

-- FINO
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

-- FINO
CREATE TABLE Persona (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    primer_nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50),
    primer_apellido VARCHAR(50) NOT NULL,
    segundo_apellido VARCHAR(50),
    fecha_nacimiento DATE NOT NULL,
    sexo tipo_sexo NOT NULL,
    ubicacion_geografica VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Miembro(correo_electronico)
);

-- FINO
CREATE TABLE Organizacion (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    nombre_organizacion VARCHAR(255) UNIQUE NOT NULL,   
    descripcion_org VARCHAR(255),
    FOREIGN KEY (correo_electronico) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Dependencia_UCAB (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    tipo_entidad_institucional tipo_entidad NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Organizacion(correo_electronico)
);

--FINO
CREATE TABLE Organizacion_Asociada (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    RIF VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Organizacion(correo_electronico)
);

--FINO
CREATE TABLE Facultad (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (correo_electronico) REFERENCES Dependencia_UCAB(correo_electronico)
);

--FINO
CREATE TABLE Escuela (
    correo_electronico VARCHAR(255) PRIMARY KEY,
    correo_facultad VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Dependencia_UCAB(correo_electronico),
    FOREIGN KEY (correo_facultad) REFERENCES Facultad(correo_electronico)
);

-- FINO
CREATE TABLE Carrera (
    nombre_carrera VARCHAR(255) PRIMARY KEY,
    nivel_carrera nivel_carrera NOT NULL,
    correo_electronico VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_electronico) REFERENCES Escuela(correo_electronico)
);

--FINO
CREATE TABLE Notificacion (
    id_notificacion VARCHAR(50),
    correo_destinatario VARCHAR(255),
    texto_notificacion VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id_notificacion, correo_destinatario),
    FOREIGN KEY (correo_destinatario) REFERENCES Miembro(correo_electronico)
);

CREATE TYPE tipo_cargo_exp AS ENUM ('Jornada Completa', 'Jornada Parcial', 'Contrato', 'Pasantía', 'Voluntariado', 'Miembro');
CREATE TYPE modalidad_trabajo AS ENUM ('Remoto', 'Semi-Presencial', 'Presencial');
CREATE TYPE estado_oferta AS ENUM ('abierta', 'cerrada');

--FINO
CREATE TABLE Periodo (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    descripcion_periodo TEXT,
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (correo_persona) REFERENCES Persona(correo_electronico),
    CHECK (fecha_inicio <> fecha_fin)
);

--FINO
CREATE TABLE Periodo_Educativo (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    id_carrera VARCHAR(255),
    archivo_certificado BYTEA,
    formato_archivo extension_carta,
    nombre_estudio VARCHAR(255),
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona),
    FOREIGN KEY (id_carrera) REFERENCES Carrera(nombre_carrera)
);

-- FINO
CREATE TABLE Periodo_Experiencia (
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    correo_organizacion VARCHAR(255) NOT NULL,
    tipo_cargo tipo_cargo_exp NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    archivo_carta BYTEA,
    formato_archivo extension_certificado,
    PRIMARY KEY (id_periodo, correo_persona),
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona),
    FOREIGN KEY (correo_organizacion) REFERENCES Organizacion(correo_electronico)
);

-- FINO
CREATE TABLE Habilidad (
    nombre_habilidad VARCHAR(50) PRIMARY KEY
);

-- FINO
CREATE TABLE Demuestra (
    nombre_habilidad VARCHAR(50),
    id_periodo VARCHAR(50),
    correo_persona VARCHAR(255),
    PRIMARY KEY (nombre_habilidad, id_periodo, correo_persona),
    FOREIGN KEY (nombre_habilidad) REFERENCES Habilidad(nombre_habilidad),
    FOREIGN KEY (id_periodo, correo_persona) REFERENCES Periodo(id_periodo, correo_persona)
);

-- FINO
CREATE TABLE Interes (
    nombre_interes VARCHAR(50) PRIMARY KEY
);

--FINO
CREATE TABLE Expresa (
    nombre_interes VARCHAR(50),
    correo_miembro VARCHAR(255),
    PRIMARY KEY (nombre_interes, correo_miembro),
    FOREIGN KEY (nombre_interes) REFERENCES Interes(nombre_interes),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico)
);

--FINO
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
    FOREIGN KEY (correo_publicador) REFERENCES Organizacion(correo_electronico)
);

--FINO
CREATE TABLE Aplica (
    correo_aplicante VARCHAR(255),
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    fecha_aplicacion DATE DEFAULT CURRENT_DATE,
    nombre_archivo VARCHAR(50),
    archivo_cv BYTEA,
    texto_aplicante TEXT,
    PRIMARY KEY (correo_aplicante, correo_publicador, nombre_cargo),
    FOREIGN KEY (correo_aplicante) REFERENCES Persona(correo_electronico),
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo)
);

--FINO
CREATE TABLE Guarda (
    correo_persona VARCHAR(255),
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    PRIMARY KEY (correo_persona, correo_publicador, nombre_cargo),
    FOREIGN KEY (correo_persona) REFERENCES Persona(correo_electronico),
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo)
);

--FINO
CREATE TABLE Etiqueta (
    correo_publicador VARCHAR(255),
    nombre_cargo VARCHAR(255),
    nombre_carrera VARCHAR(255),
    PRIMARY KEY (correo_publicador, nombre_cargo, nombre_carrera),
    FOREIGN KEY (correo_publicador, nombre_cargo) REFERENCES Oferta_Trabajo(correo_publicador, nombre_cargo),
    FOREIGN KEY (nombre_carrera) REFERENCES Carrera(nombre_carrera)
);


-- Entidades Sociales (Miguel)
CREATE TYPE estado_amistad AS ENUM ('pendiente', 'aceptada', 'rechazada');
CREATE TYPE rol_grupo AS ENUM ('administrador', 'moderador', 'participante');
CREATE TYPE tipo_grupo AS ENUM ('Publico', 'Privado', 'Secreto');
CREATE TYPE estado_msg AS ENUM ('no_recibido', 'leido', 'recibido');
CREATE TYPE modalidad_evento AS ENUM ('virtual', 'presencial');
CREATE TYPE estado_evento AS ENUM ('borrador', 'publicado', 'en curso', 'finalizado', 'archivado');
CREATE TYPE extension_multimedia AS ENUM ('mp4', 'jpeg', 'jpg', 'png', 'pdf');
CREATE TYPE extension_publicacion AS ENUM ('mp4', 'jpeg', 'jpg', 'png');

--FINO
CREATE TABLE Es_Amigo (
    correo_persona1 VARCHAR(255),
    correo_persona2 VARCHAR(255),
    estado estado_amistad DEFAULT 'pendiente',
    fecha_solicitud DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (correo_persona1, correo_persona2),
    FOREIGN KEY (correo_persona1) REFERENCES Persona(correo_electronico),
    FOREIGN KEY (correo_persona2) REFERENCES Persona(correo_electronico)
);

--FINO
CREATE TABLE Sigue (
    correo_seguidor VARCHAR(255),
    correo_seguido VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_seguidor, correo_seguido),
    FOREIGN KEY (correo_seguidor) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (correo_seguido) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Valida (
    correo_validador VARCHAR(255),
    correo_validado VARCHAR(255),
    descripcion_relacion TEXT,
    PRIMARY KEY (correo_validador, correo_validado),
    FOREIGN KEY (correo_validador) REFERENCES Persona(correo_electronico),
    FOREIGN KEY (correo_validado) REFERENCES Persona(correo_electronico)
);

--FINO
CREATE TABLE Grupo (
    nombre_grupo VARCHAR(255) PRIMARY KEY,
    descripcion VARCHAR(255),
    tipo_grupo tipo_grupo,
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    correo_creador VARCHAR(255) NOT NULL,
    FOREIGN KEY (correo_creador) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Pertenece (
    nombre_grupo VARCHAR(255),
    correo_miembro VARCHAR(255),
    rol_en_grupo rol_grupo DEFAULT 'participante',
    fecha_ingreso DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (nombre_grupo, correo_miembro),
    FOREIGN KEY (nombre_grupo) REFERENCES Grupo(nombre_grupo),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Publicacion (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    id_grupo VARCHAR(255),
    texto_pub VARCHAR(280),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    total_likes INTEGER DEFAULT 0,
    total_comen INTEGER DEFAULT 0,
    PRIMARY KEY (id_publicacion, correo_autor),
    FOREIGN KEY (correo_autor) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (id_grupo) REFERENCES Grupo(nombre_grupo)
);

--FINO
CREATE TABLE Archivo_Publicacion (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    nombre_archivo VARCHAR(50),
    formato_archivo extension_publicacion,
    archivo BYTEA,
    PRIMARY KEY (id_publicacion, correo_autor, nombre_archivo, formato_archivo),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor)
);

--FINO
--AÑADIR EN ENTREGA 2
CREATE TABLE Trata_Sobre (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    nombre_interes VARCHAR(50),
    PRIMARY KEY (id_publicacion, correo_autor, nombre_interes),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor),
    FOREIGN KEY (nombre_interes) REFERENCES Interes(nombre_interes)
);

--FINO
CREATE TABLE Me_Gusta (
    id_publicacion VARCHAR(50),
    correo_autor_pub VARCHAR(255),
    correo_miembro VARCHAR(255),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_publicacion, correo_autor_pub, correo_miembro),
    FOREIGN KEY (id_publicacion, correo_autor_pub) REFERENCES Publicacion(id_publicacion, correo_autor),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico)
);

--FINO
--AÑADIR EN ENTREGA 2 MODIFICACION
CREATE TABLE Comenta (
    id_comentario VARCHAR(50),
    id_publicacion VARCHAR(50),
    correo_autor_pub VARCHAR(255),
    correo_miembro VARCHAR(255),
    texto_comentario TEXT,
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_comentario, id_publicacion, correo_autor_pub, correo_miembro),
    FOREIGN KEY (id_publicacion, correo_autor_pub) REFERENCES Publicacion(id_publicacion, correo_autor),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Mensaje (
    id_mensaje VARCHAR(50),
    correo_emisor VARCHAR(255),
    correo_receptor VARCHAR(255),
    texto VARCHAR(280),
    fecha_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    estado_mensaje estado_msg DEFAULT 'no_recibido',
    PRIMARY KEY (id_mensaje, correo_emisor, correo_receptor),
    FOREIGN KEY (correo_emisor) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (correo_receptor) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Archivo_Mensaje (
    id_mensaje VARCHAR(50),
    correo_emisor VARCHAR(255),
    correo_receptor VARCHAR(255),
    nombre_archivo VARCHAR(255),
    archivo BYTEA,
    formato extension_multimedia,
    PRIMARY KEY (nombre_archivo, id_mensaje, correo_emisor, correo_receptor, formato),
    FOREIGN KEY (id_mensaje, correo_emisor, correo_receptor) 
    REFERENCES Mensaje(id_mensaje, correo_emisor, correo_receptor)
);

--FINO
CREATE TABLE Encuesta (
    id_publicacion VARCHAR(50),
    correo_autor VARCHAR(255),
    fecha_hora_fin TIMESTAMPTZ,
    PRIMARY KEY (id_publicacion, correo_autor),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Publicacion(id_publicacion, correo_autor) ON DELETE CASCADE
);

--FINO
CREATE TABLE Opcion (
    id_publicacion VARCHAR(50), 
    correo_autor VARCHAR(255),
    texto_opcion VARCHAR(100),
    total_votos INTEGER DEFAULT 0,
    PRIMARY KEY (id_publicacion, correo_autor, texto_opcion),
    FOREIGN KEY (id_publicacion, correo_autor) REFERENCES Encuesta(id_publicacion, correo_autor)
);

--FINO
--SE QUITA ID DE ENCUESTA PQ ES SUFICIENTE CON OPCION, MODIFICAR EN ENTREGA 2
CREATE TABLE Vota (
    correo_miembro VARCHAR(255),
    id_publicacion VARCHAR(50),
    correo_autor_encuesta VARCHAR(255),
    texto_opcion VARCHAR(100),
    fecha_voto TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, id_publicacion, correo_autor_encuesta, texto_opcion),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (id_publicacion, correo_autor_encuesta, texto_opcion) 
    REFERENCES Opcion(id_publicacion, correo_autor, texto_opcion)
);

--FINO
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
    FOREIGN KEY (correo_organizador) REFERENCES Miembro(correo_electronico)
);

--FINO
CREATE TABLE Asiste (
    correo_miembro VARCHAR(255),
    nombre_evento VARCHAR(255),
    fecha_confirmacion TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, nombre_evento),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (nombre_evento) REFERENCES Evento(nombre_evento)
);
--FINO
CREATE TABLE Muestra_Interes (
    correo_miembro VARCHAR(255),
    nombre_evento VARCHAR(255),
    fecha_interes TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correo_miembro, nombre_evento),
    FOREIGN KEY (correo_miembro) REFERENCES Miembro(correo_electronico),
    FOREIGN KEY (nombre_evento) REFERENCES Evento(nombre_evento)
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
