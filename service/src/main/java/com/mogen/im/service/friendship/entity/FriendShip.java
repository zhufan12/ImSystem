package com.mogen.im.service.friendship.entity;


import com.mogen.im.common.enums.FriendShipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(value = FriendShipId.class)
@Table(name = "im-friend-ship")
@EntityListeners(AuditingEntityListener.class)
public class FriendShip implements Serializable {

    @Id
    private Integer appId;

    @Id
    private String fromId;

    @Id
    private String toId;

    @Column
    private String remark;

    @Column
    @Enumerated
    private FriendShipStatus status;

    @Column
    @Enumerated
    private FriendShipStatus black;

    @CreationTimestamp
    @Column(name = "create_time")
    private Long createTime;
    @Column
    private Long friendSequence;
    @Column
    private String addSource;
    @Column
    private String extra;
}
