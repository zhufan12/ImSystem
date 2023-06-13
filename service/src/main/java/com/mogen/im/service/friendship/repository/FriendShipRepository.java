package com.mogen.im.service.friendship.repository;

import com.mogen.im.common.enums.FriendShipStatusEnum;
import com.mogen.im.service.friendship.entity.FriendShip;
import com.mogen.im.service.friendship.entity.FriendShipId;
import com.mogen.im.service.friendship.model.resp.CheckFriendShipResp;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, FriendShipId> {

    Optional<FriendShip> findById(FriendShipId friendShipId);

    @Modifying
    @Query(value = "UPDATE FriendShip SET status = :statusEnum WHERE appId = :#{#friendShipId.appId} and fromId = :#{#friendShipId.fromId} and  toId = :#{#friendShipId.toId} ")
    int updateStatusById(FriendShipStatusEnum statusEnum,@Param("friendShipId") FriendShipId friendShipId);

    @Modifying
    @Query(value = "UPDATE FriendShip SET black = :statusEnum WHERE appId = :#{#friendShipId.appId} and fromId = :#{#friendShipId.fromId} and  toId = :#{#friendShipId.toId} ")
    int updateBlackById(FriendShipStatusEnum statusEnum,@Param("friendShipId") FriendShipId friendShipId);


    @Modifying
    @Query("UPDATE FriendShip SET status = :friendShipStatusEnum WHERE status = 1 AND fromId = :fromId AND appId = :appId")
    int updateStatusByStatusAndFromId(FriendShipStatusEnum friendShipStatusEnum, String fromId,Integer appId);

    @Query(value = "SELECT * FROM `im-friend-ship` WHERE from_id = :fromId AND app_id = :appId",nativeQuery = true)
    List<FriendShip> findAllFriendShopByFromIdAndAppId(String fromId, Integer appId);

    @Query(value = "SELECT new com.mogen.im.service.friendship.model.resp.CheckFriendShipResp(fromId,toId,status)  FROM FriendShip" +
            "   WHERE fromId = :fromId and toId in (:toId) and appId = :appId" , nativeQuery = false)
    List<CheckFriendShipResp> checkSingleFriendShipByFromIdAndToIds(String fromId, Integer appId, List<String> toId);


    @Query("SELECT new com.mogen.im.service.friendship.model.resp.CheckFriendShipResp(a.from_id,a.to_id," +
            " (  " +
            "    case " +
            "    when a.status1 = 1 and b.status1 = 1 then 1 " +
            "    when a.status1 = 1 and b.status1 <> 1 then 2 " +
            "    when a.status1 <> 1 and b.status1 = 1 then 3  " +
            "    when a.status1 <> 1 and b.status1 <> 1 then 4  " +
            "    end  " +
            "     )  "+
            "   as status" +
            ")  FROM " +
            "( " +
            " SELECT fromId as from_id,toId as to_id,status as status1 FROM FriendShip WHERE fromId = :fromId and toId in (:toId) and appId = :appId " +
            " ) AS a" +
            " INNER JOIN " +
            "( " +
            " SELECT fromId as from_id,toId as to_id,status as status1 FROM FriendShip WHERE toId = :fromId and fromId in (:toId) and appId = :appId  " +
            " ) AS b " +
            " on a.from_id = b.to_id AND b.from_id = a.to_id ")
    List<CheckFriendShipResp> checkBothFriendShipByFromIdAndToIds(String fromId,Integer appId,List<String> toId);

    @Query(value = "SELECT new com.mogen.im.service.friendship.model.resp.CheckFriendShipResp(fromId,toId,black)  FROM FriendShip" +
            "   WHERE fromId = :fromId and toId in (:toId) and appId = :appId" , nativeQuery = false)
    List<CheckFriendShipResp> checkSingleFriendShipBlackByFromIdAndToIds(String fromId, Integer appId, List<String> toId);


    @Query("SELECT new com.mogen.im.service.friendship.model.resp.CheckFriendShipResp(a.from_id,a.to_id," +
            " (  " +
            "    case " +
            "    when a.black1 = 4 and b.black1 = 4 then 1 " +
            "    when a.black1 = 4 and b.black1 <> 4 then 2 " +
            "    when a.black1 <> 4 and b.black1 = 4 then 3  " +
            "    when a.black1 <> 4 and b.black1 <> 4 then 4  " +
            "    end  " +
            "     )  "+
            "   as status" +
            ")  FROM " +
            "( " +
            " SELECT fromId as from_id,toId as to_id,black as black1 FROM FriendShip WHERE fromId = :fromId and toId in (:toId) and appId = :appId " +
            " ) AS a" +
            " INNER JOIN " +
            "( " +
            " SELECT fromId as from_id,toId as to_id,black as black1 FROM FriendShip WHERE toId = :fromId and fromId in (:toId) and appId = :appId  " +
            " ) AS b " +
            " on a.from_id = b.to_id AND b.from_id = a.to_id ")
    List<CheckFriendShipResp> checkBothFriendShipBlackByFromIdAndToIds(String fromId,Integer appId,List<String> toId);
}

