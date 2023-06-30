DROP PROCEDURE IF EXISTS message_history_table_create;
DELIMITER //

CREATE PROCEDURE message_history_table_create() BEGIN


CREATE TABLE IF NOT EXISTS `im-message-history` (
  `app_id` int NOT NULL,
  `from_id` VARCHAR(255) NOT NULL,
  `to_id` VARCHAR(255) NOT NULL,
  `owner_id` VARCHAR(255) NOT NULL,
  `message_key` BIGINT(20) NOT NULL,
  `sequence` BIGINT(20) NOT NULL,
  `message_random` VARCHAR(255) NOT NULL,
  `message_time` BIGINT(20) NOT NULL,
  `message_body` TEXT NOT NULL,
  `create_Time` BIGINT(20) NOT NULL,
  PRIMARY KEY (`from_id`,`to_id`,`owner_id`,`message_key`)
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`)
  FOREIGN KEY (`owner_id`) REFERENCES  `im-user`(`user_pid`)
  FOREIGN KEY (`to_id`) REFERENCES  `im-user`(`user_pid`)
  FOREIGN KEY (`message_key`) REFERENCES  `im-message-body`(`message_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL message_history_table_create();
DROP PROCEDURE message_history_table_create;
