package com.sososeen09.host.internal;

import android.app.Service;
import android.content.ComponentName;
import android.support.v4.util.ArrayMap;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yunlong.su on 2018/4/9.
 */

public class ServiceHandler {
    private ArrayMap<ComponentName, Service> mServices = new ArrayMap<ComponentName, Service>();
    private ArrayMap<Service, AtomicInteger> mServiceCounters = new ArrayMap<Service, AtomicInteger>();

    public ArrayMap<ComponentName, Service> getServices() {
        return mServices;
    }

    public void setServices(ArrayMap<ComponentName, Service> services) {
        mServices = services;
    }

    public ArrayMap<Service, AtomicInteger> getServiceCounters() {
        return mServiceCounters;
    }

    public void setServiceCounters(ArrayMap<Service, AtomicInteger> serviceCounters) {
        mServiceCounters = serviceCounters;
    }

    public boolean isServiceAvailable(ComponentName component) {
        return this.mServices.containsKey(component);
    }

    public Service getService(ComponentName component) {
        return this.mServices.get(component);
    }

    public void rememberService(ComponentName component, Service service) {
        this.mServices.put(component, service);
        this.mServiceCounters.put(service, new AtomicInteger(0));
    }

    public AtomicInteger getServiceCounter(Service service) {
        return this.mServiceCounters.get(service);
    }

    public Service forgetService(ComponentName component) {
        Service service = this.mServices.remove(component);
        this.mServiceCounters.remove(service);
        return service;
    }
}
