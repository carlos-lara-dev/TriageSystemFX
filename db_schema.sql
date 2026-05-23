-- ============================================================
--  TRIAGE SYSTEM - Script completo de base de datos
--  Universidad Mariano Gálvez de Guatemala
-- ============================================================

-- ============================================================
--  TABLAS EXISTENTES
-- ============================================================

CREATE TABLE `paciente` (
  `id_paciente` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `dpi` varchar(20) UNIQUE NOT NULL,
  `telefono` varchar(15),
  `activo` bit DEFAULT 1,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `prioridad` (
  `id_prioridad` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(20) NOT NULL,
  `valor` integer NOT NULL,
  `activo` bit DEFAULT 1
);

CREATE TABLE `estado` (
  `id_estado` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(20) NOT NULL,
  `activo` bit DEFAULT 1
);

CREATE TABLE `medico` (
  `id_medico` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre` text NOT NULL,
  `activo` bit DEFAULT 1
);

CREATE TABLE `ingreso` (
  `id_ingreso` integer PRIMARY KEY AUTO_INCREMENT,
  `id_paciente` integer NOT NULL,
  `id_prioridad` integer NOT NULL,
  `id_estado` integer NOT NULL,
  `sintomas` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `activo` bit DEFAULT 1
);

CREATE TABLE `consulta` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `id_ingreso` integer NOT NULL,
  `id_medico` integer NOT NULL,
  `hora_inicio` timestamp NOT NULL DEFAULT (now()),
  `hora_fin` timestamp,
  `observaciones` text,
  `activo` bit DEFAULT 1
);

-- ============================================================
--  NUEVAS TABLAS: ROLES Y USUARIOS
-- ============================================================

-- Roles del sistema (ej: Administrador, Recepcionista, Médico)
CREATE TABLE `rol` (
  `id_rol` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200),
  `activo` bit DEFAULT 1
);

-- Usuarios del sistema con credenciales de acceso
-- Se puede vincular opcionalmente a un médico (id_medico nullable)
CREATE TABLE `usuario` (
  `id_usuario` integer PRIMARY KEY AUTO_INCREMENT,
  `nombre_completo` varchar(100) NOT NULL,
  `nombre_usuario` varchar(50) UNIQUE NOT NULL,
  `clave_hash` varchar(255) NOT NULL,      -- Guardar siempre encriptado (ej: SHA-256)
  `id_rol` integer NOT NULL,
  `activo` bit DEFAULT 1,
  `creado_en` timestamp DEFAULT (now())
);

-- ============================================================
--  FOREIGN KEYS
-- ============================================================

ALTER TABLE `ingreso` ADD FOREIGN KEY (`id_paciente`)  REFERENCES `paciente`  (`id_paciente`);
ALTER TABLE `ingreso` ADD FOREIGN KEY (`id_prioridad`) REFERENCES `prioridad` (`id_prioridad`);
ALTER TABLE `ingreso` ADD FOREIGN KEY (`id_estado`)    REFERENCES `estado`    (`id_estado`);

ALTER TABLE `consulta` ADD FOREIGN KEY (`id_ingreso`)  REFERENCES `ingreso`   (`id_ingreso`);
ALTER TABLE `consulta` ADD FOREIGN KEY (`id_medico`)   REFERENCES `medico`    (`id_medico`);

ALTER TABLE `usuario`  ADD FOREIGN KEY (`id_rol`)      REFERENCES `rol`       (`id_rol`);
ALTER TABLE `usuario`  ADD FOREIGN KEY (`id_medico`)   REFERENCES `medico`    (`id_medico`);

-- ============================================================
--  DATOS BASE (Inserts iniciales)
-- ============================================================

-- Prioridades médicas (valor más alto = más urgente)
INSERT INTO `prioridad` (`nombre`, `valor`) VALUES
  ('Critica', 4),
  ('Alta',     3),
  ('Media',   2),
  ('Baja',      1);

-- Estados de un ingreso
INSERT INTO `estado` (`nombre`) VALUES
  ('En espera'),
  ('En consulta'),
  ('Atendido'),
  ('Cancelado');

-- Roles del sistema
INSERT INTO `rol` (`nombre`, `descripcion`) VALUES
  ('Administrador',  'Acceso total al sistema'),
  ('Recepcionista',  'Puede registrar pacientes e ingresos'),
  ('Médico',         'Puede ver y atender la cola de pacientes');

-- Usuario administrador por defecto
-- password: admin123  →  hash SHA-256: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
INSERT INTO `usuario` (`nombre_completo`, `nombre_usuario`, `clave_hash`, `id_rol`) VALUES
  ('Administrador', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1);
