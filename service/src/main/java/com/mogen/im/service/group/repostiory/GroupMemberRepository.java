package com.mogen.im.service.group.repostiory;

import com.mogen.im.common.enums.GroupMemberRole;
import com.mogen.im.service.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember,Integer> {

    Optional<GroupMember> findByAppIdAndUserIdAndGroupId(Integer appId,String userId,Integer groupId);


    long countByGroupIdAndRole(Integer groupId, GroupMemberRole groupMemberRole);

    Optional<GroupMember> findByAppIdAndGroupIdAndUserId(Integer appId,Integer groupId,String userID);

    @Query("SELECT userId  FROM GroupMember WHERE groupId = :groupId")
    List<String> findByGroupMemberByGroupId(Integer groupId);

    List<GroupMember> findByAppIdAndUserIdAndRoleNot(Integer appId,String userId,GroupMemberRole role);


    @Modifying
    @Query("UPDATE GroupMember SET role = :role WHERE appId = :appId AND groupId = :groupId AND role = :memberRole")
    int updateRoleByAppIdAndGroupIdAndRole(GroupMemberRole role,Integer appId,Integer groupId,GroupMemberRole memberRole);

    @Modifying
    @Query("UPDATE GroupMember SET role = :role WHERE userId = :userId AND appId = :appId ")
    int updateRoleByUserIdAndAppId(GroupMemberRole role,String userId,Integer appId);

    @Modifying
    @Query("UPDATE GroupMember SET role = :role, leaveTime = :leaveTime WHERE id = :id")
    int updateRoleAndLeaveTimeById(GroupMemberRole role,long leaveTime,Integer id);

    @Modifying
    @Query("UPDATE GroupMember SET speakDate = :speakDate WHERE id = :id")
    int updateSpeakDateById(long speakDate,Integer id);

    List<GroupMember> findByGroupIdAndRoleIn(Integer groupId,List<GroupMemberRole> groupMemberRoles);

    @Query("SELECT groupId From GroupMember WHERE userId =: userId AND appId = appId AND role <> role ")
    List<Integer> findGroupIdByMemberIdAndAppIdAndStatusNot(String memberId,Integer appId,GroupMemberRole role);
}
