package com.sososeen09.plugin.a

import android.content.Intent
import android.os.Bundle
import android.widget.Button

/**
 * Created by yunlong.su on 2018/3/29.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_second).setOnClickListener {
            //插件中不能再使用this了

            val intent = Intent(if (that != null) that else this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}
