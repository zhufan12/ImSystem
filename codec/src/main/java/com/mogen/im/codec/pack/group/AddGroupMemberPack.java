package com.mogen.im.codec.pack.group;

import lombok.Data;

import java.util.List;

@Data
public class AddGroupMemberPack {

    private Integer groupId;

    private List<String> members;

}
