package com.dabangvr.util;

import android.app.Activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String hidePhoneNum(String phone) {
        String result = "";
        if (phone != null && !"".equals(phone)) {
            if (isMobileNum(phone)) {
                result = phone.substring(0, 3) + "****" + phone.substring(7);

            }
        }
        return result;
    }

    public static boolean isMobileNum(String mobiles) {

        Pattern p = Pattern

                .compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }
}
