package com.sososeen09.plugin.lib

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder

/**
 * Created by yunlong.su on 2018/3/29.
 */
interface PluginServiceInterface {
    fun attach(proxyService: Service)

    fun onCreate()

    fun onStart(intent: Intent, startId: Int)

    fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int

    fun onDestroy()

    fun onConfigurationChanged(newConfig: Configuration)

    fun onLowMemory()

    fun onTrimMemory(level: Int)

    fun onBind(intent: Intent): IBinder?

    fun onUnbind(intent: Intent): Boolean

    fun onRebind(intent: Intent)

    fun onTaskRemoved(rootIntent: Intent)
}