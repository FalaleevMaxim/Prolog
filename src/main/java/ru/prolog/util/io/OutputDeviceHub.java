package ru.prolog.util.io;

public class OutputDeviceHub extends DeviceHub<OutputDevice> implements OutputDevice {
    public OutputDeviceHub(OutputDevice baseDevice) {
        super(baseDevice);
    }

    @Override
    public void print(String s) {
        devices.forEach(d->d.print(s));
    }

    @Override
    public void println(String s) {
        devices.forEach(d->d.println(s));
    }


}
