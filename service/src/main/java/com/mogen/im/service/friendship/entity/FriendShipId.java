package com.mogen.im.service.friendship.entity;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class FriendShipId implements Serializable {
    private Integer appId;

    private String fromId;

    private String toId;
}
