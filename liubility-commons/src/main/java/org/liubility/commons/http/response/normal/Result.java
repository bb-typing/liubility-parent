package org.liubility.commons.http.response.normal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Jdragon
 * @email: 1061917196@qq.com
 * @Date: 2020.03.18 17:45
 * @Description: 返回结果构造类
 */
@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 编号.
     */
    private Long code;

    /**
     * 信息.
     */
    private String message;

    /**
     * 结果数据
     */
    private T result;

    @JsonIgnore
    private transient static Object defaultData = new Object();


    public Result(Long code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public Result(ICode resultCode, T result) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.result = result;
    }

    /**
     * 返回成功状态码
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.NORMAL, null);
    }

    /**
     * 返回成功状态码与结果
     * @param result 自定义结果
     * @param <T> 结果类型
     */
    public static <T> Result<T> success(T result) {
        return new Result<>(ResultCode.NORMAL, result);
    }

    /**
     * 自定义返回消息和结果
     * @param message 自定义消息
     * @param result 自定义结果
     * @param <T> 结果类型
     */
    public static <T> Result<T> success(String message, T result) {
        return new Result<>(ResultCode.NORMAL.getCode(), message, result);
    }

    /**
     * 构建无结果自定义消息结果
     * @param message 自定义消息
     */
    public static Result<String> successMsg(String message) {
        return new Result<>(ResultCode.NORMAL.getCode(), message, null);
    }

    public static <T> Result<T> error() {
        return error(null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.SYSTEM_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> error(T result) {
        return new Result<>(ResultCode.SYSTEM_ERROR, result);
    }

    public static <T> Result<T> error(String message, T result) {
        return new Result<>(ResultCode.SYSTEM_ERROR.getCode(), message, result);
    }

    public static <T> Result<T> build(ICode resultCode, T result) {
        return new Result<>(resultCode, result);
    }

    public static <T> Result<T> build(Long code, String message, T result) {
        return new Result<>(code, message, result);
    }

    /**
     * 认证异常
     */
    public static <T> Result<T> authFail(T result) {
        return build(ResultCode.AUTH_FAIL, result);
    }

    /**
     * 权限不足异常
     */
    public static <T> Result<T> permissionsNotEnough(T result) {
        return build(ResultCode.PERMISSIONS_NOT_ENOUGH, result);
    }

    /**
     * 参数异常
     */
    public static <T> Result<T> paramsError(T result) {
        return build(ResultCode.PARAMS_ERROR, result);
    }

    /**
     * 未知异常
     */
    public static <T> Result<T> unKnowError(T result) {
        return build(ResultCode.SYSTEM_UN_KNOW_ERROR, result);
    }

    /**
     * 判断是否成功： 依据 Wrapper.SUCCESS_CODE == this.code
     *
     * @return code =200,true;否则 false.
     */
    @JsonIgnore
    public boolean result() {
        return ResultCode.NORMAL.getCode().equals(code);
    }
}
