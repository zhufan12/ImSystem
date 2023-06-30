package com.mogen.im.codec.pack.friend;

import lombok.Data;

@Data
public class DeleteBlackPack {

    private String fromId;

    private String toId;

    private Long sequence;
}
