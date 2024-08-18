package org.rabbit.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private static final String PATTERN_UUID_STRING =
            "^[A-Fa-f0-9]{8}-([A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}$";
    private static final Pattern PATTERN_UUID = Pattern.compile(PATTERN_UUID_STRING);

    private static final String PATTERN_STRING = "^[a-zA-Z\\s\\w]*$";
    private static final Pattern PATTERN_ENGLISH = Pattern.compile(PATTERN_STRING);

    public static Boolean isUUID(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return false;
        }
        return PATTERN_UUID.matcher(uuid).matches();
    }

    public static Boolean isAlpha(String s) {
        if (StringUtils.isBlank(s)) {
            return false;
        }
        return PATTERN_ENGLISH.matcher(s).matches();
    }

    /**
     * Remove consecutive character sequence.
     *
     * @param characters the characters
     * @param remove     the remove
     * @return the char sequence
     */
    public static CharSequence removeConsecutive(CharSequence characters, Character remove) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(characters)) {
            int p = characters.charAt(0);
            sb.append((char) p);
            for (int i = 1; i < characters.length(); i++) {
                int c = characters.charAt(i);
                if (c != p || c != remove) {
                    sb.append((char) c);
                }
                p = c;
            }
        }
        return sb;
    }

    /**
     * Determine whether it contains special characters
     *
     * @param str the characters
     * @return return true if contains
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[`!@#$%^&*+=|{}':;',\\[\\].<>/?！@#￥%……&*+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String replaceSpecialChar(String str) {
        String regEx = "[`!@#$%^&*()+=|{}':;',//[//].<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("").trim().replaceAll(" ", "_");
    }

    public static boolean checkSpecialChar(String str) {
        String regEx = "[^\\w\\d\\s]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static File toFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String tmpFilePath = System.getProperty("java.io.tmpdir") + HttpPath.PATH_DELIMITER + System.currentTimeMillis() + fileName;
        File tmpFile = new File(tmpFilePath);
        FileUtils.writeByteArrayToFile(tmpFile, multipartFile.getBytes());
        return tmpFile;
    }

    /**
     * 以某一个字符来分割，讲字符串转换成驼峰命名方式
     *
     * @param name 分割字符
     * @return 驼峰命名方式的字符串
     */
    public static String toCamelCase(String name, String charStr) {
        StringBuilder result = new StringBuilder();
        String[] words = name.split(charStr);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                result.append(word);
            } else {
                result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
            }
        }
        return result.toString();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static final Pattern PATTERN_NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public static boolean isNumeric(String str) {
        Matcher isNum = PATTERN_NUMERIC.matcher(str);
        return isNum.matches();
    }

    public static int largestNumber(int[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        T bean = null;
        try {
            bean = beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

}
