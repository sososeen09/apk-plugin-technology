package com.sososeen09.plugin.a

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.sososeen09.plugin.lib.PluginActivityInterface

/**
 * Created by yunlong.su on 2018/3/29.
 */
open class BaseActivity : Activity(), PluginActivityInterface {
    protected var that: Activity? = null
    override fun attach(activity: Activity) {
        that = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (that == null) {
            super.onCreate(savedInstanceState)
        }
    }

    override fun onStart() {
        if (that == null) {
            super.onStart()
        }
    }

    override fun onRestart() {
        if (that == null) {
            super.onStart()
        }
    }

    override fun onResume() {
        if (that == null) {
            super.onStart()
        }
    }

    override fun onPause() {
        if (that == null) {
            super.onStart()
        }
    }

    override fun onStop() {
        if (that == null) {
            super.onStart()
        }
    }

    override fun onDestroy() {
        if (that == null) {
            super.onDestroy()
        }
    }


    /**
     * layout的加载需要用到LayoutInflater，而这个LayoutInflater的创建是在{@link #PhoneWindow}的构造方法中
     */
    override fun getWindow(): Window? {
        return if (that != null) {
            that?.window
        } else {
            super.getWindow()
        }
    }


    /**
     * 如果需要获取LayoutInflater，也需要重写
     */
    override fun getLayoutInflater(): LayoutInflater? {
        return if (that != null) {
            that?.layoutInflater
        } else {
            super.getLayoutInflater()
        }
    }

    override fun setContentView(layoutResID: Int) {
        if (that != null) {
            that?.setContentView(layoutResID)
        } else {
            super.setContentView(layoutResID)
        }
    }

    override fun getClassLoader(): ClassLoader? {
        return if (that != null) {
            that?.classLoader
        } else {
            super.getClassLoader()
        }
    }

    override fun startActivity(intent: Intent?) {
        if (that == null) {
            super.startActivity(intent)
        } else {
            that!!.startActivity(intent)
        }
    }

    override fun startService(service: Intent?): ComponentName {
        return if (that == null) {
            super.startService(service)
        } else {
            that!!.startService(service)
        }
    }

    override fun <T : View?> findViewById(id: Int): T {
        return if (that == null) {
            super.findViewById<T>(id)
        } else {
            that!!.findViewById<T>(id)
        }
    }
}