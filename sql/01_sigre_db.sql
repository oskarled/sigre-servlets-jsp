-- =============================================================================
-- SISTEMA DE GESTIÓN DE REGISTRO PARA EVENTOS (SIGRE)
-- E.C.M. Training Center - Cartagena de Indias, Colombia
-- EVIDENCIA: GA6-220501096-AA2-EV03 - Script Bases de Datos del Proyecto
-- AUTOR: Oscar Ramón Ledesma Puello | FICHA: 3134533
-- INSTRUCTOR: Luis Manuel Cabrales Valdés
-- MOTOR: MySQL 8.0 / InnoDB | CODIFICACIÓN: UTF8MB4
-- =============================================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS sigre_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sigre_db;

-- -----------------------------------------------------------------------------
-- ELIMINACIÓN DE TABLAS EN ORDEN INVERSO DE DEPENDENCIAS (DROP CLEANUP)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS evaluaciones_certificados;
DROP TABLE IF EXISTS matriculas_inscripcion;
DROP TABLE IF EXISTS programaciones_evento;
DROP TABLE IF EXISTS cursos_tsa;
DROP TABLE IF EXISTS categorias_curso;
DROP TABLE IF EXISTS afiliaciones_arl;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS empresas;
DROP TABLE IF EXISTS roles;

-- -----------------------------------------------------------------------------
-- 1. TABLA: ROLES
-- -----------------------------------------------------------------------------
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL,
    estado_rol ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Roles de acceso al sistema (Administrador, Asistente, Instructor)';

-- -----------------------------------------------------------------------------
-- 2. TABLA: EMPRESAS
-- -----------------------------------------------------------------------------
CREATE TABLE empresas (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nit_empresa VARCHAR(20) NOT NULL UNIQUE,
    razon_social VARCHAR(150) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email_contacto VARCHAR(100) NOT NULL UNIQUE,
    representante_legal VARCHAR(150) NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Empresas clientes de E.C.M. Training Center';

-- -----------------------------------------------------------------------------
-- 3. TABLA: USUARIOS
-- -----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    id_empresa INT NULL,
    tipo_documento ENUM('CC', 'CE', 'PASAPORTE', 'PEP') NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono_contacto VARCHAR(20) NOT NULL,
    estado_usuario ENUM('ACTIVO', 'INACTIVO', 'BLOQUEADO') DEFAULT 'ACTIVO',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) 
        REFERENCES roles(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (id_empresa) 
        REFERENCES empresas(id_empresa) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Usuarios del sistema con credenciales y perfilamiento';

-- -----------------------------------------------------------------------------
-- 4. TABLA: AFILIACIONES_ARL
-- -----------------------------------------------------------------------------
CREATE TABLE afiliaciones_arl (
    id_afiliacion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre_arl VARCHAR(100) NOT NULL,
    nivel_riesgo VARCHAR(10) NOT NULL CHECK (nivel_riesgo IN ('IV', 'V')),
    fecha_inicio_cobertura DATE NOT NULL,
    fecha_fin_cobertura DATE NOT NULL,
    estado_vigencia ENUM('VIGENTE', 'VENCIDA', 'EN_REVISION') DEFAULT 'VIGENTE',
    CONSTRAINT fk_arl_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_fechas_arl CHECK (fecha_fin_cobertura >= fecha_inicio_cobertura)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Validación legal de ARL Riesgo IV/V según Res. 4272/2021';

-- -----------------------------------------------------------------------------
-- 5. TABLA: CATEGORIAS_CURSO
-- -----------------------------------------------------------------------------
CREATE TABLE categorias_curso (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Clasificación normativa de cursos (Norma Alturas, Rescates, SST)';

-- -----------------------------------------------------------------------------
-- 6. TABLA: CURSOS_TSA
-- -----------------------------------------------------------------------------
CREATE TABLE cursos_tsa (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    codigo_curso VARCHAR(20) NOT NULL UNIQUE,
    nombre_curso VARCHAR(150) NOT NULL,
    intensidad_horaria_hs INT NOT NULL CHECK (intensidad_horaria_hs > 0),
    vigencia_meses INT NOT NULL DEFAULT 12,
    requisito_nivel_riesgo VARCHAR(10) DEFAULT 'V',
    CONSTRAINT fk_curso_categoria FOREIGN KEY (id_categoria) 
        REFERENCES categorias_curso(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Catálogo de cursos de Trabajo Seguro en Alturas';

-- -----------------------------------------------------------------------------
-- 7. TABLA: PROGRAMACIONES_EVENTO
-- -----------------------------------------------------------------------------
CREATE TABLE programaciones_evento (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    id_curso INT NOT NULL,
    id_instructor INT NOT NULL,
    codigo_evento VARCHAR(30) NOT NULL UNIQUE,
    pista_entrenamiento VARCHAR(100) NOT NULL,
    fecha_hora_inicio DATETIME NOT NULL,
    fecha_hora_fin DATETIME NOT NULL,
    cupo_maximo INT NOT NULL CHECK (cupo_maximo > 0 AND cupo_maximo <= 30),
    cupo_disponible INT NOT NULL,
    estado_evento ENUM('PROGRAMADO', 'EN_CURSO', 'FINALIZADO', 'CANCELADO') DEFAULT 'PROGRAMADO',
    CONSTRAINT fk_evento_curso FOREIGN KEY (id_curso) 
        REFERENCES cursos_tsa(id_curso) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_evento_instructor FOREIGN KEY (id_instructor) 
        REFERENCES usuarios(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_fechas_evento CHECK (fecha_hora_fin > fecha_hora_inicio),
    CONSTRAINT chk_cupos CHECK (cupo_disponible <= cupo_maximo AND cupo_disponible >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Programación física y aforo de pistas de entrenamiento';

-- -----------------------------------------------------------------------------
-- 8. TABLA: MATRICULAS_INSCRIPCION
-- -----------------------------------------------------------------------------
CREATE TABLE matriculas_inscripcion (
    id_matricula INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_evento INT NOT NULL,
    codigo_qr VARCHAR(255) NOT NULL UNIQUE,
    fecha_inscripcion DATETIME DEFAULT CURRENT_TIMESTAMP,
    asistencia_confirmada TINYINT(1) DEFAULT 0,
    hora_escaneo_qr DATETIME NULL,
    estado_matricula ENUM('INSCRITO', 'ASISTIO', 'INASISTENCIA', 'CANCELADO') DEFAULT 'INSCRITO',
    CONSTRAINT fk_matricula_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_matricula_evento FOREIGN KEY (id_evento) 
        REFERENCES programaciones_evento(id_evento) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_usuario_evento UNIQUE (id_usuario, id_evento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Inscripciones y control de asistencia con código QR';

-- -----------------------------------------------------------------------------
-- 9. TABLA: EVALUACIONES_CERTIFICADOS
-- -----------------------------------------------------------------------------
CREATE TABLE evaluaciones_certificados (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_matricula INT NOT NULL UNIQUE,
    nota_teorica DECIMAL(4,2) NOT NULL CHECK (nota_teorica >= 0.00 AND nota_teorica <= 10.00),
    nota_practica DECIMAL(4,2) NOT NULL CHECK (nota_practica >= 0.00 AND nota_practica <= 10.00),
    nota_definitiva DECIMAL(4,2) GENERATED ALWAYS AS ((nota_teorica + nota_practica) / 2) STORED,
    resultado_final ENUM('APROBADO', 'NO_APROBADO') NOT NULL,
    numero_certificado_min VARCHAR(50) NOT NULL UNIQUE,
    hash_seguridad_sha256 VARCHAR(64) NOT NULL UNIQUE,
    fecha_expedicion DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    CONSTRAINT fk_evaluacion_matricula FOREIGN KEY (id_matricula) 
        REFERENCES matriculas_inscripcion(id_matricula) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_fechas_cert CHECK (fecha_vencimiento > fecha_expedicion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Evaluaciones y certificados inmutables emitidos bajo Res. 4272/2021';

-- =============================================================================
-- INSERCIÓN DE DATOS INICIALES Y PRUEBA (DML POPULATION)
-- =============================================================================

INSERT INTO roles (nombre_rol, descripcion) VALUES 
('ADMINISTRADOR', 'Acceso total a parametrización, aforos y reportes'),
('INSTRUCTOR', 'Gestión de listas de asistencia y registro de notas'),
('ASISTENTE', 'Consulta de oferta, matrícula y descarga de código QR');

INSERT INTO empresas (nit_empresa, razon_social, direccion, telefono, email_contacto, representante_legal) VALUES 
('900123456-1', 'E.C.M. Training Center S.A.S.', 'Zona Industrial Mamonal Km 7, Cartagena', '6056930000', 'contacto@ecmtraining.com', 'Carlos Eduardo Mendoza'),
('800987654-3', 'Petroquímica del Caribe S.A.', 'Mamonal Sector 3, Cartagena', '6056941111', 'gestionhumana@petrocaribe.co', 'Laura Beatriz Gómez');

INSERT INTO usuarios (id_rol, id_empresa, tipo_documento, numero_documento, nombres, apellidos, correo_electronico, password_hash, telefono_contacto) VALUES 
(1, 1, 'CC', '1047400001', 'Oscar Ramón', 'Ledesma Puello', 'oscar.ledesma@sena.edu.co', '$2a$12$e8N812y0z5F21H.X2G4zdeS8k7Xm11w1234567890abcde', '3001234567'),
(2, 1, 'CC', '73123456', 'Luis Manuel', 'Cabrales Valdés', 'luis.cabrales@sena.edu.co', '$2a$12$e8N812y0z5F21H.X2G4zdeS8k7Xm11w1234567890fghij', '3109876543'),
(3, 2, 'CC', '1047500123', 'Andrés Felipe', 'Martínez Silva', 'andres.martinez@petrocaribe.co', '$2a$12$e8N812y0z5F21H.X2G4zdeS8k7Xm11w1234567890klmno', '3205558899');

INSERT INTO afiliaciones_arl (id_usuario, nombre_arl, nivel_riesgo, fecha_inicio_cobertura, fecha_fin_cobertura, estado_vigencia) VALUES 
(3, 'ARL SURA', 'V', '2026-01-01', '2026-12-31', 'VIGENTE');

INSERT INTO categorias_curso (nombre_categoria, descripcion) VALUES 
('TRABAJO SEGURO EN ALTURAS', 'Cursos normativos obligatorios bajo la Resolución 4272 de 2021');

INSERT INTO cursos_tsa (id_categoria, codigo_curso, nombre_curso, intensidad_horaria_hs, vigencia_meses, requisito_nivel_riesgo) VALUES 
(1, 'TSA-ADV-01', 'Trabajo Seguro en Alturas - Nivel Avanzado / Trabajador Autorizado', 32, 18, 'V'),
(1, 'TSA-REC-01', 'Reentrenamiento en Trabajo Seguro en Alturas', 8, 12, 'V');

INSERT INTO programaciones_evento (id_curso, id_instructor, codigo_evento, pista_entrenamiento, fecha_hora_inicio, fecha_hora_fin, cupo_maximo, cupo_disponible, estado_evento) VALUES 
(1, 2, 'EVT-202609-01', 'Pista 1 - Torre de Entrenamiento Mamonal', '2026-09-25 08:00:00', '2026-09-28 17:00:00', 15, 14, 'PROGRAMADO');

INSERT INTO matriculas_inscripcion (id_usuario, id_evento, codigo_qr, asistencia_confirmada, estado_matricula) VALUES 
(3, 1, 'QR-SIGRE-EVT01-USR03-202609', 1, 'INSCRITO');

INSERT INTO evaluaciones_certificados (id_matricula, nota_teorica, nota_practica, resultado_final, numero_certificado_min, hash_seguridad_sha256, fecha_expedicion, fecha_vencimiento) VALUES 
(1, 9.50, 9.80, 'APROBADO', 'CERT-MIN-2026-000892', '8f3c7b2e1a9d4e5f60718293a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3', '2026-09-28', '2028-03-28');

-- =============================================================================
-- VISTAS TRANSACCIONALES (VIEWS FOR REPORTING & AUDIT)
-- =============================================================================

CREATE OR REPLACE VIEW vw_asistencia_certificaciones AS
SELECT 
    m.id_matricula,
    u.numero_documento,
    CONCAT(u.nombres, ' ', u.apellidos) AS nombre_completo,
    emp.razon_social AS empresa,
    c.nombre_curso,
    e.codigo_evento,
    e.pista_entrenamiento,
    m.estado_matricula,
    ev.nota_definitiva,
    ev.resultado_final,
    ev.numero_certificado_min,
    ev.fecha_expedicion,
    ev.fecha_vencimiento
FROM matriculas_inscripcion m
JOIN usuarios u ON m.id_usuario = u.id_usuario
LEFT JOIN empresas emp ON u.id_empresa = emp.id_empresa
JOIN programaciones_evento e ON m.id_evento = e.id_evento
JOIN cursos_tsa c ON e.id_curso = c.id_curso
LEFT JOIN evaluaciones_certificados ev ON m.id_matricula = ev.id_matricula;

-- =============================================================================
-- PROCEDIMIENTO ALMACENADO: PROCESAR INSCRIPCIÓN Y ACTUALIZAR AFORO
-- =============================================================================

DROP PROCEDURE IF EXISTS sp_registrar_asistente_evento;
DELIMITER //
CREATE PROCEDURE sp_registrar_asistente_evento(
    IN p_id_usuario INT,
    IN p_id_evento INT,
    IN p_codigo_qr VARCHAR(255),
    OUT p_resultado VARCHAR(100)
)
BEGIN
    DECLARE v_cupos INT;
    DECLARE v_arl_vigente VARCHAR(20);
    
    -- 1. Verificar vigencia de ARL
    SELECT estado_vigencia INTO v_arl_vigente
    FROM afiliaciones_arl 
    WHERE id_usuario = p_id_usuario 
    ORDER BY id_afiliacion DESC LIMIT 1;
    
    IF v_arl_vigente IS NULL OR v_arl_vigente != 'VIGENTE' THEN
        SET p_resultado = 'ERROR: El usuario no cuenta con cobertura ARL Riesgo V activa.';
    ELSE
        -- 2. Verificar disponibilidad de cupos
        SELECT cupo_disponible INTO v_cupos 
        FROM programaciones_evento 
        WHERE id_evento = p_id_evento FOR UPDATE;
        
        IF v_cupos > 0 THEN
            INSERT INTO matriculas_inscripcion (id_usuario, id_evento, codigo_qr) 
            VALUES (p_id_usuario, p_id_evento, p_codigo_qr);
            
            -- El cupo lo descuenta el trigger trg_actualizar_aforo_ins (evita descontarlo dos veces).
            SET p_resultado = 'ÉXITO: Inscripción realizada y cupo reservado satisfactoriamente.';
        ELSE
            SET p_resultado = 'ERROR: Aforo máximo alcanzado para esta programación de pista.';
        END IF;
    END IF;
END //
DELIMITER ;

-- =============================================================================
-- TRIGGER: DECREMENTAR CUPO AL INSERTAR MATRÍCULA
-- =============================================================================

DROP TRIGGER IF EXISTS trg_actualizar_aforo_ins;
DELIMITER //
CREATE TRIGGER trg_actualizar_aforo_ins
AFTER INSERT ON matriculas_inscripcion
FOR EACH ROW
BEGIN
    UPDATE programaciones_evento 
    SET cupo_disponible = cupo_disponible - 1 
    WHERE id_evento = NEW.id_evento AND cupo_disponible > 0;
END //
DELIMITER ;

-- FIN DEL SCRIPT SQL SIGRE
