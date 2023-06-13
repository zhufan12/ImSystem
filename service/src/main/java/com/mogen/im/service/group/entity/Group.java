package com.mogen.im.service.group.entity;


import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupStatus;
import com.mogen.im.common.enums.GroupType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "im-group")
@EntityListeners(AuditingEntityListener.class)
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column
    private Integer appId;

    @Column
    private String ownerId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    List<GroupMember> groupMembers;

    @Column
    @Enumerated
    private GroupType groupType;

    private String groupName;
    @Column
    @Enumerated
    private GroupMuteType mute;
    @Column
    private Integer applyJoinType;
    @Column
    private String introduction;
    @Column
    private String notification;
    @Column
    private String photo;
    @Column
    private Integer maxMemberCount;
    @Column
    @Enumerated
    private GroupStatus status = GroupStatus.NORMAL;
    @Column
    private Long sequence;
    @Column
    @CreationTimestamp
    private Long createTime;
    @Column
    @UpdateTimestamp
    private Long updateTime;
    @Column
    private String extra;
}
