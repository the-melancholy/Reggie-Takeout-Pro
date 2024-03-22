package com.zjc.reggie.common;


import com.zjc.reggie.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;


/**可以使用@RestControllerAdvice注解来代替以下两个注解
 *  @ControllerAdvice  @ResponseBody
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // 错误示例: Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] strs = message.split(" ");
            String response = strs[2]+"已存在";
            return Result.error(response);
        }
        return Result.error("未知错误,请稍后重试！");

    }

    @ExceptionHandler({CommonException.class})
    public Result<String> exceptionHandler(CommonException ex){
        log.error(ex.getMessage());
        return Result.error(ex.getMessage());
    }



}
