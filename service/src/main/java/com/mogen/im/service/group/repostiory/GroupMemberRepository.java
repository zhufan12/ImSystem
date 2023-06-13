package com.mogen.im.service.group.repostiory;

import com.mogen.im.common.enums.GroupMemberRole;
import com.mogen.im.service.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember,Integer> {

    Optional<GroupMember> findByAppIdAndUserIdAndGroupId(Integer appId,String userId,Integer groupId);


    long countByGroupIdAndRole(Integer groupId, GroupMemberRole groupMemberRole);

    Optional<GroupMember> findByAppIdAndGroupIdAndUserId(Integer appId,Integer groupId,String userID);


    List<GroupMember> findByAppIdAndUserIdAndRoleNot(Integer appId,String userId,GroupMemberRole role);


    @Modifying
    int updateRoleByAppIdAndGroupIdAndRole(GroupMemberRole role,Integer appId,Integer groupId,GroupMemberRole memberRole);

    @Modifying
    int updateRoleByUserIdAndAppId(GroupMemberRole role,String userId,Integer appId);

    @Modifying
    int updateRoleAndLeaveTimeById(GroupMemberRole role,long leaveTime,Integer id);

    @Modifying
    int updateSpeakDateById(long speakDate,Integer id);
}
