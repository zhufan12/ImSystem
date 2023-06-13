package com.mogen.im.service.friendship.repository;

import com.mogen.im.service.friendship.entity.FriendShipGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendShipGroupMemberRepository extends JpaRepository<FriendShipGroupMember,FriendShipGroupMember.FriendShipGroupMemberId> {


    int deleteByGroupId(Integer groupId);


    int deleteByGroupIdAndToIdIn(Integer groupId, List<String> toIds);
}
