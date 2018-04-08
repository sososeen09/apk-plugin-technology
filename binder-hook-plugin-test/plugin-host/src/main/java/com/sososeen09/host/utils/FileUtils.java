package com.sososeen09.host.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class FileUtils {
    private static File sBaseDir;

    public static File getPluginOptDexDir(Context context, String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(context, packageName), "odex"));
    }

    public static File getPluginLibDir(Context applicationContext, String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(applicationContext, packageName), "lib"));
    }

    // 需要加载得插件得基本目录 /data/data/<package>/files/plugin/
    private static File getPluginBaseDir(Context context, String packageName) {
        if (sBaseDir == null) {
            sBaseDir = context.getFileStreamPath("plugin");
            enforceDirExists(sBaseDir);
        }
        return enforceDirExists(new File(sBaseDir, packageName));
    }


    private static synchronized File enforceDirExists(File sBaseDir) {
        if (!sBaseDir.exists()) {
            boolean ret = sBaseDir.mkdir();
            if (!ret) {
                throw new RuntimeException("create dir " + sBaseDir + "failed");
            }
        }
        return sBaseDir;
    }
}
