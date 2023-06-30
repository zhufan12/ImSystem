package com.mogen.im.service.user.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogen.im.common.enums.AllowFriendType;
import com.mogen.im.common.enums.DelFlag;
import com.mogen.im.common.enums.UserForbiddenFlagType;
import com.mogen.im.common.enums.UserSilentFlagType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "update `im-user` set del_flag = 1 where id = ?")
@Where(clause = "del_flag != 1")
@Table(name = "im-user")
public class User {

    // 用户id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column
    @JsonProperty(value = "user_pid")
    private String userPid;

    @Column
    private String nickName;

    @Column
    private String password;

    @Column
    private Integer userSex;

    @Column
    private String selfSignature;

    @Column
    @Enumerated
    private AllowFriendType friendAllowType;

    @Column
    private Integer disableAddFriend;

    @Column
    @Enumerated
    private UserForbiddenFlagType forbiddenFlag;

    @Column
    @Enumerated
    private UserSilentFlagType silentFlag;
    @Column
    private Integer userType;
    @Column
    private Integer appId;
    @Column
    @Embedded
    private DelFlag delFlag;
    @Column
    private String extra;
}
