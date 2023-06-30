package com.mogen.im.service.group.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "im-group-message-history")
public class GroupMessageHistory {
    @Column
    private Integer appId;

    @Column
    private String fromId;

    @Column
    private String groupId;

    @Id
    @GeneratedValue(generator = "messageKeyGenerator",
            strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
            name = "messageKeyGenerator",
            strategy = "com.mogen.im.service.message.entity.id.MessageKeyGenerator"
    )
    private Long messageKey;

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
