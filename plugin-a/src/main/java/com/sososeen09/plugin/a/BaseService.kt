package com.sososeen09.plugin.a

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.sososeen09.plugin.lib.PluginServiceInterface

/**
 * Created by yunlong.su on 2018/3/29.
 */
open class BaseService : Service(), PluginServiceInterface {
    var that: Service? = null

    override fun attach(proxyService: Service) {
        that = proxyService
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        if (that == null) {
            super.onCreate()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (that == null) {
            super.onDestroy()
        }
    }

    override fun onLowMemory() {
        if (that == null) {
            super.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        if (that == null) {
            super.onTrimMemory(level)
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    override fun getApplicationContext(): Context {
        return if (that == null) {
            super.getApplicationContext()
        } else {
            return that!!.applicationContext
        }
    }
}