package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccessGyroscope extends AccessSensor;
    protected int getSensorType() {
        return SENSOR.TYPE_ORIENTATION;
    }
}
