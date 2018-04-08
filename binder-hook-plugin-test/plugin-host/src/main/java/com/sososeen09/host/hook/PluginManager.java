package com.sososeen09.host.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.ArrayMap;

import com.sososeen09.host.PluginClassLoader;
import com.sososeen09.host.utils.FileUtils;

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


    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
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
            pluginApk = new PluginApk();


            File dexOptFile = applicationContext.getDir("dex", Context.MODE_PRIVATE);
            pluginApk.setDexClassLoader(new PluginClassLoader(pluginPath, dexOptFile.getAbsolutePath(), null, applicationContext.getClassLoader()));

            AssetManager assetManager = AssetManager.class.newInstance();

            Method addPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);

            addPathMethod.invoke(assetManager, pluginPath);

            pluginApk.setResources(new Resources(assetManager, applicationContext.getResources().getDisplayMetrics(), applicationContext.getResources().getConfiguration()));

            PackageManager packageManager = applicationContext.getPackageManager();
            pluginApk.setPackageInfo(packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES));
            parseLoadedApkPlugin(pluginApk, pluginPath);

            loadedApk.put(pluginPath, pluginApk);

        }
    }

    public PluginApk getLoadedPlugin(String pluginPath) {
        return loadedApk.get(pluginPath);
    }


    /**
     * 解析插件中的静态BroadcastReceiver，然后进行注册，实际上就是静态广播动态注册化
     */
    private void parseLoadedApkPlugin(PluginApk pluginApk, String path) throws Exception {

        addLoadedApk(pluginApk, path);
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
        Method getPackageInfoNoCheck = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class,compatibilityInfoClass);
        getPackageInfoNoCheck.setAccessible(true);
        Object loadedApk = getPackageInfoNoCheck.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo);

        pluginApk.setApplicationInfo(applicationInfo);
        pluginApk.setLoadedApk(loadedApk);

        // 4. 设置LoadedApk的mClassLoader 为DexClassLoader
        // 把LoadedApk中的classLoad设置为DexClassLoader
        String odexPath = FileUtils.getPluginOptDexDir(applicationContext,applicationInfo.packageName).getPath();
        String libDir = FileUtils.getPluginLibDir(applicationContext,applicationInfo.packageName).getPath();
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
        return applicationInfo;
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
            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) pluginApk.getDexClassLoader().loadClass(receiverClassName).newInstance();
            for (IntentFilter intentFilter : intentFilters) {
                applicationContext.registerReceiver(broadcastReceiver, intentFilter);
            }
        }
    }
}
