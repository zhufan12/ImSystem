package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.codec.pack.friend.*;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.AllowFriendType;
import com.mogen.im.common.enums.CheckFriendShipType;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.common.enums.FriendShipStatus;
import com.mogen.im.common.enums.action.FriendshipEventAction;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.model.RequestBase;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.common.model.SyncResp;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.friendship.entity.FriendShip;
import com.mogen.im.service.friendship.entity.FriendShipId;
import com.mogen.im.service.friendship.model.req.*;
import com.mogen.im.service.friendship.model.resp.CheckFriendShipResp;
import com.mogen.im.service.friendship.model.resp.ImportFriendShipResp;
import com.mogen.im.service.friendship.repository.FriendShipRepository;
import com.mogen.im.service.friendship.service.FriendShipRequestService;
import com.mogen.im.service.friendship.service.FriendShipService;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
import com.mogen.im.service.utils.MessageProducer;
import com.mogen.im.service.utils.UserSessionUtils;
import com.mogen.im.service.utils.WriteUserSeq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogen.im.common.enums.FriendShipErrorCode.*;

@Service
public class FriendShipServiceImpl implements FriendShipService {

    public static final int MAX_FRIEND_SHIP_NUMBER = 100;

    @Autowired
    private FriendShipRepository repository;

    @Autowired
    private FriendShipRequestService friendShipRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisSeq redisSeq;


    @Autowired
    private WriteUserSeq writeUserSeq;

    @Override
    public ResponseVo importFriendShip(ImportFriendShipReq req) {
        if(req.getFriendItem().size() > MAX_FRIEND_SHIP_NUMBER){
            return ResponseVo.errorResponse(IMPORT_SIZE_BEYOND);
        }

        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> failedId = new ArrayList<>();
        req.getFriendItem().stream().forEach(item -> {
            FriendShip friendShip = new FriendShip();
            BeanUtils.copyPropertiesIgnoreNull(item,friendShip);
            friendShip.setToId(item.getToId());
            friendShip.setFromId(req.getFromId());
            friendShip.setAppId(req.getAppId());
            try {
                FriendShip insert = repository.save(friendShip);
                if(insert == null){
                    failedId.add(item.getToId());
                }else {
                    successId.add(item.getToId());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                failedId.add(item.getToId());
            }

        });
        resp.setErrorId(failedId);
        resp.setSuccessId(successId);
        return ResponseVo.successResponse(resp);

    }

    @Override
    public ResponseVo addFiendShip(AddFriendReq addFriendReq) {
        ResponseVo fromUser = userService.getSingleUserInfo(addFriendReq.getFromId(),addFriendReq.getAppId());
        if(!fromUser.isOk()){
            return fromUser;
        }
        ResponseVo toUser = userService.getSingleUserInfo(addFriendReq.getToItem().getToId(),addFriendReq.getAppId());
        if(!toUser.isOk()){
            return toUser;
        }
        User user = (User)toUser.getData();
        if(user.getFriendAllowType() != null && user.getFriendAllowType().equals(AllowFriendType.NOT_NEED)){
            return doAddFriendShip(addFriendReq,addFriendReq.getFromId(),addFriendReq.getToItem(),addFriendReq.getAppId());
        }
        ResponseVo responseVo = friendShipRequestService.addFriendShipRequest(addFriendReq.getFromId(),addFriendReq.getToItem(),addFriendReq.getAppId());
        if(!responseVo.isOk()){
            return responseVo;
        }

        return ResponseVo.successResponse();
    }


    @Transactional
    public ResponseVo doAddFriendShip(RequestBase requestBase, String formId, FriendDto friendDto, Integer appId){

        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setAppId(appId);
        friendShipId.setFromId(formId);
        friendShipId.setToId(friendDto.getToId());
        Optional<FriendShip> friendShip = repository.findById(friendShipId);
        FriendShip fromItem = new FriendShip();
        FriendShip toItem = new FriendShip();
        long seq = 0L;
        if(!friendShip.isPresent()){
            BeanUtils.copyPropertiesIgnoreNull(friendDto,fromItem);
            seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.Friendship);
            fromItem.setFriendSequence(seq);
            fromItem.setAppId(appId);
            fromItem.setFromId(formId);
            fromItem.setToId(friendDto.getToId());
            fromItem.setStatus(FriendShipStatus.FRIEND_STATUS_NORMAL);
            FriendShip insert =  repository.save(fromItem);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(appId,formId,Constants.SeqConstants.Friendship,seq);
        }else{
            if(friendShip.get().getStatus().equals(FriendShipStatus.FRIEND_STATUS_NORMAL)){
                return ResponseVo.errorResponse(TO_IS_YOUR_FRIEND);
            }

            if(friendShip.get().getStatus().equals(FriendShipStatus.FRIEND_STATUS_DELETE)){
                BeanUtils.copyPropertiesIgnoreNull(friendDto,friendShip.get());
                friendShip.get().setStatus(FriendShipStatus.FRIEND_STATUS_NORMAL);
                seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.Friendship);
                friendShip.get().setFriendSequence(seq);
                fromItem = repository.save(friendShip.get());
                if(fromItem == null){
                    return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
                }
                writeUserSeq.writeUserSeq(appId,formId,Constants.SeqConstants.Friendship,seq);
            }
        }


        friendShipId.setToId(formId);
        friendShipId.setFromId(friendDto.getToId());
        Optional<FriendShip> toItemEntity = repository.findById(friendShipId);

        if(!toItemEntity.isPresent()){
            BeanUtils.copyPropertiesIgnoreNull(friendDto,toItem);
            toItem.setAppId(appId);
            toItem.setFromId(friendDto.getToId());
            toItem.setToId(formId);
            seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.Friendship);
            toItem.setFriendSequence(seq);
            toItem.setStatus(FriendShipStatus.FRIEND_STATUS_NORMAL);
            FriendShip insert =  repository.save(toItem);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(appId,formId,Constants.SeqConstants.Friendship,seq);

            return ResponseVo.successResponse();
        }

        if(toItemEntity.get().getStatus().equals(FriendShipStatus.FRIEND_STATUS_NORMAL)){
                return ResponseVo.errorResponse(TO_IS_YOUR_FRIEND);
        }

        if(toItemEntity.get().getStatus().equals(FriendShipStatus.FRIEND_STATUS_DELETE)){
            BeanUtils.copyPropertiesIgnoreNull(friendDto,toItemEntity.get());
            friendShip.get().setStatus(FriendShipStatus.FRIEND_STATUS_NORMAL);
            seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.Friendship);
            friendShip.get().setFriendSequence(seq);
            toItem = repository.save(toItemEntity.get());
            if(toItem == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(appId,formId,Constants.SeqConstants.Friendship,seq);
        }

        AddFriendPack addFriendPack = new AddFriendPack();
        BeanUtils.copyPropertiesIgnoreNull(fromItem,addFriendPack);
        addFriendPack.setSequence(seq);
        messageProducer.sendToUser(formId,requestBase.getClientType(),
                requestBase.getImei(), requestBase.getAppId(),
                FriendshipEventAction.FRIEND_ADD,addFriendPack);

        AddFriendPack addFriendToPack = new AddFriendPack();
        BeanUtils.copyPropertiesIgnoreNull(toItem,addFriendToPack);
        addFriendPack.setSequence(seq);
        messageProducer.sendToUser(toItem.getFromId(),
                FriendshipEventAction.FRIEND_ADD,addFriendToPack,requestBase.getAppId());

        return ResponseVo.successResponse();
    }


    @Override
    public ResponseVo updateFriend(UpdateFriendReq req){
        ResponseVo fromUser = userService.getSingleUserInfo(req.getFromId(),req.getAppId());
        if(!fromUser.isOk()){
            return fromUser;
        }
        ResponseVo toUser = userService.getSingleUserInfo(req.getToItem().getToId(),req.getAppId());
        if(!toUser.isOk()){
            return toUser;
        }

        FriendShipId friendShipId = FriendShipId.builder().appId(req.getAppId())
                .fromId(req.getFromId()).toId(req.getToItem().getToId()).build();
        ResponseVo<Long> responseVo = this.doUpdate(friendShipId,req.getToItem());
        if (responseVo.isOk()){
            UpdateFriendPack updateFriendPack = new UpdateFriendPack();
            updateFriendPack.setRemark(req.getToItem().getRemark());
            updateFriendPack.setToId(req.getToItem().getToId());
            updateFriendPack.setSequence(responseVo.getData());
            messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                    FriendshipEventAction.FRIEND_UPDATE,req.getAppId());
        }
        return responseVo;

    }


    @Transactional
    public ResponseVo doUpdate(FriendShipId friendShipId,FriendDto friendDto){
        long seq = redisSeq.doGetSeq(friendShipId.getAppId() + ":" + Constants.SeqConstants.Friendship);
        Optional<FriendShip> friendShipOptional = repository.findById(friendShipId);
        if(!friendShipOptional.isPresent()){
            return ResponseVo.successResponse(REPEATSHIP_IS_NOT_EXIST);
        }
        friendShipOptional.get().setAddSource(friendDto.getAddSource());
        friendShipOptional.get().setRemark(friendDto.getRemark());
        friendShipOptional.get().setExtra(friendDto.getExtra());
        friendShipOptional.get().setFriendSequence(seq);
        FriendShip friendShip = repository.save(friendShipOptional.get());
        if(friendShip == null){
            return ResponseVo.errorResponse();
        }
        writeUserSeq.writeUserSeq(friendShipId.getAppId(),friendShipId.getFromId(),Constants.SeqConstants.Friendship,seq);
        return ResponseVo.successResponse(seq);
    }

    @Override
    public ResponseVo deleteFriend(DeleteFriendReq req) {
        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setToId(req.getToId());
        friendShipId.setFromId(req.getFromId());
        friendShipId.setAppId(req.getAppId());
        Optional<FriendShip> friendShipOptional = repository.findById(friendShipId);
        if(!friendShipOptional.isPresent()){
            return ResponseVo.errorResponse(TO_IS_NOT_YOUR_FRIEND);
        }

        if(!friendShipOptional.get().getStatus().equals(FriendShipStatus.FRIEND_STATUS_NORMAL)){
            return ResponseVo.errorResponse(FRIEND_IS_DELETED);
        }
        long seq = redisSeq.doGetSeq(friendShipId.getAppId() + ":" + Constants.SeqConstants.Friendship);
        repository.updateStatusById(FriendShipStatus.FRIEND_STATUS_DELETE,friendShipId,seq);
        DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
        deleteFriendPack.setToId(req.getToId());
        deleteFriendPack.setFromId(req.getFromId());
        deleteFriendPack.setSequence(seq);
        messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),FriendshipEventAction.FRIEND_DELETE,
                deleteFriendPack);
        writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);

        return ResponseVo.successResponse();

    }

    @Override
    public ResponseVo deleteAllFriend(DeleteFriendReq req){
        repository.updateStatusByStatusAndFromId(FriendShipStatus.FRIEND_STATUS_DELETE,req.getFromId(),req.getAppId());

        DeleteAllFriendPack deleteAllFriendPack = new DeleteAllFriendPack();
        deleteAllFriendPack.setFromId(req.getFromId());

        messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                FriendshipEventAction.FRIEND_ALL_DELETE,deleteAllFriendPack);

        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo getAllFriendShip(GetAllFriendShipReq req) {
        ResponseVo fromUser = userService.getSingleUserInfo(req.getFromId(),req.getAppId());
        if(!fromUser.isOk()){
            return fromUser;
        }
        List<FriendShip> friendShips = repository.findAllFriendShopByFromIdAndAppId(req.getFromId(),req.getAppId());
        return ResponseVo.successResponse(friendShips);
    }

    @Override
    public ResponseVo getRelation(GetRelationReq req) {
        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setAppId(req.getAppId());
        friendShipId.setFromId(req.getFromId());
        friendShipId.setToId(req.getToId());
        Optional<FriendShip> friendShip = repository.findById(friendShipId);
        if(!friendShip.isPresent()){
            return ResponseVo.errorResponse(FriendShipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVo.successResponse(friendShip.get());
    }

    @Override
    public ResponseVo checkFriendship(CheckFriendShipReq req) {
        List<CheckFriendShipResp> checkFriendShipResp = new ArrayList<>();
        if(req.getCheckType().equals(CheckFriendShipType.SINGLE)){
            checkFriendShipResp = repository.checkSingleFriendShipByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
            return ResponseVo.successResponse(checkFriendShipResp);
        }

        checkFriendShipResp = repository.checkBothFriendShipByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
        return ResponseVo.successResponse(checkFriendShipResp);
    }


    @Override
    @Transactional
    public ResponseVo addBlack(FriendShipBlackReq req){
        ResponseVo fromUser = userService.getSingleUserInfo(req.getFromId(),req.getAppId());
        if(!fromUser.isOk()){
            return fromUser;
        }
        ResponseVo toUser = userService.getSingleUserInfo(req.getToId(),req.getAppId());
        if(!toUser.isOk()){
            return toUser;
        }
        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setToId(req.getToId());
        friendShipId.setFromId(req.getFromId());
        friendShipId.setAppId(req.getAppId());
        Optional<FriendShip> friendShipOptional = repository.findById(friendShipId);

        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setFromId(req.getFromId());
        addFriendBlackPack.setToId(req.getToId());
        long seq = 0;
        if(!friendShipOptional.isPresent()){
             seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);
            FriendShip friendShipEntity = new FriendShip();
            friendShipEntity.setAppId(req.getAppId());
            friendShipEntity.setToId(req.getToId());
            friendShipEntity.setFriendSequence(seq);
            friendShipEntity.setFromId(req.getFromId());
            friendShipEntity.setStatus(FriendShipStatus.FRIEND_STATUS_NORMAL);
            friendShipEntity.setBlack(FriendShipStatus.BLACK_STATUS_BLACKED);
            FriendShip insert = repository.save(friendShipEntity);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);
            addFriendBlackPack.setSequence(seq);
            messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                    FriendshipEventAction.FRIEND_BLACK_ADD,addFriendBlackPack);
            return  ResponseVo.successResponse();
        }

        if(friendShipOptional.get().getBlack().equals(FriendShipStatus.BLACK_STATUS_BLACKED)){
            return ResponseVo.errorResponse(FRIEND_IS_BLACK);
        }

        seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);
        int update =  repository.updateBlackById(FriendShipStatus.BLACK_STATUS_BLACKED,friendShipId,seq);
        if(update == 0){
            return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
        }
        writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);
        addFriendBlackPack.setSequence(seq);
        messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                FriendshipEventAction.FRIEND_BLACK_ADD,addFriendBlackPack);
        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo deleteBlack(FriendShipBlackReq req){
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);
        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setToId(req.getToId());
        friendShipId.setFromId(req.getFromId());
        friendShipId.setAppId(req.getAppId());
        FriendShip friendShip= repository.findById(friendShipId).get();
        if(friendShip.getStatus().equals(FriendShipStatus.BLACK_STATUS_NORMAL)){
            throw new ApplicationException(FRIEND_IS_NOT_YOUR_BLACK);
        }
        int update = repository.updateStatusById(FriendShipStatus.BLACK_STATUS_NORMAL,friendShipId,seq);
        if(update == 0){
            return ResponseVo.errorResponse(DELETE_BLACK_ERROR);
        }
        writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);
        DeleteBlackPack deleteBlackPack = new DeleteBlackPack();
        deleteBlackPack.setSequence(seq);
        deleteBlackPack.setFromId(req.getFromId());
        deleteBlackPack.setToId(req.getToId());
        messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                FriendshipEventAction.FRIEND_DELETE,deleteBlackPack);
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo checkBlack(CheckFriendShipReq req) {
        List<CheckFriendShipResp> checkFriendShipResp = new ArrayList<>();
        if(req.getCheckType().equals(CheckFriendShipType.SINGLE)){
            checkFriendShipResp = repository.checkSingleFriendShipBlackByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
            return ResponseVo.successResponse(checkFriendShipResp);
        }

        checkFriendShipResp = repository.checkBothFriendShipBlackByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
        return ResponseVo.successResponse(checkFriendShipResp);
    }


    @Override
    public ResponseVo syncFriendshipList(SyncReq syncReq) {
        if(syncReq.getMaxLimit()  > 100){
            syncReq.setMaxLimit(100);
        }

        SyncResp<FriendShip> friendShipSyncResp = new SyncResp<>();
        List<FriendShip> friendShips = repository.findBySeqAndLimit(syncReq.getOperator(),syncReq.getAppId(),syncReq.getLastSequence(),syncReq.getMaxLimit());
        if (friendShips != null && !friendShips.isEmpty()){
            FriendShip maxFriendShip = friendShips.get(friendShips.size() -1);
            friendShipSyncResp.setDataList(friendShips);
            Long maxSeq = repository.findMaxSeqByFromIdAndAppId(syncReq.getOperator(),syncReq.getAppId());
            BeanUtils.copyPropertiesIgnoreNull(syncReq,friendShipSyncResp);
            friendShipSyncResp.setMaxSequence(maxSeq);
            friendShipSyncResp.setCompleted(maxFriendShip.getFriendSequence() >= maxSeq);
            return ResponseVo.successResponse(friendShipSyncResp);
        }
        return ResponseVo.successResponse();
    }
}
