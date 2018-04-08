package com.sososeen09.host;

import dalvik.system.DexClassLoader;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class PluginClassLoader extends DexClassLoader {
    public PluginClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
