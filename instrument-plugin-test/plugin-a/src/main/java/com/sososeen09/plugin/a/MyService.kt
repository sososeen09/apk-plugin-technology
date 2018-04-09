package com.sososeen09.plugin.a

import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class MyService : BaseService() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext,"plugin a service start!!!",Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext,"plugin a service destroy!!!",Toast.LENGTH_SHORT).show()
    }

}

