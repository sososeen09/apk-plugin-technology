package com.sososeen09.plugin.a

import android.content.Intent
import android.os.Bundle
import android.widget.Button

/**
 * Created by yunlong.su on 2018/3/29.
 */
class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        findViewById<Button>(R.id.btn_service).setOnClickListener {
            startService(Intent(if (that != null) that else this@SecondActivity, MyService::class.java))
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            stopService(Intent(if (that != null) that else this@SecondActivity, MyService::class.java))
        }
    }
}
