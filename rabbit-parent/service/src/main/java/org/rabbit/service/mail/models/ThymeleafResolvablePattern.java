package org.rabbit.service.mail.models;

import lombok.experimental.UtilityClass;

/**
 * 匹配 Thymeleaf 解析器的标记
 * @author Lyle
 */
@UtilityClass
public class ThymeleafResolvablePattern {

    public static final String TEXT = "Text:*";

    public static final String HTML = "<!DOCTYPE html>*";

    public static final String EMAIL_BODY = "notification.*";

    public static final String EMAIL_SUBJECT = "notification_subject.*";

}
