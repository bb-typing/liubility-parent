package org.liubility.commons.handler;

import org.liubility.commons.exception.AuthException;
import org.liubility.commons.exception.LBException;
import org.liubility.commons.exception.LBRuntimeException;
import org.liubility.commons.http.response.normal.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author JDragon
 * @Date 2021.02.16 下午 5:31
 * @Email 1061917196@qq.com
 * @Des:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseBody
    public Result<String> getMessage(MethodArgumentNotValidException exception) {
        // 获取NotNull注解中的message
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();
        return Result.paramsError(message);
    }

    @ExceptionHandler(value = {UnknownError.class})
    @ResponseBody
    public Result<String> getMessage(UnknownError exception) {
        return Result.unKnowError(exception.getMessage());
    }

    @ExceptionHandler(value = {LBRuntimeException.class})
    @ResponseBody
    public Result<Object> getMessage(LBRuntimeException exception) {
        return Result.build(exception.getCode().getCode(), exception.getMessage(), exception.getData());
    }

    @ExceptionHandler(value = {LBException.class})
    @ResponseBody
    public Result<?> getMessage(LBException exception) {
        return Result.build(exception.getCode().getCode(), exception.getMessage(), exception.getData());
    }


    @ExceptionHandler(value = {AuthException.class})
    @ResponseBody
    public Result<String> getMessage(AuthException exception) {
        return Result.authFail(exception.getMessage());
    }
}