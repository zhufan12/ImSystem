package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.AllowFriendTypeEnum;
import com.mogen.im.common.enums.CheckFriendShipTypeEnum;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.common.enums.FriendShipStatusEnum;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.friendship.entity.FriendShip;
import com.mogen.im.service.friendship.entity.FriendShipId;
import com.mogen.im.service.friendship.model.req.*;
import com.mogen.im.service.friendship.model.resp.CheckFriendShipResp;
import com.mogen.im.service.friendship.model.resp.ImportFriendShipResp;
import com.mogen.im.service.friendship.repository.FriendShipRepository;
import com.mogen.im.service.friendship.service.FriendShipRequestService;
import com.mogen.im.service.friendship.service.FriendShipService;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
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
        if(user.getFriendAllowType() != null && user.getFriendAllowType().equals(AllowFriendTypeEnum.NOT_NEED)){
            return doAddFriendShip(addFriendReq.getFromId(),addFriendReq.getToItem(),addFriendReq.getAppId());
        }
        ResponseVo responseVo = friendShipRequestService.addFriendShipRequest(addFriendReq.getFromId(),addFriendReq.getToItem(),addFriendReq.getAppId());
        if(!responseVo.isOk()){
            return responseVo;
        }

        return ResponseVo.successResponse();
    }


    @Transactional
    public ResponseVo doAddFriendShip(String formId, FriendDto friendDto,Integer appId){

        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setAppId(appId);
        friendShipId.setFromId(formId);
        friendShipId.setToId(friendDto.getToId());
        Optional<FriendShip> friendShip = repository.findById(friendShipId);

        if(!friendShip.isPresent()){
            FriendShip fromItem = new FriendShip();
            BeanUtils.copyPropertiesIgnoreNull(friendDto,fromItem);
            fromItem.setAppId(appId);
            fromItem.setFromId(formId);
            fromItem.setToId(friendDto.getToId());
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
            FriendShip insert =  repository.save(fromItem);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
        }else{
            if(friendShip.get().getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_NORMAL)){
                return ResponseVo.errorResponse(TO_IS_YOUR_FRIEND);
            }

            if(friendShip.get().getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_DELETE)){
                BeanUtils.copyPropertiesIgnoreNull(friendDto,friendShip.get());
                friendShip.get().setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
                FriendShip updateEntity = repository.save(friendShip.get());
                if(updateEntity == null){
                    return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
                }
            }
        }


        friendShipId.setToId(formId);
        friendShipId.setFromId(friendDto.getToId());
        Optional<FriendShip> toItemEntity = repository.findById(friendShipId);

        if(!toItemEntity.isPresent()){
            FriendShip fromItem = new FriendShip();
            BeanUtils.copyPropertiesIgnoreNull(friendDto,fromItem);
            fromItem.setAppId(appId);
            fromItem.setFromId(friendDto.getToId());
            fromItem.setToId(formId);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
            FriendShip insert =  repository.save(fromItem);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            return ResponseVo.successResponse();
        }

        if(toItemEntity.get().getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_NORMAL)){
            return ResponseVo.errorResponse(TO_IS_YOUR_FRIEND);
        }

        if(toItemEntity.get().getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_DELETE)){
            BeanUtils.copyPropertiesIgnoreNull(friendDto,toItemEntity.get());
            friendShip.get().setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
            FriendShip updateEntity = repository.save(toItemEntity.get());
            if(updateEntity == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
        }

        return ResponseVo.successResponse();
    }


    @Override
    public ResponseVo updateFriend(UpdateFriendReq req) {
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

        return doUpdate(friendShipId,req.getToItem());

    }


    @Transactional
    public ResponseVo doUpdate(FriendShipId friendShipId,FriendDto friendDto){
        Optional<FriendShip> friendShipOptional = repository.findById(friendShipId);
        if(!friendShipOptional.isPresent()){
            return ResponseVo.successResponse(REPEATSHIP_IS_NOT_EXIST);
        }
        friendShipOptional.get().setAddSource(friendDto.getAddSource());
        friendShipOptional.get().setRemark(friendDto.getRemark());
        friendShipOptional.get().setExtra(friendDto.getExtra());
        FriendShip friendShip = repository.save(friendShipOptional.get());
        if(friendShip == null){
            return ResponseVo.errorResponse();
        }
        return ResponseVo.successResponse();
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

        if(!friendShipOptional.get().getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_NORMAL)){
            return  ResponseVo.errorResponse(FRIEND_IS_DELETED);
        }
        repository.updateStatusById(FriendShipStatusEnum.FRIEND_STATUS_DELETE,friendShipId);


        return ResponseVo.successResponse();

    }

    @Override
    public ResponseVo deleteAllFriend(DeleteFriendReq req) {
        repository.updateStatusByStatusAndFromId(FriendShipStatusEnum.FRIEND_STATUS_DELETE,req.getFromId(),req.getAppId());
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
        if(req.getCheckType().equals(CheckFriendShipTypeEnum.SINGLE)){
            checkFriendShipResp = repository.checkSingleFriendShipByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
            return ResponseVo.successResponse(checkFriendShipResp);
        }

        checkFriendShipResp = repository.checkBothFriendShipByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
        return ResponseVo.successResponse(checkFriendShipResp);
    }


    @Override
    @Transactional
    public ResponseVo addBlack(FriendShipBlackReq req) {
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
        if(!friendShipOptional.isPresent()){
            FriendShip friendShipEntity = new FriendShip();
            friendShipEntity.setAppId(req.getAppId());
            friendShipEntity.setToId(req.getToId());
            friendShipEntity.setFromId(req.getFromId());
            friendShipEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
            friendShipEntity.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED);
            FriendShip insert = repository.save(friendShipEntity);
            if(insert == null){
                return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
            }
            return  ResponseVo.successResponse();
        }

        if(friendShipOptional.get().getBlack().equals(FriendShipStatusEnum.BLACK_STATUS_BLACKED)){
            return ResponseVo.errorResponse(FRIEND_IS_BLACK);
        }

        int update =  repository.updateBlackById(FriendShipStatusEnum.BLACK_STATUS_BLACKED,friendShipId);
        if(update == 0){
            return ResponseVo.errorResponse(ADD_FRIEND_ERROR);
        }
        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo deleteBlack(FriendShipBlackReq req) {
        FriendShipId friendShipId = new FriendShipId();
        friendShipId.setToId(req.getToId());
        friendShipId.setFromId(req.getFromId());
        friendShipId.setAppId(req.getAppId());
        FriendShip friendShip= repository.findById(friendShipId).get();
        if(friendShip.getStatus().equals(FriendShipStatusEnum.BLACK_STATUS_NORMAL)){
            throw new ApplicationException(FRIEND_IS_NOT_YOUR_BLACK);
        }
        repository.updateStatusById(FriendShipStatusEnum.BLACK_STATUS_NORMAL,friendShipId);
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo checkBlack(CheckFriendShipReq req) {
        List<CheckFriendShipResp> checkFriendShipResp = new ArrayList<>();
        if(req.getCheckType().equals(CheckFriendShipTypeEnum.SINGLE)){
            checkFriendShipResp = repository.checkSingleFriendShipBlackByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
            return ResponseVo.successResponse(checkFriendShipResp);
        }

        checkFriendShipResp = repository.checkBothFriendShipBlackByFromIdAndToIds(req.getFromId(),req.getAppId(),req.getToIds());
        return ResponseVo.successResponse(checkFriendShipResp);
    }
}
