package com.sososeen09.host;

import android.app.ActivityThread;
import android.app.Application;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.sososeen09.host.hook.PluginApk;
import com.sososeen09.host.hook.PluginManager;
import com.sososeen09.host.utils.LogUtils;
import com.sososeen09.host.utils.ReflectUtil;

import java.lang.reflect.Method;

import static com.sososeen09.host.internal.Constants.EXTRA_COMMAND;
import static com.sososeen09.host.internal.Constants.EXTRA_COMMAND_START_SERVICE;
import static com.sososeen09.host.internal.Constants.EXTRA_COMMAND_STOP_SERVICE;
import static com.sososeen09.host.internal.Constants.EXTRA_TARGET;

public class ProxyService extends Service {
    private PluginManager mPluginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("ProxyService onCreate");
        mPluginManager = PluginManager.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        LogUtils.i("ProxyService onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent || !intent.hasExtra(EXTRA_TARGET) || !intent.hasExtra(EXTRA_COMMAND)) {
            return START_STICKY;
        }


        Intent target = intent.getParcelableExtra(EXTRA_TARGET);
        int command = intent.getIntExtra(EXTRA_COMMAND, 0);
        if (null == target || command <= 0) {
            return START_STICKY;
        }

        ComponentName component = target.getComponent();
        PluginApk pluginApk = mPluginManager.getLoadedPlugin(component);

        switch (command) {
            case EXTRA_COMMAND_START_SERVICE: {
                Service service;

                if (this.mPluginManager.getComponentsHandler().isServiceAvailable(component)) {
                    service = this.mPluginManager.getComponentsHandler().getService(component);
                } else {
                    //需要按照Service的正常创建流程，调用它的attach方法，给它一个上下文环境
                    try {
                        service = (Service) pluginApk.getClassLoader().loadClass(component.getClassName()).newInstance();

                        ActivityThread mainThread = (ActivityThread) ReflectUtil.getActivityThread(getBaseContext());
                        IApplicationThread appThread = mainThread.getApplicationThread();

                        Method attach = service.getClass().getMethod("attach", Context.class, ActivityThread.class, String.class, IBinder.class, Application.class, Object.class);
                        IBinder token = appThread.asBinder();
                        Application app = pluginApk.makeApplication();
                        IActivityManager am = mPluginManager.getActivityManager();
                        attach.invoke(service, pluginApk.getHostContext(), mainThread, component.getClassName(), token, app, am);
                        service.onCreate();
                        this.mPluginManager.getComponentsHandler().rememberService(component, service);
                    } catch (Throwable t) {
                        return START_STICKY;
                    }
                }

                service.onStartCommand(target, 0, this.mPluginManager.getComponentsHandler().getServiceCounter(service).getAndIncrement());
                break;
            }

            case EXTRA_COMMAND_STOP_SERVICE: {
                Service service = this.mPluginManager.getComponentsHandler().forgetService(component);
                if (null != service) {
                    try {
                        service.onDestroy();
                    } catch (Exception e) {
                        LogUtils.e("Unable to stop service " + service + ": " + e.toString());
                    }
                } else {
                    LogUtils.e(component + " not found");
                }
                break;
            }
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

}
