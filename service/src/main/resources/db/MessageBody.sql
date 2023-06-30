DROP PROCEDURE IF EXISTS message_body_table_create;
DELIMITER //

CREATE PROCEDURE message_body_table_create() BEGIN


CREATE TABLE IF NOT EXISTS `im-message-body` (
  `app_id` int NOT NULL,
  `message_key` BIGINT(20) NOT NULL,
  `message_body` TEXT NOT NULL,
  `security_key` varchar(45) DEFAULT NULL,
  `message_time` BIGINT(20) NOT NULL,
  `create_time` BIGINT(20) NOT NULL,
  `extra` varchar(255) NULL,
  `del_flag` tinyint(1) default 0,
  PRIMARY KEY (`message_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL message_body_table_create();
DROP PROCEDURE message_body__table_create;
