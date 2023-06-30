DROP PROCEDURE IF EXISTS user_table_create;
DELIMITER //

CREATE PROCEDURE user_table_create() BEGIN


CREATE TABLE IF NOT EXISTS `im-user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_pid` varchar(255) NOT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_sex` tinyint(1) DEFAULT NULL,
  `self_signature` varchar(255) DEFAULT NULL,
  `friend_allow_type` tinyint(1) DEFAULT 0,
  `disable_add_friend` tinyint(1) DEFAULT 0,
  `forbidden_flag` int DEFAULT NULL,
  `silent_flag` int DEFAULT NULL,
  `user_type` tinyint(1) DEFAULT NULL,
  `app_id` int NOT NULL,
  `del_flag` varchar(255) DEFAULT 0,
  `extra` varchar(255) DEFAULT NULL,
  `code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_pid_app_id_index` (`user_pid`,`app_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL user_table_create();
DROP PROCEDURE user_table_create;
