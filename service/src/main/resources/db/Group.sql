DROP PROCEDURE IF EXISTS group_table_create;
DELIMITER //

CREATE PROCEDURE group_table_create() BEGIN

CREATE TABLE IF NOT EXISTS `im-group` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_id` int NOT NULL,
  `owner_id` VARCHAR(255) Not NULL,
  `group_name` VARCHAR(255) NOT NULL,
  `group_type` tinyint(4) NULL DEFAULT 1,
  `mute` tinyint(4) NULL DEFAULT 0,
  `apply_join_type` tinyint(4) NULL DEFAULT 0,
  `introduction` TEXT NUll,
  `notification` TEXT NULL,
  `photo` VARCHAR(255) NUll,
  `max_member_count` int null ,
  `status` tinyint(4) not null default 1,
  `sequence` BIGINT(20) null,
  `create_time` BIGINT(20) NOT NULL ,
  `update_time` BIGINT(20) NOT NULL ,
  `extra` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`owner_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL group_table_create();
DROP PROCEDURE group_table_create;
