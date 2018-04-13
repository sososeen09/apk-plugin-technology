package com.sososeen09.host.delegate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.sososeen09.host.ProxyActivity;
import com.sososeen09.host.ProxyService;
import com.sososeen09.host.internal.Constants;
import com.sososeen09.host.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by yunlong.su on 2018/4/8.
 */
public class IActivityManagerProxy implements InvocationHandler {
    Context context;
    Object originalObject;

    public IActivityManagerProxy(Context context, Object originalObject) {
        this.context = context;
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtils.d("method:" + method.getName() + " called with args:" + Arrays.toString(args));
        //如果是startActivity方法，需要做一些手脚
        if (Constants.METHOD_START_ACTIVITY.equals(method.getName())) {
            return handleStartActivity(method, args);
        } else if (Constants.METHOD_START_SERVICE.equals(method.getName())) {
            return handleStartServiceMethod(proxy, method, args);
        } else if (Constants.METHOD_STOP_SERVICE.equals(method.getName())) {
            return handleStopServiceMethod(proxy, method, args);
        } else if (Constants.METHOD_REGISTER_RECEIVER.equals(method.getName())) {
            return handleRegisterReceiverMethod(proxy, method, args);
        }

        return method.invoke(originalObject, args);
    }

    private Object handleStartActivity(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
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
                newIntent.putExtra(Constants.EXTRA_TARGET, wantedIntent);
                index = i;
            }
        }

        args[index] = newIntent;
        return method.invoke(originalObject, args);
    }

    private Object handleRegisterReceiverMethod(Object proxy, Method method, Object[] args) {
        //TODO HOOK registerReceiver  方法
        return null;
    }

    private Object handleStopServiceMethod(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Pair<Integer, Intent> targetPair = findTargetIntentFromArgs(args);
        Intent newIntent = new Intent();
        ComponentName componentName = new ComponentName(context, ProxyService.class);
        newIntent.setComponent(componentName);

        //把原始的跳转信息当作参数携带给代理类
        newIntent.putExtra(Constants.EXTRA_TARGET, targetPair.second);
        newIntent.putExtra(Constants.EXTRA_COMMAND, Constants.EXTRA_COMMAND_STOP_SERVICE);

        args[targetPair.first] = newIntent;
        context.startService(newIntent);
        return 1;
    }


    private Object handleStartServiceMethod(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Intent newIntent = null;
        int index = 0;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Intent) {
                Intent wantedIntent = (Intent) arg;

                // 需要知道想要启动的Service是宿主的，还是插件的,如果是宿主的，不用管
                if (((Intent) arg).getComponent().getPackageName().equals(context.getPackageName())) {
                    break;
                }

                newIntent = new Intent();
                ComponentName componentName = new ComponentName(context, ProxyService.class);
                newIntent.setComponent(componentName);

                //把原始的跳转信息当作参数携带给代理类
                newIntent.putExtra(Constants.EXTRA_TARGET, wantedIntent);
                newIntent.putExtra(Constants.EXTRA_COMMAND, Constants.EXTRA_COMMAND_START_SERVICE);
                index = i;
            }
        }

        args[index] = newIntent;

        return method.invoke(originalObject, args);
    }

    private Pair<Integer, Intent> findTargetIntentFromArgs(Object[] args) {
        int index = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Intent) {
                index = i;
                break;
            }
        }
        return Pair.create(index, (Intent) args[index]);
    }

}
