package com.mogen.im.service.group.entity;

import com.mogen.im.common.enums.GroupMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "im-group-member")
@EntityListeners(AuditingEntityListener.class)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer appId;

    @Column
    private Integer groupId;

    @Column
    private String userId;

    @Column
    @Enumerated
    private GroupMemberRole role;

    @Column
    private Long speakDate;

    @Column
    private String alias;

    @Column
    private Long joinTime;

    @Column
    private Long leaveTime;

    @Column
    private String joinType;
    @Column
    private String extra;
}
