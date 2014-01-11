package com.pns.touchcollector;

import android.hardware.Sensor;

public class AccessAccelerometer extends AccessSensor {
    private static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;

    protected int getSensorType() {
        return SENSOR_TYPE;
    }
}
