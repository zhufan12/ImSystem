DROP PROCEDURE IF EXISTS conversation_table_create;
DELIMITER //

CREATE PROCEDURE conversation_table_create() BEGIN


CREATE TABLE IF NOT EXISTS `im-conversation` (
  `type` int NOT NULL,
  `app_id` int NOT NULL,
  `from_id` varchar(255) NOT NULL,
  `to_id` varchar(255) NOT NULL,
  `is_mute` tinyint(1) NOT NULL DEFAULT 0,
  `is_top` tinyint(1) NOT NULL DEFAULT 0,
  `sequence` BIGINT(20) NOT NULL ,
  `readed_sequence` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`type`,`from_id`,`to_id`,`app_id`),
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`),
  FOREIGN KEY (`to_id`) REFERENCES  `im-user`(`user_pid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL conversation_table_create();
DROP PROCEDURE conversation_table_create;