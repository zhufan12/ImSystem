package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.enums.FriendShipStatusEnum;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ImportFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId can't not be null")
    private String fromId;

    @NotNull(message = "friendItem can't be null")
    private List<ImportFriendDto> friendItem;

    @Data
    public static class ImportFriendDto{

        private String toId;

        private String remark;

        private String addSource;

        private Integer status = FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.ordinal();

        private Integer black = FriendShipStatusEnum.BLACK_STATUS_NORMAL.ordinal();
    }



}
