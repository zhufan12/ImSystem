package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.codec.pack.friend.ApproverFriendRequestPack;
import com.mogen.im.codec.pack.friend.ReadAllFriendRequestPack;
import com.mogen.im.codec.proto.Message;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ApprovedFriendRequestStatus;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.common.enums.action.FriendshipEventAction;
import com.mogen.im.service.friendship.entity.FriendShipRequest;
import com.mogen.im.service.friendship.model.req.ApprovedFriendRequestReq;
import com.mogen.im.service.friendship.model.req.FriendDto;
import com.mogen.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.mogen.im.service.friendship.repository.FriendShipRequestRepository;
import com.mogen.im.service.friendship.service.FriendShipRequestService;
import com.mogen.im.service.friendship.service.FriendShipService;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.utils.MessageProducer;
import com.mogen.im.service.utils.WriteUserSeq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mogen.im.common.enums.FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST;

@Service
public class FriendShipRequestServiceImpl implements FriendShipRequestService {


    @Autowired
    private FriendShipRequestRepository repository;

    @Autowired
    @Lazy
    private FriendShipService friendShipService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisSeq redisSeq;

    @Autowired
    private WriteUserSeq writeUserSeq;

    @Override
    public ResponseVo addFriendShipRequest(String fromId, FriendDto dto, Integer appId) {
        Optional<FriendShipRequest> friendShipRequest = repository.findByAppIdAndFromIdAndToId(appId,fromId,dto.getToId());
        long seq;
        if(!friendShipRequest.isPresent()){
            FriendShipRequest friendShipRequestEntity = new FriendShipRequest();
            BeanUtils.copyProperties(dto,friendShipRequestEntity);
            friendShipRequestEntity.setFromId(fromId);
            friendShipRequestEntity.setAppId(appId);
            seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.FriendshipRequest);
            friendShipRequestEntity.setSequence(seq);
            repository.save(friendShipRequestEntity);
            writeUserSeq.writeUserSeq(appId,dto.getToId(),Constants.SeqConstants.FriendshipRequest,seq);
        }else {
            seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.FriendshipRequest);
            BeanUtils.copyProperties(dto,friendShipRequest.get());
            friendShipRequest.get().setSequence(seq);
            repository.save(friendShipRequest.get());
            writeUserSeq.writeUserSeq(appId,dto.getToId(),Constants.SeqConstants.FriendshipRequest,seq);

        }
        messageProducer.sendToUser(dto.getToId(),null,null,appId,
                FriendshipEventAction.FRIEND_REQUEST,friendShipRequest.get());

        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo approvedFriendRequest(ApprovedFriendRequestReq req) {
        Optional<FriendShipRequest> friendShipRequest = repository.findById(req.getId());
        if (!friendShipRequest.isPresent()){
           return ResponseVo.errorResponse(FRIEND_REQUEST_IS_NOT_EXIST);
        }
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipRequest);

        repository.updateApproveStatusById(req.getStatus(),req.getId(),seq);
        writeUserSeq.writeUserSeq(req.getAppId(),req.getOperator(),Constants.SeqConstants.FriendshipRequest,seq);
        if(ApprovedFriendRequestStatus.AGREE.ordinal() == req.getStatus()){
            FriendDto dto = new FriendDto();
            dto.setAddSource(friendShipRequest.get().getAddSource());
            dto.setAddWording(friendShipRequest.get().getAddWording());
            dto.setRemark(friendShipRequest.get().getRemark());
            dto.setToId(friendShipRequest.get().getToId());
            ResponseVo responseVO = friendShipService.doAddFriendShip(req,friendShipRequest.get().getFromId(), dto,req.getAppId());
            if(!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()){
                return responseVO;
            }
        }
        ApproverFriendRequestPack approverFriendRequestPack = new ApproverFriendRequestPack();
        approverFriendRequestPack.setId(req.getId());
        approverFriendRequestPack.setStatus(req.getStatus());
        approverFriendRequestPack.setSequence(seq);

        messageProducer.sendToUser(friendShipRequest.get().getToId(),req.getClientType(),req.getImei(),req.getAppId(),
                FriendshipEventAction.FRIEND_REQUEST_APPROVER,approverFriendRequestPack);
        return ResponseVo.successResponse();

    }

    @Override
    @Transactional
    public ResponseVo readFriendShipRequestReq(GetFriendShipRequestReq req) {
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipRequest);
        repository.updateReadStatusByToIdAndAppId(1,req.getFromId(),req.getAppId(),seq);
        ReadAllFriendRequestPack readAllFriendRequestPack = new ReadAllFriendRequestPack();
        readAllFriendRequestPack.setFromId(readAllFriendRequestPack.getFromId());
        readAllFriendRequestPack.setSequence(seq);
        writeUserSeq.writeUserSeq(req.getAppId(),req.getOperator(),Constants.SeqConstants.FriendshipRequest,seq);
        messageProducer.sendToUser(req.getFromId(),req.getClientType(),req.getImei(),req.getAppId(),
                FriendshipEventAction.FRIEND_REQUEST_READ,readAllFriendRequestPack);
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo getFriendRequest(String fromId, Integer appId) {
        List<FriendShipRequest> friendShipRequests = repository.findByAppIdAndToId(appId,fromId);
        return ResponseVo.successResponse(friendShipRequests);
    }
}
