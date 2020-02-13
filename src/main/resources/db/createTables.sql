USE `U05NQU` ;

-- -----------------------------------------------------
-- Table `U05NQU`.`appointment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `U05NQU`.`appointment` (
  `appointmentId` INT(10) NOT NULL AUTO_INCREMENT,
  `start` DATETIME NOT NULL,
  `end` DATETIME NOT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `type` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `month` VARCHAR(255) NULL DEFAULT NULL,
  `createDate` DATETIME NULL DEFAULT NULL,
  `createdBy` VARCHAR(45) NULL DEFAULT NULL,
  `lastUpdate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` VARCHAR(45) NOT NULL,
  `customerId` INT(10) NULL DEFAULT NULL,
  `barberId` INT(10) NULL DEFAULT NULL,
  `petId` INT(10) NULL DEFAULT NULL,
  PRIMARY KEY (`appointmentId`))
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `U05NQU`.`barber`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `U05NQU`.`barber` (
  `barberId` INT(10) NOT NULL AUTO_INCREMENT,
  `barberName` VARCHAR(255) NULL DEFAULT NULL,
  `barberPhone` VARCHAR(45) NULL DEFAULT NULL,
  `barberEmail` VARCHAR(45) NULL DEFAULT NULL,
  `notes` VARCHAR(255) NULL DEFAULT NULL,
  `active` VARCHAR(45) NULL DEFAULT NULL,
  `hireDate` DATETIME NULL DEFAULT NULL,
  `createDate` DATETIME NULL DEFAULT NULL,
  `createdBy` VARCHAR(40) NULL DEFAULT NULL,
  `lastUpdate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`barberId`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `U05NQU`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `U05NQU`.`customer` (
  `customerId` INT(10) NOT NULL AUTO_INCREMENT,
  `customerName` VARCHAR(45) NULL DEFAULT NULL,
  `customerPhone` VARCHAR(255) NULL DEFAULT NULL,
  `customerEmail` VARCHAR(255) NULL DEFAULT NULL,
  `notes` VARCHAR(255) NULL DEFAULT NULL,
  `active` TINYINT(1) NULL DEFAULT NULL,
  `createDate` DATETIME NOT NULL,
  `createdBy` VARCHAR(40) NOT NULL,
  `lastUpdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`customerId`))
ENGINE = InnoDB
AUTO_INCREMENT = 30
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `U05NQU`.`pet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `U05NQU`.`pet` (
  `petId` INT(10) NOT NULL AUTO_INCREMENT,
  `petName` VARCHAR(255) NULL DEFAULT NULL,
  `petType` VARCHAR(255) NULL DEFAULT NULL,
  `petDescription` VARCHAR(255) NULL DEFAULT NULL,
  `createDate` DATETIME NOT NULL,
  `createdBy` VARCHAR(40) NOT NULL,
  `lastUpdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` VARCHAR(40) NOT NULL,
  `customerId` INT(10) NULL DEFAULT NULL,
  PRIMARY KEY (`petId`),
  INDEX `customerId_idx` (`customerId` ASC) VISIBLE,
  CONSTRAINT `customerId_fk`
    FOREIGN KEY (`customerId`)
    REFERENCES `U05NQU`.`customer` (`customerId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 38
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `U05NQU`.`images`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `U05NQU`.`images` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `image` LONGBLOB NULL DEFAULT NULL,
  `petId` INT(10) NULL DEFAULT NULL,
  `createDate` DATETIME NULL DEFAULT NULL,
  `createdBy` VARCHAR(45) NULL DEFAULT NULL,
  `lastUpdate` TIMESTAMP NULL DEFAULT NULL,
  `lastUpdatedBy` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `petId_idx` (`petId` ASC) VISIBLE,
  CONSTRAINT `petId_fk`
    FOREIGN KEY (`petId`)
    REFERENCES `U05NQU`.`pet` (`petId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = latin1;
