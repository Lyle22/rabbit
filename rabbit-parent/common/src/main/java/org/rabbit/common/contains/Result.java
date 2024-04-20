package org.rabbit.common.contains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;

/**
 * the type of global result
 *
 * @author ninerabbit
 */
@Data
public class Result<T> implements Serializable {

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        result = (this.code == ResponseEnum.SUCCESS.getCode());
    }

    public Result(int code) {
        this.code = code;
        result = (this.code == ResponseEnum.SUCCESS.getCode());
    }

    public Result(ResultCode resultCode, String Message) {
        this.code = resultCode.getCode();
        this.message = Message;
        result = (this.code == ResponseEnum.SUCCESS.getCode());
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<T>(ResponseEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        if (NumberUtils.isParsable(msg)) {
            return errorCode(Integer.valueOf(msg));
        }
        return new Result(ResponseEnum.FAIL, msg);
    }

    public static <T> Result<T> errorCode(Integer code) {
        return new Result(code);
    }

}
