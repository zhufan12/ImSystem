package com.mogen.im.service.friendship.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "im-friend-group-member")
@IdClass(value = FriendShipGroupMember.FriendShipGroupMemberId.class)
@Data
public class FriendShipGroupMember {

    @Id
    private Integer groupId;

    @Id
    private String toId;

    @Data
    public static class  FriendShipGroupMemberId {
        private Integer groupId;

        private String toId;
    }
}
