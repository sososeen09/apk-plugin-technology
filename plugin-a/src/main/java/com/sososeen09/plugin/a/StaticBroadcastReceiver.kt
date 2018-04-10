package com.sososeen09.plugin.a

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by yunlong.su on 2018/4/2.
 */

class StaticBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_DYNAMIC: String = "com.sososeen09.plugin.apk.dynamic"
        const val ACTION_STATIC: String = "com.sososeen09.plugin.apk.static.receiver"
        const val ACTION_HOST: String = "com.sososeen09.plugin.apk.host.action"

    }
    override fun onReceive(context: Context, intent: Intent) {
        val msg = intent.getStringExtra("msg")
        Toast.makeText(context, "static broadcast receiver  receive msg :" + msg, Toast.LENGTH_LONG).show()


        val intentBroadcast = Intent(ACTION_HOST)
        intentBroadcast.putExtra("msg","broad cast from plugin StaticBroadcastReceiver")
        context.sendBroadcast(intentBroadcast)
    }

}