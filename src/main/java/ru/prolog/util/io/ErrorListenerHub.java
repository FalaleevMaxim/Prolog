package ru.prolog.util.io;

import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;

public class ErrorListenerHub extends DeviceHub<ErrorListener> implements ErrorListener {

    public ErrorListenerHub(ErrorListener baseDevice) {
        super(baseDevice);
    }

    @Override
    public void prologRuntimeException(PrologRuntimeException e) {
        devices.forEach(device -> device.prologRuntimeException(e));
    }

    @Override
    public void runtimeException(RuntimeException e) {
        devices.forEach(device -> device.runtimeException(e));
    }

    @Override
    public void print(String s) {
        devices.forEach(device -> device.print(s));
    }

    @Override
    public void println(String s) {
        devices.forEach(device -> device.println(s));
    }
}
