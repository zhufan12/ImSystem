package com.mogen.im.service.message.entity.id;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistoryId implements Serializable {

    private String fromId;

    private String toId;

    private Long messageKey;

    private String ownerId;

}
