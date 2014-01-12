package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AccessAccelerometer extends AccessSensor {
    public AccessAccelerometer(SensorManager sm) {
        super(sm);
    }

    protected int getSensorType() {
        return Sensor.TYPE_LINEAR_ACCELERATION;
    }
    protected String getName() {
        return "AccelerometerSensor";
    }
}
