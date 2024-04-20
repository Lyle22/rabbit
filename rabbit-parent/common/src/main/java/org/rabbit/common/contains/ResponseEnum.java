package org.rabbit.common.contains;

/**
 * @author nine
 */

public enum ResponseEnum implements ResultCode {

    SUCCESS(200, "success"),

    FAIL(500, "fail");

    private final int code;
    private final String msg;

    private ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
