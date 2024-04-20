package org.rabbit.utils;

import com.sun.istack.NotNull;
import org.apache.commons.lang3.StringUtils;

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
    public static CharSequence removeConsecutive(@NotNull CharSequence characters, @NotNull Character remove) {
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

    public static void main(String[] args) {
        String ss = "Read";
        String s1 = "Read Folder";
        String s2 = "Read-SL";
        String s3 = "Read_GH";
        String s4 = "Read123";
        System.out.println(ss + " > " + isAlpha(ss));
        System.out.println(s1 + " > " + isAlpha(s1));
        System.out.println(s2 + " > " + isAlpha(s2));
        System.out.println(s3 + " > " + isAlpha(s3));
        System.out.println(s4 + " > " + isAlpha(s4));
    }
}
