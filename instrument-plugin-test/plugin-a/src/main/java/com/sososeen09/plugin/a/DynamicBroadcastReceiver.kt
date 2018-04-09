package com.sososeen09.plugin.a

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.sososeen09.plugin.lib.PluginBroadcastInterface

/**
 * Created by yunlong on 2018/4/1.
 */
class DynamicBroadcastReceiver : BroadcastReceiver(), PluginBroadcastInterface {
    override fun attach(context: Context) {
        Toast.makeText(context, "attach context success " + context, Toast.LENGTH_LONG).show()
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("apk plugin", "receive dynamic broadcast receiver")
        Toast.makeText(context, "dynamic broadcast receiver  receive msg ", Toast.LENGTH_LONG).show()
    }
}