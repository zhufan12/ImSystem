package com.mogen.im.service.message.entity;

import com.mogen.im.common.enums.DelFlag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "im-message-body")
public class MessageBody {

    @Column
    private Integer appId;

    @Id
    @GeneratedValue(generator = "messageKeyGenerator",
            strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
            name = "messageKeyGenerator",
            strategy = "com.mogen.im.service.message.entity.id.MessageKeyGenerator"
    )
    private Long messageKey;
    @Column(name = "message_body")
    private String messageBody;
    @Column
    private String securityKey;
    @Column
    private Long messageTime;
    @Column
    @CreationTimestamp
    private Long createTime;
    @Column
    private String extra;
    @Column
    @Enumerated
    private DelFlag delFlag = DelFlag.NORMAL;
}
