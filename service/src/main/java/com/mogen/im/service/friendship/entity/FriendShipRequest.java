package com.mogen.im.service.friendship.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "im-friend-ship-request")
@EntityListeners(AuditingEntityListener.class)
public class FriendShipRequest {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Integer appId;
    @Column
    private String fromId;
    @Column
    private String toId;
    @Column
    private String remark;

    @Column
    private Integer readStatus = 0;

    @Column
    private String addSource;
    @Column
    private String addWording;

    @Column
    private Integer approveStatus = 0;
    @Column
    @CreationTimestamp
    private Long createTime;
    @Column
    @UpdateTimestamp
    private Long updateTime;

    @Column
    private Long sequence;
}
