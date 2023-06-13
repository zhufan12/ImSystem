package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.ApprovedFriendRequestStatusEnum;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.service.friendship.entity.FriendShipRequest;
import com.mogen.im.service.friendship.model.req.ApprovedFriendRequestReq;
import com.mogen.im.service.friendship.model.req.FriendDto;
import com.mogen.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.mogen.im.service.friendship.repository.FriendShipRequestRepository;
import com.mogen.im.service.friendship.service.FriendShipRequestService;
import com.mogen.im.service.friendship.service.FriendShipService;
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

    @Override
    public ResponseVo addFriendShipRequest(String fromId, FriendDto dto, Integer appId) {
        Optional<FriendShipRequest> friendShipRequest = repository.findByAppIdAndFromIdAndToId(appId,fromId,dto.getToId());
        if(!friendShipRequest.isPresent()){
            FriendShipRequest friendShipRequestEntity = new FriendShipRequest();
            BeanUtils.copyProperties(dto,friendShipRequestEntity);
            friendShipRequestEntity.setFromId(fromId);
            friendShipRequestEntity.setAppId(appId);
            repository.save(friendShipRequestEntity);
            return ResponseVo.successResponse();
        }
        BeanUtils.copyProperties(dto,friendShipRequest.get());
        repository.save(friendShipRequest.get());
        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo approvedFriendRequest(ApprovedFriendRequestReq req) {
        Optional<FriendShipRequest> friendShipRequest = repository.findById(req.getId());
        if (!friendShipRequest.isPresent()){
            ResponseVo.errorResponse(FRIEND_REQUEST_IS_NOT_EXIST);
        }

        repository.updateApproveStatusById(req.getStatus(),req.getId());

        if(ApprovedFriendRequestStatusEnum.AGREE.ordinal() == req.getStatus()){
            FriendDto dto = new FriendDto();
            dto.setAddSource(friendShipRequest.get().getAddSource());
            dto.setAddWording(friendShipRequest.get().getAddWording());
            dto.setRemark(friendShipRequest.get().getRemark());
            dto.setToId(friendShipRequest.get().getToId());
            ResponseVo responseVO = friendShipService.doAddFriendShip(friendShipRequest.get().getFromId(), dto,req.getAppId());
            if(!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()){
                return responseVO;
            }
        }

        return ResponseVo.successResponse();

    }

    @Override
    @Transactional
    public ResponseVo readFriendShipRequestReq(GetFriendShipRequestReq req) {
        repository.updateReadStatusByToIdAndAppId(1,req.getFromId(),req.getAppId());
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo getFriendRequest(String fromId, Integer appId) {
        List<FriendShipRequest> friendShipRequests = repository.findByAppIdAndToId(appId,fromId);
        return ResponseVo.successResponse(friendShipRequests);
    }
}
