package com.sososeen09.plugin.lib

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build

/**
 * Created by yunlong.su on 2018/3/30.
 */
class PackageUtils {
    private val TAG = "PackageUtils"

    companion object {
        fun getPackageInfo(context: Context, apkFilepath: String?): PackageInfo? {
            val pm = context.packageManager
            var pkgInfo: PackageInfo? = null
            try {
                pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
            } catch (e: Exception) {
                // should be something wrong with parse
                e.printStackTrace()
            }

            return pkgInfo
        }

        fun getAppIcon(context: Context, apkFilepath: String): Drawable? {
            val pm = context.packageManager
            val pkgInfo = getPackageInfo(context, apkFilepath) ?: return null

            // Workaround for http://code.google.com/p/android/issues/detail?id=9151
            val appInfo = pkgInfo.applicationInfo
            if (Build.VERSION.SDK_INT >= 8) {
                appInfo.sourceDir = apkFilepath
                appInfo.publicSourceDir = apkFilepath
            }

            return pm.getApplicationIcon(appInfo)
        }

        fun getAppLabel(context: Context, apkFilepath: String): CharSequence? {
            val pm = context.packageManager
            val pkgInfo = getPackageInfo(context, apkFilepath) ?: return null

            // Workaround for http://code.google.com/p/android/issues/detail?id=9151
            val appInfo = pkgInfo.applicationInfo
            if (Build.VERSION.SDK_INT >= 8) {
                appInfo.sourceDir = apkFilepath
                appInfo.publicSourceDir = apkFilepath
            }

            return pm.getApplicationLabel(appInfo)
        }
    }


}