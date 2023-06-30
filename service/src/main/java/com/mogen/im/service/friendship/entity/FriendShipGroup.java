package com.mogen.im.service.friendship.entity;

import com.mogen.im.common.enums.DelFlag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "im-friend-group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FriendShipGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String fromId;
    @Column
    private Integer appId;
    @Column
    private String groupName;

    @Column
    @CreationTimestamp
    private Long createTime;

    @Column
    @UpdateTimestamp
    private Long updateTime;

    @Column
    private Long sequence;

    @Column
    @Enumerated
    private DelFlag delFlag;
}
