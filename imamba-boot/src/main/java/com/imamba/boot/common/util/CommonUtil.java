package com.imamba.boot.common.util;

import java.util.*;

public class CommonUtil {

    public CommonUtil() {
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
    }

    public static String getRandomString(int length) {
        if (length <= 0) {
            return "";
        } else {
            char[] randomChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'};
            Random random = new Random();
            StringBuffer stringBuffer = new StringBuffer();

            for(int i = 0; i < length; ++i) {
                stringBuffer.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
            }

            return stringBuffer.toString();
        }
    }

    public static String getRandomNumberString(int length) {
        if (length <= 0) {
            return "";
        } else {
            char[] randomChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            Random random = new Random();
            StringBuffer stringBuffer = new StringBuffer();

            for(int i = 0; i < length; ++i) {
                stringBuffer.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
            }

            return stringBuffer.toString();
        }
    }

    public static List<String> splitString(String str, int length) {
        List<String> list = new ArrayList();

        for(int i = 0; i < str.length(); i += length) {
            int endIndex = i + length;
            if (endIndex <= str.length()) {
                list.add(str.substring(i, i + length));
            } else {
                list.add(str.substring(i, str.length() - 1));
            }
        }

        return list;
    }

    public static String toString(List<String> list, String separator) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            String str = (String)var3.next();
            stringBuffer.append(separator + str);
        }

        stringBuffer.deleteCharAt(0);
        return stringBuffer.toString();
    }
}
