package com.sososeen09.apk.plugin.sample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sososeen09.plugin.lib.PluginServiceInterface

class ProxyService : Service() {
    var serviceInterface: PluginServiceInterface? = null
    override fun onBind(intent: Intent): IBinder? {
        init(intent)
        return serviceInterface!!.onBind(intent)
    }

    private fun init(intent: Intent) {
        val className = intent.getStringExtra("serviceName")
        val clazz = classLoader?.loadClass(className)
        serviceInterface = clazz?.newInstance() as PluginServiceInterface
        serviceInterface?.attach(this)
    }


    /**
     * 由于onCreate在onBind和onstartCommand之前执行，所以这个生命周期没办法重写
     */
    override fun onCreate() {
        super.onCreate()
//        serviceInterface.onCreate()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (serviceInterface == null) {
            init(intent)
        }
        return serviceInterface!!.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent): Boolean {
        return serviceInterface!!.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceInterface!!.onDestroy()
    }

    override fun getClassLoader(): ClassLoader? {
        return PluginManager.getInstance().getDexClassLoader()
    }

}
