package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AccessGyroscope extends AccessSensor {
    public AccessGyroscope(SensorManager sm) {
        super(sm);
    }
    protected int getSensorType() {
        return Sensor.TYPE_ORIENTATION;
    }
    public String getName() { return "SensorGyroscope"; }
}
