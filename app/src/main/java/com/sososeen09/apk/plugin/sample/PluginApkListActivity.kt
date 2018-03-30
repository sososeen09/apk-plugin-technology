package com.sososeen09.apk.plugin.sample

import android.app.ListActivity
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sososeen09.plugin.lib.PackageUtils
import java.io.File

/**
 * Created by yunlong.su on 2018/3/30.
 */
class PluginApkListActivity : ListActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = PluginAdapter(getData())
        listView.isTextFilterEnabled = true
    }

    private fun getData(): ArrayList<PluginItem> {
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

    private inner class PluginAdapter(var mPluginItems: ArrayList<PluginItem>) : BaseAdapter() {

        private val mInflater: LayoutInflater = this@PluginApkListActivity.layoutInflater


        override fun getCount(): Int {
            return mPluginItems.size
        }

        override fun getItem(position: Int): Any {
            return mPluginItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent, false)
                holder = ViewHolder()
                holder.appIcon = convertView!!.findViewById(R.id.app_icon) as ImageView
                holder.appName = convertView.findViewById(R.id.app_name) as TextView
                holder.apkName = convertView.findViewById(R.id.apk_name) as TextView
                holder.packageName = convertView.findViewById(R.id.package_name) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val item = mPluginItems.get(position)
            val packageInfo = item.packageInfo
            holder.appIcon?.setImageDrawable(PackageUtils.getAppIcon(this@PluginApkListActivity, item.pluginPath!!))
            holder.appName?.text = PackageUtils.getAppLabel(this@PluginApkListActivity, item.pluginPath!!)
            holder.apkName?.text = item.pluginPath!!.substring((item.pluginPath!!.lastIndexOf(File.separatorChar)) + 1)
            holder.packageName?.text = packageInfo!!.applicationInfo.packageName + "\n" +
                    item.launcherActivityName + "\n" +
                    item.launcherServiceName
            return convertView
        }
    }

    private class ViewHolder {
        var appIcon: ImageView? = null
        var appName: TextView? = null
        var apkName: TextView? = null
        var packageName: TextView? = null
    }


    class PluginItem(var packageInfo: PackageInfo? = null,
                     var pluginPath: String? = null,
                     var launcherActivityName: String? = null,
                     var launcherServiceName: String? = null)
}