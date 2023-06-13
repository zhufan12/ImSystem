package com.mogen.im.service.user.exception;

import com.mogen.im.common.ResponseCode;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.exception.ApplicationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

import static com.mogen.im.common.ResponseCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value= Exception.class)
    @ResponseBody
    public ResponseVo unknowException(Exception e){
        e.printStackTrace();
        return ResponseVo.builder().code(SYSTEM_ERROR.getCode()).msg(SYSTEM_ERROR.getMsg()).build();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseVo handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ResponseVo resultBean = new ResponseVo();
        resultBean.setCode(ResponseCode.PARAMETER_ERROR.getCode());
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            String paramName = pathImpl.getLeafNode().getName();
            String message = "param {".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            resultBean.setMsg(message);
            return resultBean;
        }
        resultBean.setMsg(ResponseCode.PARAMETER_ERROR.getMsg() + ex.getMessage());
        return resultBean;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseVo applicationExceptionHandler(ApplicationException e) {
        return ResponseVo.builder().code(e.getCode()).msg(e.getError()).build();
    }


    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseVo  handleException2(BindException ex) {
        FieldError err = ex.getFieldError();
        String message = "param {".concat(err.getField()).concat("}").concat(err.getDefaultMessage());
        return ResponseVo.builder().msg(message).code(PARAMETER_ERROR.getCode()).build();
    }


    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseVo handleMissingServletRequestParameterException(MissingServletRequestParameterException ex){
        return ResponseVo.builder().code(PARAMETER_ERROR.getCode()).msg("Missing Request Parameter " + ex.getParameterName()).build();
    }
}
