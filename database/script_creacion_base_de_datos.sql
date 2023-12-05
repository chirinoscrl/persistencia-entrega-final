-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema recursos_humano_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema recursos_humano_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `recursos_humano_db` DEFAULT CHARACTER SET utf8 ;
USE `recursos_humano_db` ;

-- -----------------------------------------------------
-- Table `recursos_humano_db`.`pais`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`pais` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`ciudad`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`ciudad` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(60) NULL,
  `pais_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ciudad_pais_idx` (`pais_id` ASC) VISIBLE,
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE,
  CONSTRAINT `fk_ciudad_pais`
    FOREIGN KEY (`pais_id`)
    REFERENCES `recursos_humano_db`.`pais` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`localizacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`localizacion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `direccion` VARCHAR(100) NULL,
  `ciudad_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_localizacion_ciudad1_idx` (`ciudad_id` ASC) VISIBLE,
  CONSTRAINT `fk_localizacion_ciudad`
    FOREIGN KEY (`ciudad_id`)
    REFERENCES `recursos_humano_db`.`ciudad` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`departamento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`departamento` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `localizacion_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_departamento_localizacion1_idx` (`localizacion_id` ASC) VISIBLE,
  CONSTRAINT `fk_departamento_localizacion`
    FOREIGN KEY (`localizacion_id`)
    REFERENCES `recursos_humano_db`.`localizacion` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`cargo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`cargo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(60) NOT NULL,
  `sueldo_minimo` DECIMAL(11,3) NOT NULL,
  `sueldo_maximo` DECIMAL(11,3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`empleado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`empleado` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `documento_identidad` VARCHAR(50) NOT NULL,
  `primer_nombre` VARCHAR(50) NOT NULL,
  `segundo_nombre` VARCHAR(50) NULL,
  `primer_apellido` VARCHAR(50) NOT NULL,
  `segundo_apellido` VARCHAR(50) NULL,
  `email` VARCHAR(60) NOT NULL,
  `fecha_nac` DATE NOT NULL,
  `sueldo` DECIMAL(11,3) NOT NULL,
  `comision` INT NULL,
  `cargo_id` INT NOT NULL,
  `departamento_id` INT NOT NULL,
  `gerente_id` INT NULL,
  `estado` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_empleado_cargo_idx` (`cargo_id` ASC) VISIBLE,
  INDEX `fk_empleado_departamento_idx` (`departamento_id` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  INDEX `fk_empleado_gerente_idx` (`gerente_id` ASC) VISIBLE,
  UNIQUE INDEX `documento_identidad_UNIQUE` (`documento_identidad` ASC) VISIBLE,
  CONSTRAINT `fk_empleado_cargo`
    FOREIGN KEY (`cargo_id`)
    REFERENCES `recursos_humano_db`.`cargo` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_empleado_departamento`
    FOREIGN KEY (`departamento_id`)
    REFERENCES `recursos_humano_db`.`departamento` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_empleado_gerente`
    FOREIGN KEY (`gerente_id`)
    REFERENCES `recursos_humano_db`.`empleado` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recursos_humano_db`.`historico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recursos_humano_db`.`historico` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `empleado_id` INT NOT NULL,
  `cargo_id` INT NOT NULL,
  `departamento_id` INT NOT NULL,
  `fecha_retiro` DATE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_historico_empleado1_idx` (`empleado_id` ASC) VISIBLE,
  INDEX `fk_historico_cargo1_idx` (`cargo_id` ASC) VISIBLE,
  INDEX `fk_historico_departamento1_idx` (`departamento_id` ASC) VISIBLE,
  CONSTRAINT `fk_historico_empleado`
    FOREIGN KEY (`empleado_id`)
    REFERENCES `recursos_humano_db`.`empleado` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_historico_cargo`
    FOREIGN KEY (`cargo_id`)
    REFERENCES `recursos_humano_db`.`cargo` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_historico_departamento`
    FOREIGN KEY (`departamento_id`)
    REFERENCES `recursos_humano_db`.`departamento` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `recursos_humano_db`;

DELIMITER $$
USE `recursos_humano_db`$$
CREATE DEFINER = CURRENT_USER TRIGGER `recursos_humano_db`.`empleado_BEFORE_INSERT` BEFORE INSERT ON `empleado` FOR EACH ROW
BEGIN
	DECLARE gerente_departamento INT;
	  
	  IF NEW.gerente_id IS NOT NULL THEN
		SELECT departamento_id INTO gerente_departamento 
		FROM empleado WHERE id = NEW.gerente_id;
		
		IF gerente_departamento != NEW.departamento_id THEN
		  SIGNAL SQLSTATE VALUE '45000'
		  SET MESSAGE_TEXT = 'El gerente no pertenece al mismo departamento que el empleado.';
		END IF;
	  END IF;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
