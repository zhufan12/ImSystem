DROP PROCEDURE IF EXISTS message_group_history_table_create;
DELIMITER //

CREATE PROCEDURE message_group_history_table_create() BEGIN


CREATE TABLE IF NOT EXISTS `im-group-message-history` (
  `app_id` int NOT NULL,
  `from_id` VARCHAR(255) NOT NULL,
  `group_id` int NOT NULL,
  `message_key` BIGINT(20) NOT NULL,
  `sequence`  BIGINT(20)  NULL,
  `message_random` VARCHAR(255) null,
  `message_time` BIGINT(20) NOT NULL,
  `create_Time` BIGINT(20) NOT NULL,
  PRIMARY KEY (`message_key`),
  FOREIGN KEY (`from_id`) REFERENCES  `im-user`(`user_pid`),
  FOREIGN KEY (`group_id`) REFERENCES  `im-group`(`id`),
  FOREIGN KEY (`message_key`) REFERENCES  `im-message-body`(`message_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL message_group_history_table_create();
DROP PROCEDURE message_group_history_table_create;
