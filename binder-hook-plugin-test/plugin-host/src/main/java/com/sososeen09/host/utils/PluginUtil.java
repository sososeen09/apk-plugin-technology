/*
 * Copyright (C) 2017 Beijing Didi Infinity Technology and Development Co.,Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sososeen09.host.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.sososeen09.host.hook.PluginApk;
import com.sososeen09.host.hook.PluginManager;

/**
 * Created by renyugang on 16/8/15.
 */
public class PluginUtil {


    public static int selectDefaultTheme(final int curTheme, final int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion,
                android.R.style.Theme,
                android.R.style.Theme_Holo,
                android.R.style.Theme_DeviceDefault,
                android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
    }

    private static int selectSystemTheme(final int curTheme, final int targetSdkVersion, final int orig, final int holo, final int dark, final int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }

        if (targetSdkVersion < 11 /* Build.VERSION_CODES.HONEYCOMB */) {
            return orig;
        }

        if (targetSdkVersion < 14 /* Build.VERSION_CODES.ICE_CREAM_SANDWICH */) {
            return holo;
        }

        if (targetSdkVersion < 24 /* Build.VERSION_CODES.N */) {
            return dark;
        }

        return deviceDefault;
    }

    public static void hookActivityResources(Activity activity, String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isVivo(activity.getResources())) {
            // for 5.0+ vivo
            return;
        }

        // designed for 5.0 - only, but some bad phones not work, eg:letv
        try {
            Context base = activity.getBaseContext();
            final PluginApk plugin = PluginManager.getInstance().getLoadedPlugin(packageName);
            final Resources resources = plugin.getResources();
            if (resources != null) {
                ReflectUtil.setField(base.getClass(), base, "mResources", resources);

                // copy theme
                Resources.Theme theme = resources.newTheme();
                theme.setTo(activity.getTheme());
                int themeResource = (int)ReflectUtil.getField(ContextThemeWrapper.class, activity, "mThemeResource");
                theme.applyStyle(themeResource, true);
                ReflectUtil.setField(ContextThemeWrapper.class, activity, "mTheme", theme);

                ReflectUtil.setField(ContextThemeWrapper.class, activity, "mResources", resources);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final boolean isLocalService(final ServiceInfo serviceInfo) {
        return TextUtils.isEmpty(serviceInfo.processName) || serviceInfo.applicationInfo.packageName.equals(serviceInfo.processName);
    }

    public static boolean isVivo(Resources resources) {
        return resources.getClass().getName().equals("android.content.res.VivoResources");
    }

    public static void putBinder(Bundle bundle, String key, IBinder value) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bundle.putBinder(key, value);
        } else {
            try {
                ReflectUtil.invoke(Bundle.class, bundle, "putIBinder", new Class[]{String.class, IBinder.class}, key, value);
            } catch (Exception e) {
            }
        }
    }

    public static IBinder getBinder(Bundle bundle, String key) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return bundle.getBinder(key);
        } else {
            try {
                return (IBinder) ReflectUtil.invoke(Bundle.class, bundle, "getIBinder", key);
            } catch (Exception e) {
            }

            return null;
        }
    }
}
