package com.mogen.im.common;


import com.mogen.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseVo<T> {

    private int code = ResponseCode.SUCCESSD.getCode();

    private String msg = ResponseCode.SUCCESSD.getMsg();

    private T data;


    public static ResponseVo successResponse(Object data) {
        return new ResponseVo(200, "success", data);
    }

    public static ResponseVo successResponse() {
        return new ResponseVo(200, "success");
    }

    public static ResponseVo errorResponse() {
        return new ResponseVo(500, "System Error");
    }

    public static ResponseVo errorResponse(int code, String msg) {
        return new ResponseVo(code, msg);
    }

    public static ResponseVo errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVo(enums.getCode(), enums.getMsg());
    }

    public boolean isOk(){
        return this.code == 200;
    }


    public ResponseVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
//		this.data = null;
    }

    public ResponseVo success(){
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVo success(T data){
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }
}
