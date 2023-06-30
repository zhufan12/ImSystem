DROP PROCEDURE IF EXISTS friend_ship_request_table_create;
DELIMITER //

CREATE PROCEDURE friend_ship_request_table_create() BEGIN

CREATE TABLE IF NOT EXISTS `im-friend-ship-request` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_id` int NOT NULL,
  `from_id` VARCHAR(255) Not NULL,
  `to_id` VARCHAR(255) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `add_source` varchar(255) DEFAULT NULL,
  `add_wording`  varchar(255) DEFAULT NULL,
  `read_status` tinyint(4) DEFAULT 1,
  `approve_status` tinyint(4) DEFAULT 0,
  `create_time` BIGINT(20) NOT NULL ,
  `update_time` BIGINT(20) NOT NULL ,
  `sequence` BIGINT(20) NULL ,
   PRIMARY KEY (`id`),
  FOREIGN KEY (`to_id`) REFERENCES `im-user`(`user_pid`),
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL friend_ship_request_table_create();
DROP PROCEDURE friend_ship_request_table_create;
