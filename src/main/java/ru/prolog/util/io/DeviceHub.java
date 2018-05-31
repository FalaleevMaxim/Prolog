package ru.prolog.util.io;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class DeviceHub<T> {
    protected Set<T> devices = new HashSet<>();

    public DeviceHub(T baseDevice) {
        this.devices .add(baseDevice);
    }

    public Collection<T> getDevices(){
        return Collections.unmodifiableSet(devices);
    }

    public void add(T device){
        devices.add(device);
    }

    private boolean remove(T device){
        return devices.remove(device);
    }

    public void removeAll(){
        devices.clear();
    }

    public void removeStd(){
        devices.removeIf(t -> t instanceof StdIO);
    }
}
