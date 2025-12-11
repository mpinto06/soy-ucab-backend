-- USUARIOS BASE (KENETH)

-- Miembros
INSERT INTO Miembro (correo_electronico, hash_contrasena, encabezado_perfil, privacidad_mensajes) VALUES
('miguel@ucab.edu.ve', 'hash123', 'Estudiante de Ingeniería', 'cualquiera'),
('maria@ucab.edu.ve', 'hash456', 'Delegada de curso', 'solo_amigos'),
('ingenieria@ucab.edu.ve', 'hash789', 'Facultad de Ingeniería', 'nadie'),
('informatica@ucab.edu.ve', 'hashabc', 'Escuela de Informática', 'cualquiera'),
('civil@ucab.edu.ve', 'hash_civil', 'Escuela de Ingeniería Civil', 'cualquiera'),
('google@empresa.com', 'hashxyz', 'Tech Company', 'cualquiera');

-- Personas
INSERT INTO Persona (correo_electronico, primer_nombre, primer_apellido, fecha_nacimiento, sexo, ubicacion_geografica) VALUES
('miguel@ucab.edu.ve', 'Miguel', 'Pinto', '2004-06-01', 'M', 'Caracas'),
('maria@ucab.edu.ve', 'Maria', 'Perez', '2003-05-15', 'F', 'Caracas');

-- Organizaciones
INSERT INTO Organizacion (correo_electronico, nombre_organizacion, descripcion_org) VALUES
('ingenieria@ucab.edu.ve', 'Facultad de Ingeniería', 'Decanato de Ingeniería'),
('informatica@ucab.edu.ve', 'Escuela de Ingeniería Informática', 'Escuela de la carrera de informática'),
('civil@ucab.edu.ve', 'Escuela de Ingeniería Civil', 'Escuela de la carrera de civil'),
('google@empresa.com', 'Google Venezuela', 'Empresa de tecnología');

-- Dependencias y Organizaciones Asociadas
INSERT INTO Dependencia_UCAB (correo_electronico, tipo_entidad_institucional) VALUES
('ingenieria@ucab.edu.ve', 'facultad'),
('civil@ucab.edu.ve', 'escuela'),
('informatica@ucab.edu.ve', 'escuela');

INSERT INTO Organizacion_Asociada (correo_electronico, RIF) VALUES
('google@empresa.com', 'J-123456789');

-- Facultades y Escuelas
INSERT INTO Facultad (correo_electronico) VALUES
('ingenieria@ucab.edu.ve');

INSERT INTO Escuela (correo_electronico, correo_facultad) VALUES
('civil@ucab.edu.ve', 'ingenieria@ucab.edu.ve'),
('informatica@ucab.edu.ve', 'ingenieria@ucab.edu.ve');

-- Carreras
INSERT INTO Carrera (nombre_carrera, nivel_carrera, correo_electronico) VALUES
('Ingeniería Informática', 'pregrado', 'informatica@ucab.edu.ve'),
('Ingeniería Civil', 'pregrado', 'civil@ucab.edu.ve'),
('Maestría en Ingeniería Informática', 'posgrado', 'informatica@ucab.edu.ve'),
('Maestría en Ingeniería Civil', 'posgrado', 'civil@ucab.edu.ve');

-- Notificaciones
INSERT INTO Notificacion (id_notificacion, correo_destinatario, texto_notificacion, fecha_hora) VALUES
('2026-01-01 09:00:02', 'miguel@ucab.edu.ve', 'María comentó tu publicación', '2026-01-01 09:00:02'),
('2026-01-01 09:00:03', 'miguel@ucab.edu.ve', 'Google ha publicado una nueva oferta', '2026-01-01 09:00:03'),
('2026-01-01 09:00:04', 'miguel@ucab.edu.ve', 'Tu solicitud de amistad fue aceptada', '2026-01-01 09:00:04'),
('2026-01-01 09:00:05', 'miguel@ucab.edu.ve', 'Bienvenido al grupo de Desarrollo', '2026-01-01 09:00:05');


-- Perfil Profesional y Educativo (Valentina)

-- Periodos
-- (Para usuario miguel)
INSERT INTO Periodo (id_periodo, correo_persona, fecha_inicio, fecha_fin, descripcion_periodo) VALUES
('2021-09-15 08:30:00', 'miguel@ucab.edu.ve', '2021-09-15', '2025-07-30', 'Estudios de Pregrado en Informática'),
('2022-02-15 08:00:00', 'miguel@ucab.edu.ve', '2022-02-15', '2022-07-15', 'Preparaduría de Programación'),
('2023-03-01 10:00:00', 'miguel@ucab.edu.ve', '2023-03-01', '2023-06-01', 'Curso de Liderazgo Estudiantil'),
('2024-02-01 11:30:00', 'miguel@ucab.edu.ve', '2024-02-01', NULL, 'Freelance Junior Developer'),
('2025-09-15 18:00:00', 'miguel@ucab.edu.ve', '2025-09-15', NULL, 'Inicio de Especialización');

-- (Para usuario maria)
INSERT INTO Periodo (id_periodo, correo_persona, fecha_inicio, fecha_fin, descripcion_periodo) VALUES
('2021-10-01 08:00:00', 'maria@ucab.edu.ve', '2021-10-01', '2022-07-30', 'Asistente de Investigación'),
('2022-11-01 09:00:00', 'maria@ucab.edu.ve', '2022-11-01', '2026-07-30', 'Estudios de Ingeniería Civil'),
('2023-05-20 13:00:00', 'maria@ucab.edu.ve', '2023-05-20', '2023-12-20', 'Analista de Datos Junior'),
('2024-03-01 16:00:00', 'maria@ucab.edu.ve', '2024-03-01', NULL, 'Coordinadora de Eventos Académicos'),
('2026-09-20 17:00:00', 'maria@ucab.edu.ve', '2026-09-20', NULL, 'Maestría en Estructuras');


-- Periodo Educativo
-- Miguel
INSERT INTO Periodo_Educativo (id_periodo, correo_persona, id_carrera, nombre_estudio) VALUES
('2021-09-15 08:30:00', 'miguel@ucab.edu.ve', 'Ingeniería Informática', NULL),
('2023-03-01 10:00:00', 'miguel@ucab.edu.ve', NULL, 'Diplomado en Liderazgo'),
('2025-09-15 18:00:00', 'miguel@ucab.edu.ve', 'Maestría en Ingeniería Informática', NULL);

-- Maria
INSERT INTO Periodo_Educativo (id_periodo, correo_persona, id_carrera, nombre_estudio) VALUES
('2022-11-01 09:00:00', 'maria@ucab.edu.ve', 'Ingeniería Civil', NULL),
('2026-09-20 17:00:00', 'maria@ucab.edu.ve', 'Maestría en Ingeniería Civil', NULL);


-- Periodo Experiencia
-- Miguel
INSERT INTO Periodo_Experiencia (id_periodo, correo_persona, correo_organizacion, tipo_cargo, cargo) VALUES
('2022-02-15 08:00:00', 'miguel@ucab.edu.ve', 'informatica@ucab.edu.ve', 'Miembro', 'Preparador Académico'),
('2024-02-01 11:30:00', 'miguel@ucab.edu.ve', 'google@empresa.com', 'Contrato', 'Junior Web Developer');

-- Maria
INSERT INTO Periodo_Experiencia (id_periodo, correo_persona, correo_organizacion, tipo_cargo, cargo) VALUES
('2021-10-01 08:00:00', 'maria@ucab.edu.ve', 'civil@ucab.edu.ve', 'Pasantía', 'Asistente de Lab. de Suelos'),
('2023-05-20 13:00:00', 'maria@ucab.edu.ve', 'google@empresa.com', 'Jornada Parcial', 'QA Tester'),
('2024-03-01 16:00:00', 'maria@ucab.edu.ve', 'ingenieria@ucab.edu.ve', 'Voluntariado', 'Líder de Logística');


-- Habilidades e Intereses
INSERT INTO Habilidad (nombre_habilidad) VALUES 
('SQL'), 
('Java'), 
('Python'),
('JavaScript'),
('Git'),
('Liderazgo');

INSERT INTO Interes (nombre_interes) VALUES 
('Data Science'), 
('Web Development'),
('Cloud Computing'),
('Ciberseguridad'),
('Inteligencia Artificial'),
('Gerencia de Proyectos');

-- Demuestra

-- Miguel demuestra 'JavaScript' y 'Git'
INSERT INTO Demuestra (nombre_habilidad, id_periodo, correo_persona) VALUES
('JavaScript', '2024-02-01 11:30:00', 'miguel@ucab.edu.ve'),
('Git', '2024-02-01 11:30:00', 'miguel@ucab.edu.ve');

-- Maria demuestra 'Liderazgo'
INSERT INTO Demuestra (nombre_habilidad, id_periodo, correo_persona) VALUES
('Liderazgo', '2024-03-01 16:00:00', 'maria@ucab.edu.ve');

-- Maria demuestra 'Python'
INSERT INTO Demuestra (nombre_habilidad, id_periodo, correo_persona) VALUES
('Python', '2023-05-20 13:00:00', 'maria@ucab.edu.ve');

-- Expresa

-- Miguel expresa 'Cloud Computing', 'Ciberseguridad' y 'Inteligencia Artificial'
INSERT INTO Expresa (nombre_interes, correo_miembro) VALUES
('Cloud Computing', 'miguel@ucab.edu.ve'),
('Ciberseguridad', 'miguel@ucab.edu.ve'),
('Inteligencia Artificial', 'miguel@ucab.edu.ve');

-- Maria expresa 'Gerencia de Proyectos', 'Data Science' y 'Inteligencia Artificial'
INSERT INTO Expresa (nombre_interes, correo_miembro) VALUES
('Gerencia de Proyectos', 'maria@ucab.edu.ve'),
('Data Science', 'maria@ucab.edu.ve'),
('Inteligencia Artificial', 'maria@ucab.edu.ve');

-- Ofertas de Trabajo
INSERT INTO Oferta_Trabajo (correo_publicador, nombre_cargo, tipo_cargo, modalidad, ubicacion) VALUES
('google@empresa.com', 'SRE Engineer', 'Jornada Completa', 'Presencial', 'Caracas');

-- Aplica
INSERT INTO Aplica (correo_aplicante, correo_publicador, nombre_cargo, nombre_archivo, texto_aplicante) VALUES
('miguel@ucab.edu.ve', 'google@empresa.com', 'SRE Engineer', 'cv_miguel.pdf', 'Interesado en la vacante.');

-- Guarda
INSERT INTO Guarda (correo_persona, correo_publicador, nombre_cargo) 
VALUES ('maria@ucab.edu.ve', 'google@empresa.com', 'SRE Engineer');

-- Etiqueta
INSERT INTO Etiqueta (correo_publicador, nombre_cargo, nombre_carrera) 
VALUES ('google@empresa.com', 'SRE Engineer', 'Ingeniería Informática');


-- Entidades Sociales

-- Es_Amigo
INSERT INTO Es_Amigo (correo_persona1, correo_persona2, estado) VALUES
('miguel@ucab.edu.ve', 'maria@ucab.edu.ve', 'aceptada');

-- Sigue
INSERT INTO Sigue (correo_seguidor, correo_seguido) VALUES
('miguel@ucab.edu.ve', 'informatica@ucab.edu.ve');

-- Valida
INSERT INTO Valida (correo_validador, correo_validado, descripcion_relacion) VALUES
('miguel@ucab.edu.ve', 'maria@ucab.edu.ve', 'Trabajamos juntos en el proyecto de desarrollo web y demostró gran habilidad en SQL.'),
('maria@ucab.edu.ve', 'miguel@ucab.edu.ve', 'Excelente compañero de clases, muy responsable y con gran liderazgo en los grupos.');

-- Grupo
INSERT INTO Grupo (nombre_grupo, descripcion, tipo_grupo, correo_creador) VALUES
('Devs UCAB', 'Comunidad de desarrolladores', 'Publico', 'miguel@ucab.edu.ve');

-- Pertenece
INSERT INTO Pertenece (nombre_grupo, correo_miembro, rol_en_grupo) VALUES
('Devs UCAB', 'miguel@ucab.edu.ve', 'administrador'),
('Devs UCAB', 'maria@ucab.edu.ve', 'participante');

-- Publicacion
INSERT INTO Publicacion (id_publicacion, correo_autor, id_grupo, texto_pub) VALUES
('2025-11-20 16:45:12', 'miguel@ucab.edu.ve', 'Devs UCAB', '¿Alguien sabe cuándo es la entrega?'),
('2025-11-22 10:00:00', 'maria@ucab.edu.ve', NULL, '¡Por fin terminaron los parciales! A descansar un poco.'),  
('2025-11-21 09:00:00', 'informatica@ucab.edu.ve', NULL, 'Encuesta sobre electivas');

-- Archivo Publicacion
INSERT INTO Archivo_Publicacion (id_publicacion, correo_autor, nombre_archivo, formato_archivo, archivo) VALUES 
('2025-11-22 10:00:00', 'maria@ucab.edu.ve', 'celebracion.jpg', 'jpg', '\xFFD8FFE0');

-- Trata sobre
INSERT INTO Trata_Sobre (id_publicacion, correo_autor, nombre_interes) VALUES
('2025-11-22 10:00:00', 'maria@ucab.edu.ve', 'Data Science'),
('2025-11-22 10:00:00', 'maria@ucab.edu.ve', 'Gerencia de Proyectos'),
('2025-11-22 10:00:00', 'maria@ucab.edu.ve', 'Inteligencia Artificial');

-- Me gusta
INSERT INTO Me_Gusta (id_publicacion, correo_autor_pub, correo_miembro) VALUES
('2025-11-20 16:45:12', 'miguel@ucab.edu.ve', 'maria@ucab.edu.ve');

-- Comentario
INSERT INTO Comenta (id_comentario, id_publicacion, correo_autor_pub, correo_miembro, texto_comentario, fecha_hora) VALUES
('2025-11-20 18:30:00', '2025-11-20 16:45:12', 'miguel@ucab.edu.ve', 'maria@ucab.edu.ve', 'Creo que es el viernes', '2025-11-20 18:30:00');

-- Mensaje
INSERT INTO Mensaje (id_mensaje, correo_emisor, correo_receptor, texto, estado_mensaje) VALUES
('2025-12-05 11:11:11', 'maria@ucab.edu.ve', 'miguel@ucab.edu.ve', 'Mira este archivo', 'recibido');

-- Archivo Mensaje
INSERT INTO Archivo_Mensaje (id_mensaje, correo_emisor, correo_receptor, nombre_archivo, formato, archivo) VALUES
('2025-12-05 11:11:11', 'maria@ucab.edu.ve', 'miguel@ucab.edu.ve', 'tarea.pdf', 'pdf', '\x255044462D');

-- Encuesta
INSERT INTO Encuesta (id_publicacion, correo_autor, fecha_hora_fin) VALUES
('2025-11-21 09:00:00', 'informatica@ucab.edu.ve', '2025-12-01 23:59:59-04');

-- Opciones de Encuesta
INSERT INTO Opcion (id_publicacion, correo_autor, texto_opcion, total_votos) VALUES
('2025-11-21 09:00:00', 'informatica@ucab.edu.ve', 'Inteligencia Artificial', 0),
('2025-11-21 09:00:00', 'informatica@ucab.edu.ve', 'Computación Gráfica', 0);

-- Voto
INSERT INTO Vota (correo_miembro, id_publicacion, correo_autor_encuesta, texto_opcion) VALUES
('miguel@ucab.edu.ve', '2025-11-21 09:00:00', 'informatica@ucab.edu.ve', 'Inteligencia Artificial');


-- Eventos
INSERT INTO Evento (nombre_evento, correo_organizador, descripcion, modalidad, estado_evento, ubicacion) VALUES
('Hackathon 2025', 'informatica@ucab.edu.ve', 'Competencia de prog', 'presencial', 'publicado', 'Caracas');

-- Asiste
INSERT INTO Asiste (correo_miembro, nombre_evento) VALUES
('miguel@ucab.edu.ve', 'Hackathon 2025');

-- Muestra Interes
INSERT INTO Muestra_Interes (correo_miembro, nombre_evento) 
VALUES ('maria@ucab.edu.ve', 'Hackathon 2025');