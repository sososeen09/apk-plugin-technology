package com.sososeen09.plugin.lib

import android.app.Activity
import android.os.Bundle

/**
 * Created by yunlong.su on 2018/3/29.
 */
interface PluginActivityInterface {
    fun attach(activity: Activity)
    fun onCreate(savedInstanceState: Bundle?)
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
    fun onRestart()
}