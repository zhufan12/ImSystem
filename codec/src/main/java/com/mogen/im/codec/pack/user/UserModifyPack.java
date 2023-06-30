package com.mogen.im.codec.pack.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyPack {
        private String userId;

        private String nickName;

        private String password;

        private String photo;

        private String userSex;

        private String selfSignature;

        private Integer friendAllowType;
}