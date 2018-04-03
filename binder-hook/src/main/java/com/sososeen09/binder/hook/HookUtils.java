package com.sososeen09.binder.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sososeen09.binder.hook.utils.SPHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by yunlong.su on 2018/4/3.
 */

public class HookUtils {

    private static final String METHOD_START_ACTIVITY = "startActivity";
    public static final String METHOD_GET_ACTIVITY_INFO = "getActivityInfo";
    public static final String EXTRA_REAL_WANTED_INTENT = "real_wanted_intent";
    private static final int LAUNCH_ACTIVITY = 100;

    private Context context;

    public void initHook(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hookActivityManagerApi26();

        } else {
            hookActivityManagerApi25();
        }

        HookPackageManager();
        hookActivityThreadHandler();
    }

    private void HookPackageManager() {
        //需要hook ActivityThread
        try {
            //获取ActivityThread的成员变量 sCurrentActivityThread
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object iPackageManagerObj = sPackageManagerField.get(null);


            Class<?> iPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
            InterceptPackageManagenHandler interceptInvocationHandler = new InterceptPackageManagenHandler(iPackageManagerObj);
            Object iPackageManagerObjProxy = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iPackageManagerClass}, interceptInvocationHandler);

            sPackageManagerField.set(null,iPackageManagerObjProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hookActivityManagerApi26() {
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

    private void hookActivityManagerApi25() {
        try {
            // 反射获取ActivityManagerNative的静态成员变量gDefault, 注意，在8.0的时候这个已经更改了
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefaultObj = gDefaultField.get(null);
            realHookActivityManager(gDefaultObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void realHookActivityManager(Object iActivityManagerSingleton) throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        // ActivityManagerNative.getDefault()方法在ActivityThread调用attach方法初始化的时候已经调用过，
        // 所以我们在这里拿到的instanceObj对象不为空，如果为空的话就没办法使用
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        Object instanceObj = mInstanceField.get(iActivityManagerSingleton);

        // 需要动态代理IActivityManager，把Singleton的成员变量mInstance的值设置为我们的这个动态代理对象
        // 但是有一点，我们不可能完全重写一个IActivityManager的实现类
        // 所以还是需要用到原始的IActivityManager对象，只是在调用某些方法的时候做一些手脚
        Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");
        InterceptInvocationHandler interceptInvocationHandler = new InterceptInvocationHandler(instanceObj);
        Object iActivityManagerObj = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iActivityManagerClass}, interceptInvocationHandler);
        mInstanceField.set(iActivityManagerSingleton, iActivityManagerObj);
    }

    private void hookActivityThreadHandler() {
        //需要hook ActivityThread
        try {
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
            mCallbackField.set(mHObj, new ActivityCallback(mHObj));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ActivityCallback implements Handler.Callback {

        private Handler mH;

        public ActivityCallback(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(Message msg) {
            // 跳转过程中发现如果被跳转的Activity继承自AppCompatActivity会报异常android.content.pm.PackageManager$NameNotFoundException:
            // 这是因为被跳转的Activity没有在AndroidManifest.xml中注册
            // 而AppCompatActivity在onCreate方法中会调用NavUtils.getParentActivityName方法，在这个方法中PackageManager会调用getActivityInfo
            // 实际上会再检查一次Activity的合法性
            if (msg.what == LAUNCH_ACTIVITY) {
                handleLaunchActivity(msg);
            }

//            mH.handleMessage(msg);
            return false;
        }

        private void handleLaunchActivity(Message msg) {
            //替换我们真正想要的intent
            try {
                Object activityClientRecord = msg.obj;
                Field intentField = activityClientRecord.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                //这个是代理ProxyActivity
                Intent interceptedIntent = (Intent) intentField.get(activityClientRecord);

                //真正想要跳转的 SecondActivity
                Intent realWanted = interceptedIntent.getParcelableExtra(EXTRA_REAL_WANTED_INTENT);
                if (realWanted != null) {
                    //如果不需要登录
                    Class<?> real = Class.forName(realWanted.getComponent().getClassName());
                    NeedLogin annotation = real.getAnnotation(NeedLogin.class);

                    if (annotation != null && !SPHelper.getBoolean("login", false)) {
                        //如果需要登录并且没有登录，跳转登录页面
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        loginIntent.putExtra(EXTRA_REAL_WANTED_INTENT, realWanted);
                        interceptedIntent.setComponent(loginIntent.getComponent());
                    } else {
                        interceptedIntent.setComponent(realWanted.getComponent());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class InterceptInvocationHandler implements InvocationHandler {
        Object originalObject;

        public InterceptInvocationHandler(Object originalObject) {
            this.originalObject = originalObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.e("binder hook", "method invoke: " + method.getName());
            //如果是startActivity方法，需要做一些手脚
            if (METHOD_START_ACTIVITY.equals(method.getName())) {
                Intent newIntent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof Intent) {
                        Intent wantedIntent = (Intent) arg;
                        //hook目标进入登录页面
//                        newIntent = new Intent(context, LoginActivity.class);

                        //加入目标Activity没有在清单文件中注册，我们就欺骗ActivityManagerService，启动一个代理页面
                        // 真正启动页面，会开始回调ActivityThread的handleLaunchActivity方法，调用这个方法前可以做点文章，启动我们想要启动的页面
                        newIntent = new Intent();
                        ComponentName componentName = new ComponentName(context, ProxyActivity.class);
                        newIntent.setComponent(componentName);

                        //把原始的跳转信息当作参数携带给代理类

                        newIntent.putExtra(EXTRA_REAL_WANTED_INTENT, wantedIntent);
                        index = i;
                    }
                }

                args[index] = newIntent;
            }
            return method.invoke(originalObject, args);
        }
    }


    private class InterceptPackageManagenHandler  implements InvocationHandler {
        Object originalObject;

        public InterceptPackageManagenHandler(Object originalObject) {
            this.originalObject = originalObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (METHOD_GET_ACTIVITY_INFO.equals(method.getName())) {
                return new ActivityInfo();
            }
            return method.invoke(originalObject,args);
        }
    }
}
