package com.dabangvr.util;

import org.apache.commons.lang.StringUtils;

public class TextUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.equals("null") || str.equals("");
    }


    public static String isNull(String str){
        if (StringUtils.isEmpty(str)||str.equals("null")){
            return "0";
        }else {
            return str;
        }
    }

    public static String isNull2Url(String str){
        if (StringUtils.isEmpty(str)||str.equals("null")){
            return "";
        }else {
            return str;
        }
    }

    public static boolean isNullFor(String str){
        if (StringUtils.isEmpty(str)||str.equals("null")){
            return true;
        }else {
            return false;
        }
    }
}
