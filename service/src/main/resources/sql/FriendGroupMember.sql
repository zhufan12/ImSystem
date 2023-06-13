DROP PROCEDURE IF EXISTS friend_group_member_table_create;
DELIMITER//

CREATE PROCEDURE friend_group_member_table_create() BEGIN





CREATE TABLE IF NOT EXISTS `im-friend-group-member` (
  `group_id` int NOT NULL AUTO_INCREMENT,
  `to_id` VARCHAR(255) Not NULL,
  PRIMARY KEY (`to_id`,`group_id`),
  FOREIGN KEY (`to_id`) REFERENCES  `im-user`(`user_pid`)
  FOREIGN KEY (`group_id`) REFERENCES `im-friend-group`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


END//
DELIMITER ;
CALL friend_group_member_table_create();
DROP PROCEDURE friend_group_member_table_create;