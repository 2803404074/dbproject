package com.dabangvr.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class CheckPage {

    public static boolean isAppInstalled(Context context,String name){
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(name,0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo == null){
            return false;
        }else {
            return true;
        }
    }
}
