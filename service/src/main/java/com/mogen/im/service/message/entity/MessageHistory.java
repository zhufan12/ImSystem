package com.mogen.im.service.message.entity;

import com.mogen.im.service.friendship.entity.FriendShipId;
import com.mogen.im.service.message.entity.id.MessageHistoryId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(value = MessageHistoryId.class)
@Table(name = "im-message-history")
public class MessageHistory {
    @Column
    private Integer appId;
    @Id
    private String fromId;
    @Id
    private String toId;
    @Id
    private Long messageKey;
    @Id
    private String ownerId;

    @Column
    private Long sequence;
    @Column
    private String messageRandom;
    @Column
    private Long messageTime;

    @Column
    @CreationTimestamp
    private Long createTime;

}
