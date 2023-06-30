package com.mogen.im.service.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.ResponseCode;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.GateWayErrorCode;
import com.mogen.im.common.exception.ApplicationExceptionEnum;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;


@Component
public class GateWayInterceptor implements HandlerInterceptor {

    @Autowired
    private  IdentityCheck identityCheck;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appIdStr = request.getParameter("appId");
        if(StringUtils.isBlank(appIdStr)){
            resp(ResponseVo.errorResponse(GateWayErrorCode
                    .APPID_NOT_EXIST),response);
            return false;
        }

        String identifier = request.getParameter("identifier");
        if(StringUtils.isBlank(identifier)){
            resp(ResponseVo.errorResponse(GateWayErrorCode
                    .OPERATER_NOT_EXIST),response);
            return false;
        }

        String userSign = request.getParameter("userSign");
        if(StringUtils.isBlank(userSign)){
            resp(ResponseVo.errorResponse(GateWayErrorCode
                    .USERSIGN_NOT_EXIST),response);
            return false;
        }

        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSig(identifier, appIdStr, userSign);
        if(applicationExceptionEnum != ResponseCode.SUCCESSD){
            resp(ResponseVo.errorResponse(applicationExceptionEnum),response);
            return false;
        }

        return true;
    }


    private void resp(ResponseVo respVo ,HttpServletResponse response){

        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            String resp = objectMapper.writeValueAsString(respVo);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Credentials","true");
            response.setHeader("Access-Control-Allow-Methods","*");
            response.setHeader("Access-Control-Allow-Headers","*");
            response.setHeader("Access-Control-Max-Age","3600");

            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.checkError();
            }
        }
    }
}
