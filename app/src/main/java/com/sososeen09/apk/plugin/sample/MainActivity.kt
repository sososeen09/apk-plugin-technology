package com.sososeen09.apk.plugin.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sososeen09.apk.plugin.sample.utils.FileOperation
import com.sososeen09.plugin.lib.showToast
import java.io.File

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun load(view: View) {
        PluginManager.init(this)
        loadPlugin()
    }

    private fun loadPlugin() {
//        val source = "file:///android_asset/plugina.apk"
        val destFile = File(cacheDir.absolutePath + "/plugin", "plugin-a-debug.apk")
        FileOperation.copyFileUsingStream(assets.open("plugin-a-debug.apk"), destFile)

        PluginManager.getInstance().loadPlugin(destFile.absolutePath)
        showToast("加载完毕")
    }

    fun click(view: View) {
        val intent = Intent(this, ProxyActivity::class.java)
        intent.putExtra("className", PluginManager.getInstance().getPackageInfo()?.activities?.get(0)?.name ?: "")
        startActivity(intent)
    }


}
