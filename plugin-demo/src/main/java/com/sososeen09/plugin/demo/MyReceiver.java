package com.sososeen09.plugin.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yunlong.su on 2018/4/12.
 */

public class MyReceiver extends BroadcastReceiver {
    private int callTimes;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO something

        Toast.makeText(context, "MyReceiver 收到广播：" + callTimes++, Toast.LENGTH_SHORT).show();
        Log.e("MyReceiver", "MyReceiver 收到广播：" + callTimes++);

    }
}
