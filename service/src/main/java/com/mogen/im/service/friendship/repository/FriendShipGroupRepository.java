package com.mogen.im.service.friendship.repository;

import com.mogen.im.common.enums.DelFlagEnum;
import com.mogen.im.service.friendship.entity.FriendShipGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendShipGroupRepository extends JpaRepository<FriendShipGroup,Integer> {

    Optional<FriendShipGroup> findByAppIdAndFromIdAndGroupName(Integer appId,String fromId,String groupName);

    @Query("UPDATE FriendShipGroup SET delFlag = :delFlagEnum WHERE fromId =:fromId AND appId = :appId AND groupName = :groupName")
    FriendShipGroup updateDelFlagByFromIdAndAppIdAndGroupName(DelFlagEnum delFlagEnum, String fromId, Integer appId, String groupName);

}
