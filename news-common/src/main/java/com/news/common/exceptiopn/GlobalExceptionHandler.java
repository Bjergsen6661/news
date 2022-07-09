package com.news.common.exceptiopn;

import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 全局异常处理器
 *  -@ControllerAdvice：指定拦截annotations指定注解的controller
 *  -@ResponseBody：封装json数据返回
 * @create 2022-06-16-10:37
 */
//@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //处理自定义异常类在抛出的异常
    @ExceptionHandler({DiyException.class})
    public R DiyHandler(DiyException e){
        log.error(e.getMessage());

        return R.exception(e.getResponseStatus());
    }

    //处理图片上传大小异常类在抛出的异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R MaxUploadSizeExceededHandler(MaxUploadSizeExceededException e) {
        log.error(e.getMessage());

        return R.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public R returnException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        Map<String, String> map = getErrors(result);

        return R.errorMap(map);
    }

    /**
     * 获取Bo中字段的错误信息
     * @param result 字段绑定的校验信息
     */
    private Map<String, String> getErrors(BindingResult result){
        Map<String, String> map = new HashMap<>();

        List<FieldError> fieldError = result.getFieldErrors();
        for(FieldError error : fieldError){
            String errorField = error.getField();
            String errorMessage = error.getDefaultMessage();
            map.put(errorField, errorMessage);
        }

        return map;
    }
}
