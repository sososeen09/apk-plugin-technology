package com.sososeen09.host.hook;

import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.ArrayMap;

import com.sososeen09.host.PluginClassLoader;
import com.sososeen09.host.internal.ServiceHandler;
import com.sososeen09.host.utils.FileUtils;
import com.sososeen09.host.utils.ReflectUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class PluginManager {
    private static Context applicationContext;
    private static PluginManager sPluginManager;

    private ArrayMap<String, PluginApk> loadedApk = new ArrayMap<>();
    private Map<String, Object> sLoadedApk = new HashMap<>();
    private ServiceHandler mServiceHandler;
    private Instrumentation mInstrumentation;
    private IActivityManager mIActivityManager;

    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    private PluginManager() {
        mServiceHandler = new ServiceHandler();
        mInstrumentation = ReflectUtil.getInstrumentation(applicationContext);

        try {
            HookHelper.initHook(applicationContext);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PluginManager getInstance() {
        if (sPluginManager == null) {
            sPluginManager = new PluginManager();
        }
        return sPluginManager;
    }

    public void loadPlugin(String pluginPath) throws Exception {
        PluginApk pluginApk = loadedApk.get(pluginPath);
        if (pluginApk == null) {
            pluginApk = new PluginApk(this);

            PackageManager packageManager = applicationContext.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS);

            File dexOptDir = applicationContext.getDir("dex", Context.MODE_PRIVATE);
            File dexOptFile = new File(dexOptDir, packageArchiveInfo.packageName);
            if (!dexOptFile.exists()) {
                dexOptFile.mkdirs();
            }
            pluginApk.setClassLoader(new PluginClassLoader(pluginPath, dexOptFile.getAbsolutePath(), null, applicationContext.getClassLoader()));

            AssetManager assetManager = AssetManager.class.newInstance();

            Method addPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);

            addPathMethod.invoke(assetManager, pluginPath);

            pluginApk.setResources(new Resources(assetManager, applicationContext.getResources().getDisplayMetrics(), applicationContext.getResources().getConfiguration()));
            pluginApk.setAssetManager(assetManager);

            pluginApk.setPackageInfo(packageArchiveInfo);
            parseLoadedApkPlugin(pluginApk, pluginPath);

            loadedApk.put(pluginApk.getPackageInfo().packageName, pluginApk);

        }
    }

    public PluginApk getLoadedPlugin(ComponentName componentName) {
        return loadedApk.get(componentName.getPackageName());
    }

    public PluginApk getLoadedPlugin(String packageName) {
        return loadedApk.get(packageName);
    }


    /**
     * 解析插件中的静态BroadcastReceiver，然后进行注册，实际上就是静态广播动态注册化
     */
    private void parseLoadedApkPlugin(PluginApk pluginApk, String path) throws Exception {

        addLoadedApk(pluginApk, path);
        Map<ComponentName, ServiceInfo> componentNameServiceInfoMap = generateServiceInfo(path);
        pluginApk.setServiceInfoMap(componentNameServiceInfoMap);
    }


    private void addLoadedApk(PluginApk pluginApk, String path) throws Exception {
        //为插件pluginApk添加LoadedApk，用于使用DexClassLoader加载插件中的类
        // 1. 获取ActivityThread 对象 ，通过调用currentActivityThread方法好过于直接使用获取变量的方式，因为方法是静态public的，不容易变化
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        //2. 获取ActivityThread的成员变量mPackages
        Field mPackagesField = activityThreadClass.getDeclaredField("mPackages");
        mPackagesField.setAccessible(true);
        Map mPackages = (Map) mPackagesField.get(currentActivityThread);

        //3. 需要创建一个LoadedApk，可以调用ActivityThread 的getPackageInfoNoCheck方法，传递两个参数（ApplicationInfo ai,CompatibilityInfo compatInfo）
        ApplicationInfo applicationInfo = generateApplicationInfo(path);
        Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultCompatibilityInfoField = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityInfoField.setAccessible(true);
        Object defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null);
        Method getPackageInfoNoCheck = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, compatibilityInfoClass);
        getPackageInfoNoCheck.setAccessible(true);
        Object loadedApk = getPackageInfoNoCheck.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo);

        pluginApk.setApplicationInfo(applicationInfo);
        pluginApk.setLoadedApk(loadedApk);

        // 4. 设置LoadedApk的mClassLoader 为DexClassLoader
        // 把LoadedApk中的classLoad设置为DexClassLoader
        String odexPath = FileUtils.getPluginOptDexDir(applicationContext, applicationInfo.packageName).getPath();
        String libDir = FileUtils.getPluginLibDir(applicationContext, applicationInfo.packageName).getPath();
        ClassLoader classLoader = new PluginClassLoader(path, odexPath, libDir, ClassLoader.getSystemClassLoader());

        Field mClassLoaderField = loadedApk.getClass().getDeclaredField("mClassLoader");
        mClassLoaderField.setAccessible(true);
        mClassLoaderField.set(loadedApk, classLoader);

        //5 把ClassLoader添加到mPackages中
        // 由于是弱引用, 因此我们必须在某个地方存一份, 不然容易被GC; 那么就前功尽弃了.
        sLoadedApk.put(applicationInfo.packageName, loadedApk);

        WeakReference weakReference = new WeakReference(loadedApk);
        mPackages.put(applicationInfo.packageName, weakReference);
    }


    /**
     * 调用PackageParser的parsePackage去手动解析一个未安装的Apk中的AndroidManifest.xml文件
     *
     * @param apkFilePath
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public static ApplicationInfo generateApplicationInfo(String apkFilePath)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {

        // 调用PackageParser的静态方法generateApplicationInfo获取applicationInfo，但是这个方法需要4个参数
        // public static ApplicationInfo generateApplicationInfo(Package p, int flags,PackageUserState state)

        // 1. 第1个参数Package，可以通过PackageParser的parsePackage方法来获取
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
        Object packageParser = packageParserClass.newInstance();
        Method parsePackage = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
        //调用PackageParser的parsePackage方法，获得了PackageParser$Package对象
        Object packageObj = parsePackage.invoke(packageParser, new File(apkFilePath), 0);

        // 2. 第2个参数传0即可

        // 3. 第3个参数，默认构造一个
        Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Object packageUserStateClassObj = packageUserStateClass.newInstance();


        //4. 获取PackageParser的静态方法generateApplicationInfo
//        Class<?> packageParser$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Method generateApplicationInfoMethod = packageParserClass.getDeclaredMethod("generateApplicationInfo",
                packageObj.getClass(),
                int.class,
                packageUserStateClass);

        //5 调用generateApplicationInfo方法
        ApplicationInfo applicationInfo = (ApplicationInfo) generateApplicationInfoMethod.invoke(null, packageObj, 0, packageUserStateClassObj);
        applicationInfo.sourceDir = apkFilePath;
        applicationInfo.publicSourceDir = apkFilePath;

        // 6 Package中存放了AndroidManifest.xml存放的信息，可以取出插件中对应的Service注册信息
        return applicationInfo;
    }

    public static ArrayMap<ComponentName, ServiceInfo> generateServiceInfo(String apkFilePath)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        // 1. 第1个参数Package，可以通过PackageParser的parsePackage方法来获取
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
        Object packageParser = packageParserClass.newInstance();
        Method parsePackage = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
        //调用PackageParser的parsePackage方法，获得了PackageParser$Package对象
        Object packageObj = parsePackage.invoke(packageParser, new File(apkFilePath), 0);

        //2 获取ArrayList<Service>
        Field serviceField = packageObj.getClass().getDeclaredField("services");
        List services = (List) serviceField.get(packageObj);

        //3 需要把Service转为ServiceInfo，调用PackageParser的静态方法generateServiceInfo
        Class<?> packageParser$ServiceClass = Class.forName("android.content.pm.PackageParser$Service");
        Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Class<?> userHandler = Class.forName("android.os.UserHandle");
        Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
        int userId = (Integer) getCallingUserIdMethod.invoke(null);
        Object defaultUserState = packageUserStateClass.newInstance();

        // 需要调用 android.content.pm.PackageParser#generateActivityInfo(android.content.pm.ActivityInfo, int, android.content.pm.PackageUserState, int)
        Method generateReceiverInfo = packageParserClass.getDeclaredMethod("generateServiceInfo",
                packageParser$ServiceClass, int.class, packageUserStateClass, int.class);

        ArrayMap<ComponentName, ServiceInfo> mServiceInfoMap = new ArrayMap<>(16);
        //4  解析出所有ServiceInfo，并与ComponentName对应起来
        for (Object service : services) {
            ServiceInfo info = (ServiceInfo) generateReceiverInfo.invoke(packageParser, service, 0, defaultUserState, userId);
            mServiceInfoMap.put(new ComponentName(info.packageName, info.name), info);
        }

        return mServiceInfoMap;
    }


    private void registerReceiver(PluginApk pluginApk, Object packageObj) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        //获取PackageParser$Package类的receivers字段，是public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
        Field fieldReceivers = packageObj.getClass().getDeclaredField("receivers");
        //获取receivers集合，类型是ArrayList<Activity>
        List receivers = (List) fieldReceivers.get(packageObj);

        // 此Activity非四大组件中的Activity，是表示的AndroidManifest.xml中的activity或者receiver结点
        Class componentClass = Class.forName("android.content.pm.PackageParser$Component");
        Field intentsField = componentClass.getDeclaredField("intents");
        Field classNameField = componentClass.getDeclaredField("className");

        for (Object receiver : receivers) {
            ArrayList<IntentFilter> intentFilters = (ArrayList<IntentFilter>) intentsField.get(receiver);
            String receiverClassName = (String) classNameField.get(receiver);
            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) pluginApk.getClassLoader().loadClass(receiverClassName).newInstance();
            for (IntentFilter intentFilter : intentFilters) {
                applicationContext.registerReceiver(broadcastReceiver, intentFilter);
            }
        }
    }


    public ServiceHandler getComponentsHandler() {
        return mServiceHandler;
    }

    public Instrumentation getInstrumentation() {
        return mInstrumentation;
    }

    public Context getContext() {
        return applicationContext;
    }

    public IActivityManager getActivityManager() {
        return mIActivityManager;
    }
}
