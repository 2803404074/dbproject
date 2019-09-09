package com.dabangvr.util;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
    /* 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH:mm:ss");

//        String a = simpleDateFormat2.format(date);

        long lt = new Long(s);
        Date date = new Date(lt);
        Date date2 = new Date(System.currentTimeMillis());
        String one = simpleDateFormat2.format(date);//转换后的日期
        String tow = simpleDateFormat2.format(date2);//当前系统时间
        if(one.equals(tow)){
            //如果时间一样，返回时分秒即可
            return simpleDateFormat3.format(date);
        }else {//否则返回完整日期
            return simpleDateFormat.format(date);
        }
    }


    public static String getDateForNow(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 判断手机号是否符合规范
     * @param phoneNo 输入的手机号
     * @return
     */
    public static boolean isPhoneNumber(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            return false;
        }
        if (phoneNo.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit(phoneNo.charAt(i))) {
                    return false;
                }
            }
            Pattern p = Pattern.compile("^((13[^4,\\D])" + "|(134[^9,\\D])" +
                    "|(14[5,7])" +
                    "|(15[^4,\\D])" +
                    "|(17[3,6-8])" +
                    "|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(phoneNo);
            return m.matches();
        }
        return false;
    }

    /**
     * 移除指定字符
     * @param str  总字符串
     * @param delChar 需要移除的字符
     * @return
     */
    public static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }


}
