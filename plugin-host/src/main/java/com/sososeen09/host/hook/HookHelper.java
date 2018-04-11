package com.sososeen09.host.hook;

import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.sososeen09.host.delegate.ActivityCallback;
import com.sososeen09.host.delegate.IActivityManagerProxy;
import com.sososeen09.host.delegate.IPackageManagerProxy;
import com.sososeen09.host.mergeway.PluginInstrumentation;
import com.sososeen09.host.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by yunlong.su on 2018/4/10.
 */

public class HookHelper {
    private static Context applicationContext;
    private static IActivityManager mIActivityManager;

    public static void initHook(Context context) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        applicationContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hookActivityManagerApi26();

        } else {
            hookActivityManagerApi25();
        }

        HookPackageManager();
        hookActivityThreadHandler();
    }

    private static void HookPackageManager() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        //需要hook ActivityThread
        //获取ActivityThread的成员变量 sCurrentActivityThread
        Class<?> activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
        sPackageManagerField.setAccessible(true);
        Object iPackageManagerObj = sPackageManagerField.get(null);


        Class<?> iPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
        IPackageManagerProxy interceptInvocationHandler = new IPackageManagerProxy(iPackageManagerObj);
        Object iPackageManagerObjProxy = Proxy.newProxyInstance(applicationContext.getClassLoader(), new Class[]{iPackageManagerClass}, interceptInvocationHandler);

        sPackageManagerField.set(null, iPackageManagerObjProxy);
    }


    private static void hookActivityManagerApi26() {
        try {
            // 反射获取ActivityManager的静态成员变量IActivityManagerSingleton,适配8.0
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingletonField = activityManagerNativeClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            Object iActivityManagerSingleton = iActivityManagerSingletonField.get(null);
            realHookActivityManager(iActivityManagerSingleton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookActivityManagerApi25() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 反射获取ActivityManagerNative的静态成员变量gDefault, 注意，在8.0的时候这个已经更改了
        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
        Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);
        Object gDefaultObj = gDefaultField.get(null);
        realHookActivityManager(gDefaultObj);
    }

    private static void realHookActivityManager(Object gDefaultObj) throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        // ActivityManagerNative.getDefault()方法在ActivityThread调用attach方法初始化的时候已经调用过，
        // 所以我们在这里拿到的instanceObj对象不为空，如果为空的话就没办法使用
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        Object instanceObj = mInstanceField.get(gDefaultObj);

        // 需要动态代理IActivityManager，把Singleton的成员变量mInstance的值设置为我们的这个动态代理对象
        // 但是有一点，我们不可能完全重写一个IActivityManager的实现类
        // 所以还是需要用到原始的IActivityManager对象，只是在调用某些方法的时候做一些手脚
        Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");
        IActivityManagerProxy IActivityManagerProxy = new IActivityManagerProxy(applicationContext, instanceObj);
        Object iActivityManagerObj = Proxy.newProxyInstance(applicationContext.getClassLoader(), new Class[]{iActivityManagerClass}, IActivityManagerProxy);
        mInstanceField.set(gDefaultObj, iActivityManagerObj);

        mIActivityManager = (IActivityManager) iActivityManagerObj;
    }

    private static void hookActivityThreadHandler() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        //需要hook ActivityThread
        //获取ActivityThread的成员变量 sCurrentActivityThread
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field sCurrentActivityThread = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThread.setAccessible(true);
        Object activityThreadObj = sCurrentActivityThread.get(null);

        //获取ActivityThread的成员变量 mH
        Field mHField = activityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mHObj = (Handler) mHField.get(activityThreadObj);

        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        mCallbackField.set(mHObj, new ActivityCallback(applicationContext, mHObj));
    }

    public static void hookInstrumentation() {
        Instrumentation baseInstrumentation = ReflectUtil.getInstrumentation(applicationContext);
        if (baseInstrumentation.getClass().getName().contains("lbe")) {
            // reject executing in paralell space, for example, lbe.
            System.exit(0);
        }
        final PluginInstrumentation instrumentation = new PluginInstrumentation(baseInstrumentation);
        Object activityThread = ReflectUtil.getActivityThread(applicationContext);
        ReflectUtil.setInstrumentation(activityThread, instrumentation);

        ReflectUtil.setHandlerCallback(applicationContext, instrumentation);
    }

}
