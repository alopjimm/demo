USE ventas;
CREATE TABLE IF NOT EXISTS `cliente_has_comercial` (
  `cliente_id` INT UNSIGNED NOT NULL,
  `comercial_id` INT UNSIGNED NOT NULL,
  `fecha` DATE NULL,
  `prioridad` INT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`cliente_id`, `comercial_id`),
  INDEX `fk_cliente_has_comercial_comercial1_idx` (`comercial_id` ASC) VISIBLE,
  INDEX `fk_cliente_has_comercial_cliente1_idx` (`cliente_id` ASC) VISIBLE,
  CONSTRAINT `fk_cliente_has_comercial_cliente1`
    FOREIGN KEY (`cliente_id`)
    REFERENCES `cliente` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cliente_has_comercial_comercial1`
    FOREIGN KEY (`comercial_id`)
    REFERENCES `comercial` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

ENGINE = InnoDB; 

SET @migration = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'cliente_has_comercial'
     AND COLUMN_NAME = 'fecha') = 0,
  'ALTER TABLE `cliente_has_comercial` ADD COLUMN `fecha` DATE NULL',
  'SELECT 1'
);
PREPARE migration_statement FROM @migration;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @migration = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'cliente_has_comercial'
     AND COLUMN_NAME = 'prioridad') = 0,
  'ALTER TABLE `cliente_has_comercial` ADD COLUMN `prioridad` INT UNSIGNED NOT NULL DEFAULT 1',
  'SELECT 1'
);
PREPARE migration_statement FROM @migration;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;