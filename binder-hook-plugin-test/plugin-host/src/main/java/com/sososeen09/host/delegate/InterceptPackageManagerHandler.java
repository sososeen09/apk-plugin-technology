package com.sososeen09.host.delegate;

import android.content.pm.ActivityInfo;

import com.sososeen09.host.internal.Constants;
import com.sososeen09.host.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by yunlong.su on 2018/4/8.
 */
public class InterceptPackageManagerHandler implements InvocationHandler {
    Object originalObject;

    public InterceptPackageManagerHandler(Object originalObject) {
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtils.d("method:" + method.getName() + " called with args:" + Arrays.toString(args));
        if (Constants.METHOD_GET_ACTIVITY_INFO.equals(method.getName())) {
            return new ActivityInfo();
        }
        return method.invoke(originalObject, args);
    }
}
