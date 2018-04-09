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
        const val ACTION_DYNAMIC: String = "com.sososeen09.plugin.apk.dynamic"
        const val ACTION_STATIC: String = "com.sososeen09.plugin.apk.static.receiver"

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
            val intentFilter = IntentFilter(ACTION_DYNAMIC)
            registerReceiver(DynamicBroadcastReceiver(), intentFilter)
        }

        findViewById<Button>(R.id.btn_send_broadcast).setOnClickListener {
            val dynamicIntent = Intent(ACTION_DYNAMIC)
            sendBroadcast(dynamicIntent)

            val staticIntent = Intent(ACTION_STATIC)
            staticIntent.putExtra("msg", "from mainActivity")
            sendBroadcast(staticIntent)
        }
    }
}
