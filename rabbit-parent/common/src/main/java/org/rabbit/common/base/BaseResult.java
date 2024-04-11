package org.rabbit.common.base;

/**
 * The base class of return result
 *
 * @author nine
 * @since 2019-12-20
 */
public interface BaseResult {

    /**
     * 错误码
     */
    String getResultCode();

    /**
     * 错误描述
     */
    String getResultMsg();
}
