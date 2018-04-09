package com.sososeen09.plugin.lib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by yunlong on 2018/4/1.
 */
interface PluginBroadcastInterface {
    fun attach(context: Context)

    fun onReceive(context: Context, intent: Intent)
}