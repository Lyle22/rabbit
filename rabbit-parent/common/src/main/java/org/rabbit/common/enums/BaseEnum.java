package org.rabbit.common.enums;

import org.rabbit.common.base.BaseResult;

public enum BaseEnum implements BaseResult {
    // 数据操作错误定义
    SUCCESS("200", "成功!"), 
    BAD_METHOD("405", "Http请求方法不正确"),
    BODY_NOT_MATCH("400","请求的参数格式不符!"),
    SIGNATURE_NOT_MATCH("401","请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
    TYPE_MISMATCH("407","请求参数类型不匹配!"),
    MISS_REQUEST_PARAMETER("408", "请求参数不全，请检查!"),
    NULL_EXCEPTION("409", "业务对象为空，出现系统错误"),
    ERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503","服务器正忙，请稍后再试!");

    /** 错误码 */
    private String resultCode;

    /** 错误描述 */
    private String resultMsg;

    BaseEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }

}
