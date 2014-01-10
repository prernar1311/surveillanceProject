package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.pns.touchcollector.InputCollection.DataStreamCollector;

public class AccessGyroscope extends DataStreamCollector<SensorEvent> implements
        SensorEventListener {
    private final SensorManager sManager;

    public AccessGyroscope(SensorManager sm) {
        sManager = sm;
    }

    public void startRecording() {
        /*  Register the sensor listener to listen to the gyroscope sensor, use
         *  the callbacks defined in this class, and gather the sensor
         *  information as quickly as possible */
        sManager.registerListener(this,
            sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopRecording() {
        sManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        assert event.values.length == 3;
        //if sensor is unreliable, ignore event
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        registerEvent(event);
/*
 *
 *        // else it will output the Roll, Pitch and Yawn values
 *        tv.setText("Orientation X (Roll) :"+ Float.toString(event.values[2]) +"\n"+
 *                   "Orientation Y (Pitch) :"+ Float.toString(event.values[1]) +"\n"+
 *                   "Orientation Z (Yaw) :"+ Float.toString(event.values[0]));
 */
    }
}
