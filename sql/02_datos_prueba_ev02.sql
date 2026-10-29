-- =============================================================================
-- SIGRE - Datos de prueba para la evidencia GA7-220501096-AA2-EV02
-- Ejecutar DESPUÉS de 01_sigre_db.sql (crea la base de datos sigre_db).
-- =============================================================================
USE sigre_db;
SET NAMES utf8mb4;

-- Instructor adicional y asistentes para probar las reglas de inscripción
INSERT INTO usuarios (id_rol, id_empresa, tipo_documento, numero_documento, nombres, apellidos, correo_electronico, password_hash, telefono_contacto) VALUES
(2, 1, 'CC', '73200111', 'Diana Patricia', 'Herrera Ruiz', 'diana.herrera@ecmtraining.com', '$2a$12$pruebaHashInstructor000000000000000000000000000000', '3014445566'),
(3, 2, 'CC', '1047500200', 'Carlos Andrés', 'Pérez Julio', 'carlos.perez@petrocaribe.co', '$2a$12$pruebaHashAsistente0000000000000000000000000000001', '3001112233'),
(3, 2, 'CC', '1047500300', 'María José', 'Castro Díaz', 'maria.castro@petrocaribe.co', '$2a$12$pruebaHashAsistente0000000000000000000000000000002', '3002223344'),
(3, 2, 'CC', '1047500400', 'Jorge Luis', 'Romero Villa', 'jorge.romero@petrocaribe.co', '$2a$12$pruebaHashAsistente0000000000000000000000000000003', '3003334455');

-- ARL: Carlos y María José vigentes; Jorge Luis con ARL vencida (debe ser rechazado)
INSERT INTO afiliaciones_arl (id_usuario, nombre_arl, nivel_riesgo, fecha_inicio_cobertura, fecha_fin_cobertura, estado_vigencia)
SELECT id_usuario, 'ARL SURA', 'V', '2026-01-01', '2026-12-31', 'VIGENTE' FROM usuarios WHERE numero_documento IN ('1047500200', '1047500300');
INSERT INTO afiliaciones_arl (id_usuario, nombre_arl, nivel_riesgo, fecha_inicio_cobertura, fecha_fin_cobertura, estado_vigencia)
SELECT id_usuario, 'ARL Positiva', 'V', '2025-01-01', '2025-12-31', 'VENCIDA' FROM usuarios WHERE numero_documento = '1047500400';

-- Categoría y curso adicionales
INSERT INTO categorias_curso (nombre_categoria, descripcion) VALUES
('RESCATE EN ALTURAS', 'Cursos de rescate y atención de emergencias en alturas');

INSERT INTO cursos_tsa (id_categoria, codigo_curso, nombre_curso, intensidad_horaria_hs, vigencia_meses, requisito_nivel_riesgo) VALUES
(1, 'TSA-COO-01', 'Coordinador de Trabajo en Alturas', 80, 24, 'V');

-- Evento futuro con solo 2 cupos para probar el control de aforo
INSERT INTO programaciones_evento (id_curso, id_instructor, codigo_evento, pista_entrenamiento, fecha_hora_inicio, fecha_hora_fin, cupo_maximo, cupo_disponible, estado_evento) VALUES
(2, 2, 'EVT-202611-01', 'Pista 2 - Estructura de andamios certificados', '2026-11-20 07:00:00', '2026-11-20 17:00:00', 2, 2, 'PROGRAMADO');
