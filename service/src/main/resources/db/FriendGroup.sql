DROP PROCEDURE IF EXISTS group_table_create;
DELIMITER //

CREATE PROCEDURE group_table_create() BEGIN

CREATE TABLE IF NOT EXISTS `im-friend-group` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_id` int NOT NULL,
  `from_id` VARCHAR(255) Not NULL,
  `group_name` VARCHAR(255) NOT NULL,
  `sequence` INT NULL DEFAULT 1,
  `create_time` BIGINT(20) NOT NULL ,
  `update_time` BIGINT(20) NOT NULL ,
  `del_flag` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL group_table_create();
DROP PROCEDURE group_table_create;
