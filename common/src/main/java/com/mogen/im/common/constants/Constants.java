package com.mogen.im.common.constants;

public class Constants {

    public static final String UserId = "userId";

    public static final String AppId = "appId";

    public static final String ClientType = "clientType";

    public static final String Imei = "imei";

    public static final String ClientImei = "clientImei";

    public static final String ReadTime = "readTime";

    public static final String ImCoreZkRoot = "/im-coreRoot";

    public static final String ImCoreZkRootTcp = "/tcp";

    public static final String ImCoreZkRootWeb = "/web";


    public static class RedisConstants{

        public static final String userSign = "userSign";

        public static final String UserLoginChannel
                = "signal/channel/LOGIN_USER_INNER_QUEUE";

        public static final String UserSessionConstants = ":userSession:";

        public static final String cacheMessage = "cacheMessage";

        public static final String OfflineMessage = "offlineMessage";

        public static final String SeqPrefix = "seq";


        public static final String subscribe = "subscribe";

        public static final String userCustomerStatus = "userCustomerStatus";

    }

    public static class RabbitConstants{



        public static final String ImToUserService = "imToPipelineUserService";

        public static final String ImToMessageService = "imToPipelineMessageService";

        public static final String ImToGroupService = "imToPipelineGroupService";

        public static final String ImToFriendshipService = "imToPipelineFriendshipService";

        public static final String MessageServiceToIm = "messageServiceToPipeline";

        public static final String GroupServiceToIm = "GroupServiceToPipeline";

        public static final String FriendShipToIm = "friendShipToPipeline";

        public static final String StorePToPMessage = "storePToPMessage";

        public static final String StoreGroupMessage = "storeGroupMessage";


    }

    public static class SeqConstants {
        public static final String Message = "messageSeq";

        public static final String GroupMessage = "groupMessageSeq";


        public static final String Friendship = "friendshipSeq";

        public static final String FriendshipRequest = "friendshipRequestSeq";

        public static final String FriendshipGroup = "friendshipGrouptSeq";

        public static final String Group = "groupSeq";

        public static final String Conversation = "conversationSeq";

    }
}
