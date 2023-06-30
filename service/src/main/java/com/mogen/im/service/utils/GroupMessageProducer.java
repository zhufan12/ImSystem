package com.mogen.im.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.pack.group.AddGroupMemberPack;
import com.mogen.im.codec.pack.group.RemoveGroupMemberPack;
import com.mogen.im.codec.pack.group.UpdateGroupMemberPack;
import com.mogen.im.common.enums.ClientType;
import com.mogen.im.common.enums.GroupEventAction;
import com.mogen.im.common.enums.action.Action;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.service.group.entity.GroupMember;
import com.mogen.im.service.group.modle.req.GroupMemberDto;
import com.mogen.im.service.group.service.GroupMemberService;
import com.mogen.im.service.group.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMessageProducer {


    private static final Logger logger = LoggerFactory.getLogger(GroupMessageProducer.class);

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Lazy
    private GroupMemberService groupMemberService;

    public void producer(String userId, Action action, Object data,
                         ClientInfo clientInfo){

        try {
            String jsonStr = objectMapper.writeValueAsString(data);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            Integer groupId = jsonNode.get("groupId").asInt();
            List<String> groupMemberId = groupMemberService
                    .getGroupMemberId(groupId);
            if(action.equals(GroupEventAction.ADDED_MEMBER)){
                List<GroupMember> groupManager = groupMemberService.getGroupManager(groupId);
                AddGroupMemberPack addGroupMemberPack
                        = (AddGroupMemberPack) data;
                List<String> members = addGroupMemberPack.getMembers();
                for (GroupMember groupMemberDto : groupManager) {
                    if(clientInfo.getClientType() != ClientType.WEBAPI.ordinal() && groupMemberDto.getUserId().equals(userId)){
                        messageProducer.sendToUserExceptClient(groupMemberDto.getUserId(),action,data,clientInfo);
                    }else{
                        messageProducer.sendToUser(groupMemberDto.getUserId(),action,data,clientInfo.getAppId());
                    }
                }
                for (String member : members) {
                    if(clientInfo.getClientType() != ClientType.WEBAPI.ordinal() && member.equals(userId)){
                        messageProducer.sendToUserExceptClient(member,action,data,clientInfo);
                    }else{
                        messageProducer.sendToUser(member,action,data,clientInfo.getAppId());
                    }
                }
            }else if(action.equals(GroupEventAction.DELETED_MEMBER)){
                RemoveGroupMemberPack pack = (RemoveGroupMemberPack)data;
                String member = pack.getMember();
                List<String> members = groupMemberService.getGroupMemberId(groupId);
                members.add(member);
                for (String memberId : members) {
                    if(clientInfo.getClientType() != ClientType.WEBAPI.ordinal() && member.equals(userId)){
                        messageProducer.sendToUserExceptClient(memberId,action,data,clientInfo);
                    }else{
                        messageProducer.sendToUser(memberId,action,data,clientInfo.getAppId());
                    }
                }
            }else if(action.equals(GroupEventAction.UPDATED_MEMBER)){
                UpdateGroupMemberPack pack = (UpdateGroupMemberPack) data;
                String memberId = pack.getMemberId();
                List<GroupMember> groupManager = groupMemberService.getGroupManager(groupId);
                GroupMember GroupMember = new GroupMember();
                GroupMember.setUserId(memberId);
                groupManager.add(GroupMember);
                for (GroupMember member : groupManager) {
                    if(clientInfo.getClientType() != ClientType.WEBAPI.ordinal() && member.equals(userId)){
                        messageProducer.sendToUserExceptClient(member.getUserId(),action,data,clientInfo);
                    }else{
                        messageProducer.sendToUser(member.getUserId(),action,data,clientInfo.getAppId());
                    }
                }
            }else {
                for (String memberId : groupMemberId) {
                    if(clientInfo.getClientType() != null && clientInfo.getClientType() !=
                            ClientType.WEBAPI.ordinal() && memberId.equals(userId)){
                        messageProducer.sendToUserExceptClient(memberId,action,
                                data,clientInfo);
                    }else{
                        messageProducer.sendToUser(memberId,action,data,clientInfo.getAppId());
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("Group producer failed : [{}]",e.getMessage());
        }

    }

}
