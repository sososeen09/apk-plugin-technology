package com.sososeen09.apk.plugin.sample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sososeen09.plugin.lib.PackageUtils
import com.sososeen09.plugin.lib.showToast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : Activity() {
    companion object {
        const val ACTION_DYNAMIC: String = "com.sososeen09.plugin.apk.dynamic"
        const val ACTION_STATIC: String = "com.sososeen09.plugin.apk.static.receiver"
        const val ACTION_HOST: String = "com.sososeen09.plugin.apk.host.action"

    }

    private lateinit var pluginAdapter: PluginAdapter

    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, " host receiver get msg: " + intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)

        pluginAdapter = PluginAdapter(getData(), this)
        rv_plugin_apk.layoutManager = LinearLayoutManager(this)
        rv_plugin_apk.adapter = pluginAdapter

        PluginManager.init(this)


        registerReceiver(mReceiver, IntentFilter(ACTION_HOST))

    }

    private fun getData(): List<PluginItem> {
        val pluginFolder = Environment.getExternalStorageDirectory().toString() + "/apk_plugin_dir"
        val file = File(pluginFolder)
        val plugins = file.listFiles()
        if (plugins == null || plugins.isEmpty()) {
            return ArrayList()
        }

        val mPluginItems = ArrayList<PluginItem>()
        for (plugin in plugins) {
            val item = PluginItem()
            item.pluginPath = plugin.absolutePath
            item.packageInfo = PackageUtils.getPackageInfo(this, item.pluginPath)
            if (item.packageInfo?.activities != null && item.packageInfo!!.activities.isNotEmpty()) {
                item.launcherActivityName = item.packageInfo!!.activities[0].name
            }
            if (item.packageInfo!!.services != null && item.packageInfo!!.services.isNotEmpty()) {
                item.launcherServiceName = item.packageInfo!!.services[0].name
            }
            mPluginItems.add(item)
        }

        return mPluginItems
    }

    fun loadPlugin(view: View) {
        loadPlugin()
    }

    private fun loadPlugin() {
        val pluginItem = pluginAdapter.data.firstOrNull()
        pluginItem?.let {
            PluginManager.getInstance().loadPlugin(it.pluginPath!!)
            showToast("加载完毕")
        }

    }

    fun click(view: View) {
        val intent = Intent(this, ProxyActivity::class.java)
        intent.putExtra("className", PluginManager.getInstance().getPackageInfo()?.activities?.get(0)?.name ?: "")
        startActivity(intent)
    }

    fun sendBroadcast(view: View) {
        val staticIntent = Intent(ACTION_STATIC)
        staticIntent.putExtra("msg", "from host mainActivity")
        sendBroadcast(staticIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }


    private class PluginAdapter(val data: List<PluginItem>, val context: Context) : RecyclerView.Adapter<Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plugin_apk, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.appIcon.setImageDrawable(PackageUtils.getAppIcon(context, data[position].pluginPath!!))
            holder.appName.text = PackageUtils.getAppLabel(context, data[position].pluginPath!!)
            holder.apkName.text = data[position].pluginPath!!.substring((data[position].pluginPath!!.lastIndexOf(File.separatorChar)) + 1)
            holder.packageName.text = "${data[position].packageInfo!!.applicationInfo.packageName} \n ${data[position].launcherActivityName}\n ${data[position].launcherServiceName}"
        }

    }

    class PluginItem(var packageInfo: PackageInfo? = null,
                     var pluginPath: String? = null,
                     var launcherActivityName: String? = null,
                     var launcherServiceName: String? = null)


    private class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        var appName: TextView = itemView.findViewById(R.id.app_name)
        var apkName: TextView = itemView.findViewById(R.id.apk_name)
        var packageName: TextView = itemView.findViewById(R.id.package_name)

    }
}
