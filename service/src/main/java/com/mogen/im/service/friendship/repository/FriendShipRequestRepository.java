package com.mogen.im.service.friendship.repository;

import com.mogen.im.service.friendship.entity.FriendShipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRequestRepository extends JpaRepository<FriendShipRequest,Integer> {

    Optional<FriendShipRequest> findByAppIdAndFromIdAndToId(Integer appId,String fromId,String toId);

    List<FriendShipRequest> findByAppIdAndToId(Integer appId, String fromId);

    @Query("UPDATE FriendShipRequest SET approveStatus = :approveStatus,sequence = :seq WHERE id = :id ")
    @Modifying
    int updateApproveStatusById(Integer approveStatus,Integer id,long seq);

    @Query("UPDATE FriendShipRequest SET readStatus = :status WHERE toId = :toId AND  appId = :appId ")
    @Modifying
    int updateReadStatusByToIdAndAppId(Integer status,String toId,Integer appId,long seq);
}
