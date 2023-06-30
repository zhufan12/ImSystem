package com.mogen.im.service.conversation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(value = ConversationId.class)
@Table(name = "im-conversation")
public class Conversation {

    @Id
    private Integer type;

    @Id
    private String fromId;

    @Id
    private String toId;

    @Id
    private Integer appId;

    @Column
    private int isMute;

    @Column
    private int isTop;

    @Column
    private Long sequence;

    @Column
    private Long readedSequence;


}
