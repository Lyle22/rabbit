package org.rabbit.service.email.models;

import lombok.experimental.UtilityClass;

/**
 * 匹配 Thymeleaf 解析器的标记
 * @author nine rabbit
 */
@UtilityClass
public class ThymeleafResolvablePattern {

    public static final String TEXT = "Text:*";

    public static final String HTML = "<!DOCTYPE html>*";

    public static final String EMAIL_BODY = "notification.*";

    public static final String EMAIL_SUBJECT = "notification_subject.*";

}
