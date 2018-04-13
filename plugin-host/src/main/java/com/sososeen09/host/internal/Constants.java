package com.sososeen09.host.internal;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public interface Constants {
    int LAUNCH_ACTIVITY = 100;
    int CREATE_SERVICE = 114;

    String METHOD_GET_ACTIVITY_INFO = "getActivityInfo";
    String EXTRA_TARGET = "real_wanted_intent";
    String METHOD_START_ACTIVITY = "startActivity";
    String METHOD_GET_PACKAGE_INFO = "getPackageInfo";
    String METHOD_START_SERVICE = "startService";
    String METHOD_REGISTER_RECEIVER="registerReceiver";



    String METHOD_STOP_SERVICE="stopService";
    String EXTRA_COMMAND = "command";
    int EXTRA_COMMAND_START_SERVICE = 1;
    int EXTRA_COMMAND_STOP_SERVICE = 2;
    int EXTRA_COMMAND_BIND_SERVICE = 3;
    int EXTRA_COMMAND_UNBIND_SERVICE = 4;
}
