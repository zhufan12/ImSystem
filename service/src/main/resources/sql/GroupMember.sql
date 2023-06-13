DROP PROCEDURE IF EXISTS group_table_create;
DELIMITER //

CREATE PROCEDURE group_table_create() BEGIN

CREATE TABLE IF NOT EXISTS `im-group-member` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_id` int NOT NULL,
  `group_id` int NOT NULL,
  `user_id` VARCHAR(255) Not NULL,
  `role` tinyint(4) NULL DEFAULT 1,
  `speak_date` BIGINT(20) NULL,
  `alias` VARCHAR(255) NULL,
  `join_time` BIGINT(20) NOT NULL,
  `leave_time` BIGINT(20) NULL,
  `join_type` tinyint(4) NULL DEFAULT 1,
  `extra` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`group_id`) REFERENCES  `im-group`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL group_table_create();
DROP PROCEDURE group_table_create;
