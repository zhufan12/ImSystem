package com.mogen.im.service.friendship.service;


import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.model.req.*;

public interface FriendShipService {

    public ResponseVo importFriendShip(ImportFriendShipReq req);

    public ResponseVo addFiendShip(AddFriendReq addFriendReq);

    public ResponseVo updateFriend(UpdateFriendReq req);

    public ResponseVo deleteFriend(DeleteFriendReq req);

    public ResponseVo deleteAllFriend(DeleteFriendReq req);


    public ResponseVo getAllFriendShip(GetAllFriendShipReq req);

    public ResponseVo getRelation(GetRelationReq req);

    public ResponseVo checkFriendship(CheckFriendShipReq req);

    public ResponseVo addBlack(FriendShipBlackReq req);

    public ResponseVo deleteBlack(FriendShipBlackReq req);

    public ResponseVo checkBlack(CheckFriendShipReq req);


    public ResponseVo doAddFriendShip(String formId, FriendDto friendDto,Integer appId);

}
