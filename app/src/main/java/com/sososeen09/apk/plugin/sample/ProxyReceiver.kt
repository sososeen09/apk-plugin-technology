package com.sososeen09.apk.plugin.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sososeen09.plugin.lib.PluginBroadcastInterface

/**
 * Created by yunlong on 2018/4/1.
 */
class ProxyReceiver(name: String, context: Context) : BroadcastReceiver() {
    var pluginBroadcastInterface: PluginBroadcastInterface = PluginManager.getInstance().getDexClassLoader()?.loadClass(name)?.newInstance() as PluginBroadcastInterface

    init {
        pluginBroadcastInterface.attach(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        pluginBroadcastInterface.onReceive(context, intent)
    }
}