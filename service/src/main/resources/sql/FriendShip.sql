DROP PROCEDURE IF EXISTS friend_ship_table_create;
DELIMITER //

CREATE PROCEDURE friend_ship_table_create() BEGIN

CREATE TABLE IF NOT EXISTS `im-friend-ship` (
  `app_id` int NOT NULL,
  `from_id` VARCHAR(255) Not NULL,
  `to_id` VARCHAR(255) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `extra` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT 1,
  `black`  tinyint(4) DEFAULT 0,
  `black_sequence` varchar(255) DEFAULT NULL,
  `create_time` BIGINT(20) NOT NULL ,
  `friend_sequence` INT NULL DEFAULT 1,
  `add_source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`app_id`,`from_id`,`to_id`),
  FOREIGN KEY (`to_id`) REFERENCES `im-user`(`user_pid`),
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL friend_ship_table_create();
DROP PROCEDURE friend_ship_table_create;
