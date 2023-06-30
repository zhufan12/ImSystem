package com.mogen.im.service.conversation.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConversationId implements Serializable {

    private Integer type;

    private String fromId;

    private String toId;

    private Integer appId;
}
