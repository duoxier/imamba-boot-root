package com.imamba.boot.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    public static final String V_INTEGER = "^-?[1-9]\\d*$";
    public static final String V_Z_INDEX = "^[1-9]\\d*$";
    public static final String V_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    public static final String V_NUMBER = "^([+-]?)\\d*\\.?\\d+$";
    public static final String V_POSITIVE_NUMBER = "^[1-9]\\d*|0$";
    public static final String V_NEGATINE_NUMBER = "^-[1-9]\\d*|0$";
    public static final String V_FLOAT = "^([+-]?)\\d*\\.\\d+$";
    public static final String V_POSTTIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
    public static final String V_NEGATIVE_FLOAT = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
    public static final String V_UNPOSITIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
    public static final String V_UN_NEGATIVE_FLOAT = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
    public static final String V_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    public static final String V_COLOR = "^[a-fA-F0-9]{6}$";
    public static final String V_URL = "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";
    public static final String V_CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    public static final String V_ASCII = "^[\\x00-\\xFF]+$";
    public static final String V_ZIPCODE = "^\\d{6}$";
    public static final String V_MOBILE = "1\\d{10}";
    public static final String V_IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
    public static final String V_NOTEMPTY = "^\\S+$";
    public static final String V_PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
    public static final String V_RAR = "(.*)\\.(rar|zip|7zip|tgz)$";
    public static final String V_DATE = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
    public static final String V_QQ_NUMBER = "^[1-9]*[1-9][0-9]*$";
    public static final String V_TEL = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";
    public static final String V_USERNAME = "^\\w+$";
    public static final String V_LETTER = "^[A-Za-z]+$";
    public static final String V_LETTER_U = "^[A-Z]+$";
    public static final String V_LETTER_I = "^[a-z]+$";
    public static final String V_IDCARD = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
    public static final String V_PASSWORD_REG = "[A-Za-z]+[0-9]";
    public static final String V_PASSWORD_LENGTH = "^\\d{6,18}$";
    public static final String V_TWO＿POINT = "^[0-9]+(.[0-9]{2})?$";
    public static final String V_31DAYS = "^((0?[1-9])|((1|2)[0-9])|30|31)$";

    public ValidateUtil() {
    }

    public static boolean isInteger(String value) {
        return match("^-?[1-9]\\d*$", value);
    }

    public static boolean isZIndex(String value) {
        return match("^[1-9]\\d*$", value);
    }

    public static boolean isNegativeInteger(String value) {
        return match("^-[1-9]\\d*$", value);
    }

    public static boolean isNumber(String value) {
        return match("^([+-]?)\\d*\\.?\\d+$", value);
    }

    public static boolean isPositiveNumber(String value) {
        return match("^[1-9]\\d*|0$", value);
    }

    public static boolean isNegatineNumber(String value) {
        return match("^-[1-9]\\d*|0$", value);
    }

    public static boolean is31Days(String value) {
        return match("^((0?[1-9])|((1|2)[0-9])|30|31)$", value);
    }

    public static boolean isASCII(String value) {
        return match("^[\\x00-\\xFF]+$", value);
    }

    public static boolean isChinese(String value) {
        return match("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$", value);
    }

    public static boolean isColor(String value) {
        return match("^[a-fA-F0-9]{6}$", value);
    }

    public static boolean isDate(String value) {
        return match("^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$", value);
    }

    public static boolean isEmail(String value) {
        return match("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", value);
    }

    public static boolean isFloat(String value) {
        return match("^([+-]?)\\d*\\.\\d+$", value);
    }

    public static boolean isIDcard(String value) {
        return match("^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$", value);
    }

    public static boolean isIP4(String value) {
        return match("^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$", value);
    }

    public static boolean isLetter(String value) {
        return match("^[A-Za-z]+$", value);
    }

    public static boolean isLetterI(String value) {
        return match("^[a-z]+$", value);
    }

    public static boolean isLetterU(String value) {
        return match("^[A-Z]+$", value);
    }

    public static boolean isMobile(String value) {
        return match("1\\d{10}", value);
    }

    public static boolean isNegativeFloat(String value) {
        return match("^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$", value);
    }

    public static boolean isNotEmpty(String value) {
        return match("^\\S+$", value);
    }

    public static boolean isNumberLength(String value) {
        return match("^\\d{6,18}$", value);
    }

    public static boolean isPasswordReg(String value) {
        return match("[A-Za-z]+[0-9]", value);
    }

    public static boolean isPicture(String value) {
        return match("(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", value);
    }

    public static boolean isPosttiveFloat(String value) {
        return match("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$", value);
    }

    public static boolean isQQNumber(String value) {
        return match("^[1-9]*[1-9][0-9]*$", value);
    }

    public static boolean isRar(String value) {
        return match("(.*)\\.(rar|zip|7zip|tgz)$", value);
    }

    public static boolean isTel(String value) {
        return match("^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$", value);
    }

    public static boolean isTwoPoint(String value) {
        return match("^[0-9]+(.[0-9]{2})?$", value);
    }

    public static boolean isUnNegativeFloat(String value) {
        return match("^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$", value);
    }

    public static boolean isUnpositiveFloat(String value) {
        return match("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$", value);
    }

    public static boolean isUrl(String value) {
        return match("^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$", value);
    }

    public static boolean isUserName(String value) {
        return match("^\\w+$", value);
    }

    public static boolean isZipCode(String value) {
        return match("^\\d{6}$", value);
    }

    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
