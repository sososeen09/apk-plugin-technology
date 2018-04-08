package com.sososeen09.host.delegate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sososeen09.host.ProxyActivity;
import com.sososeen09.host.internal.Constants;
import com.sososeen09.host.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by yunlong.su on 2018/4/8.
 */
public class ActivityStartMethodHandler implements InvocationHandler {
    Context context;
    Object originalObject;

    public ActivityStartMethodHandler(Context context, Object originalObject) {
        this.context = context;
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtils.d("method:" + method.getName() + " called with args:" + Arrays.toString(args));
        //如果是startActivity方法，需要做一些手脚
        if (Constants.METHOD_START_ACTIVITY.equals(method.getName())) {
            Intent newIntent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    Intent wantedIntent = (Intent) arg;

                    // 需要知道想要启动的Activity是宿主的，还是插件的,如果是宿主的，不用管
                    if (((Intent) arg).getComponent().getPackageName().equals(context.getPackageName())) {
                        break;
                    }
                    // 加入目标Activity没有在清单文件中注册，我们就欺骗ActivityManagerService，启动一个代理页面
                    // 真正启动页面，会开始回调ActivityThread的ha.ndleLaunchActivity方法
                    // 调用这个方法前可以做点文章，启动我们想要启动的页面
                    newIntent = new Intent();
                    ComponentName componentName = new ComponentName(context, ProxyActivity.class);
                    newIntent.setComponent(componentName);

                    //把原始的跳转信息当作参数携带给代理类
                    newIntent.putExtra(Constants.EXTRA_REAL_WANTED_INTENT, wantedIntent);
                    index = i;
                }
            }

            args[index] = newIntent;
        }
        return method.invoke(originalObject, args);
    }
}
