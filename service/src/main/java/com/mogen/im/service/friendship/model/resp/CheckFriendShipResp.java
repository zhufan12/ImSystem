package com.mogen.im.service.friendship.model.resp;

import com.mogen.im.common.enums.FriendShipStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class CheckFriendShipResp  implements Serializable {


    private String fromId;


    private String toId;

    private Integer status;

    public CheckFriendShipResp(String fromId, String toId, Integer status) {
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
    }

    public CheckFriendShipResp(String fromId, String toId, FriendShipStatusEnum status) {
        this.fromId = fromId;
        this.toId = toId;
        this.status = status.ordinal();
    }
}
