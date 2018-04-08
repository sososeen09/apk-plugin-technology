package com.sososeen09.binder.hook.utils;

import android.util.Log;

/**
 * Created by yunlong.su on 2018/4/3.
 */

public class LogUtils {
    private static final String TAG = "binder hook";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

}
