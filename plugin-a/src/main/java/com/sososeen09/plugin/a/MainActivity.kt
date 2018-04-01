package com.sososeen09.plugin.a

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button

/**
 * Created by yunlong.su on 2018/3/29.
 */
class MainActivity : BaseActivity() {

    companion object {
        const val ACTION_BROAD_CAST: String = "com.sososeen09.plugin.apk.dynamic"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_second).setOnClickListener {
            //插件中不能再使用this了

            val intent = Intent(if (that != null) that else this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_resister).setOnClickListener {
            //注册广播
            val intentFilter = IntentFilter(ACTION_BROAD_CAST)
            registerReceiver(DynamicBroadcastReceiver(), intentFilter)
        }

        findViewById<Button>(R.id.btn_send_broadcast).setOnClickListener {
            val broadcastIntent = Intent(ACTION_BROAD_CAST)
            sendBroadcast(broadcastIntent)
        }
    }
}
