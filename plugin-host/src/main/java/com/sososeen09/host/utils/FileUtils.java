package com.sososeen09.host.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static File copyFileFromAssets(Context context, String assetName, String dexOutputDir) throws IOException {
        File originDex = null;
        AssetManager assets = context.getAssets();
        InputStream open = assets.open(assetName);
        originDex = new File(dexOutputDir, assetName);
        FileOutputStream fileOutputStream = new FileOutputStream(originDex);
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = open.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        fileOutputStream.close();
        open.close();

        return originDex;
    }
}
