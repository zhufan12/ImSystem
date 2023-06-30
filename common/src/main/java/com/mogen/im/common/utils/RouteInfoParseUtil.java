package com.mogen.im.common.utils;

import com.mogen.im.common.ResponseCode;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.route.RouteInfo;

public class RouteInfoParseUtil {

    public static RouteInfo parse(String info){
        try {
            String[] serverInfo = info.split(":");
            RouteInfo routeInfo = new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1])) ;
            return routeInfo ;
        }catch (Exception e){
            throw new ApplicationException(ResponseCode.PARAMETER_ERROR) ;
        }
    }
}
