--  Deshabilitar la verificación de clave foránea - no puede truncar la tabla hasta que haya eliminado las referencias de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar registros existentes en las tablas
TRUNCATE TABLE `recursos_humano_db`.`historico`;
TRUNCATE TABLE `recursos_humano_db`.`empleado`;
TRUNCATE TABLE `recursos_humano_db`.`cargo`;
TRUNCATE TABLE `recursos_humano_db`.`departamento`;
TRUNCATE TABLE `recursos_humano_db`.`localizacion`;
TRUNCATE TABLE `recursos_humano_db`.`ciudad`;
TRUNCATE TABLE `recursos_humano_db`.`pais`;

-- Resetear el conteo de AUTO_INCREMENT en las tablas
ALTER TABLE `recursos_humano_db`.`historico` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`empleado` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`cargo` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`departamento` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`localizacion` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`ciudad` AUTO_INCREMENT = 1;
ALTER TABLE `recursos_humano_db`.`pais` AUTO_INCREMENT = 1;

-- Tabla pais
INSERT INTO `recursos_humano_db`.`pais` (`id`, `nombre`) VALUES (1, 'Colombia');
INSERT INTO `recursos_humano_db`.`pais` (`id`, `nombre`) VALUES (2, 'España');
INSERT INTO `recursos_humano_db`.`pais` (`id`, `nombre`) VALUES (3, 'Francia');
INSERT INTO `recursos_humano_db`.`pais` (`id`, `nombre`) VALUES (4, 'Italia');
INSERT INTO `recursos_humano_db`.`pais` (`id`, `nombre`) VALUES (5, 'Alemania');

-- Tabla ciudad
INSERT INTO `recursos_humano_db`.`ciudad` (`id`, `nombre`, `pais_id`) VALUES (1, 'Bogotá', 1);
INSERT INTO `recursos_humano_db`.`ciudad` (`id`, `nombre`, `pais_id`) VALUES (2, 'Medellín', 1);
INSERT INTO `recursos_humano_db`.`ciudad` (`id`, `nombre`, `pais_id`) VALUES (3, 'Cali', 1);
INSERT INTO `recursos_humano_db`.`ciudad` (`id`, `nombre`, `pais_id`) VALUES (4, 'Barranquilla', 1);
INSERT INTO `recursos_humano_db`.`ciudad` (`id`, `nombre`, `pais_id`) VALUES (5, 'Cartagena', 1);

-- Tabla localizacion
INSERT INTO `recursos_humano_db`.`localizacion` (`id`, `direccion`, `ciudad_id`) VALUES (1, 'Calle 85 # 14-05', 1);
INSERT INTO `recursos_humano_db`.`localizacion` (`id`, `direccion`, `ciudad_id`) VALUES (2, 'Carrera 9 # 75-70', 1);
INSERT INTO `recursos_humano_db`.`localizacion` (`id`, `direccion`, `ciudad_id`) VALUES (3, 'Carrera 13 # 37-85', 1);
INSERT INTO `recursos_humano_db`.`localizacion` (`id`, `direccion`, `ciudad_id`) VALUES (4, 'Calle 10 # 52-15', 2);
INSERT INTO `recursos_humano_db`.`localizacion` (`id`, `direccion`, `ciudad_id`) VALUES (5, 'Avenida del Poblado # 66-50', 2);

-- Tabla departamento
INSERT INTO `recursos_humano_db`.`departamento` (`id`, `nombre`, `localizacion_id`) VALUES (1, 'Recursos Humanos', 1);
INSERT INTO `recursos_humano_db`.`departamento` (`id`, `nombre`, `localizacion_id`) VALUES (2, 'Ingeniería', 2);
INSERT INTO `recursos_humano_db`.`departamento` (`id`, `nombre`, `localizacion_id`) VALUES (3, 'Marketing', 3);
INSERT INTO `recursos_humano_db`.`departamento` (`id`, `nombre`, `localizacion_id`) VALUES (4, 'Ventas', 4);
INSERT INTO `recursos_humano_db`.`departamento` (`id`, `nombre`, `localizacion_id`) VALUES (5, 'Administración', 5);

-- Tabla cargo
INSERT INTO `recursos_humano_db`.`cargo` (`id`, `nombre`, `sueldo_minimo`, `sueldo_maximo`) VALUES (1, 'Gerente General', 1800000, 5400000);
INSERT INTO `recursos_humano_db`.`cargo` (`id`, `nombre`, `sueldo_minimo`, `sueldo_maximo`) VALUES (2, 'Director de Departamento', 1600000, 4800000);
INSERT INTO `recursos_humano_db`.`cargo` (`id`, `nombre`, `sueldo_minimo`, `sueldo_maximo`) VALUES (3, 'Jefe de Equipo', 1400000, 4200000);
INSERT INTO `recursos_humano_db`.`cargo` (`id`, `nombre`, `sueldo_minimo`, `sueldo_maximo`) VALUES (4, 'Desarrollador Senior', 1300000, 3900000);
INSERT INTO `recursos_humano_db`.`cargo` (`id`, `nombre`, `sueldo_minimo`, `sueldo_maximo`) VALUES (5, 'Desarrollador Junior', 1200000, 3600000);

-- Tabla empleado
INSERT INTO `recursos_humano_db`.`empleado` (`id`, `documento_identidad`, `primer_nombre`, `segundo_nombre`, `primer_apellido`, `segundo_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`) VALUES (1, 'CC1234567', 'Juan', 'Guillermo', 'Lopez', 'Gutierrez', 'juan.g@mail.com', '1980-05-22', 5000000, 1, 1);
INSERT INTO `recursos_humano_db`.`empleado` (`id`, `documento_identidad`, `primer_nombre`, `segundo_nombre`, `primer_apellido`, `segundo_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`, `gerente_id`) VALUES (2, 'CC2345678', 'Julia', 'Carolina', 'Martinez', 'Guzman', 'julia.c@mail.com', '1982-12-15', 4500000, 2, 1, 1);
INSERT INTO `recursos_humano_db`.`empleado` (`id`, `documento_identidad`, `primer_nombre`, `primer_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`, `gerente_id`) VALUES (3, 'CC3456789', 'Enrique', 'Diaz', 'enrique.d@mail.com', '1975-06-30', 4000000, 3, 1, 2);
INSERT INTO `recursos_humano_db`.`empleado` (`id`, `documento_identidad`, `primer_nombre`, `segundo_nombre`, `primer_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`, `gerente_id`) VALUES (4, 'CC4567890', 'Sandra', 'Patricia', 'Suarez', 'sandra.p@mail.com', '1988-09-05', 3500000, 4, 1, 2);
INSERT INTO `recursos_humano_db`.`empleado` (`id`, `documento_identidad`, `primer_nombre`, `primer_apellido`, `segundo_apellido`, `email`, `fecha_nac`, `sueldo`, `cargo_id`, `departamento_id`) VALUES (5, 'CC5678901', 'Leonardo', 'Rojas', 'Castillo', 'leonardo.r@mail.com', '1987-11-20', 3000000, 5, 5);

-- Tabla historico
INSERT INTO `recursos_humano_db`.`historico` (`id`, `empleado_id`, `cargo_id`, `departamento_id`, `fecha_retiro`) VALUES (1, 4, 1, 1, '2023-01-01');
INSERT INTO `recursos_humano_db`.`historico` (`id`, `empleado_id`, `cargo_id`, `departamento_id`, `fecha_retiro`) VALUES (2, 5, 2, 2, '2023-06-01');

-- Habilitar la verificación de clave foránea de nuevo
SET FOREIGN_KEY_CHECKS = 1;