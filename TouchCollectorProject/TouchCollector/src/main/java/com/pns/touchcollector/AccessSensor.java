package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

abstract class AccessSensor extends DataStreamCollector<SensorEvent> implements
        SensorEventListener {
    protected final SensorManager sManager;
    protected final Sensor sensor;

    public AccessSensor(SensorManager sm) {
        if (sm == null) {
            throw new IllegalArgumentException("SensorManager was null.")
        }
        sManager = sm;
        sensor = sm.getDefaultSensor(getSensorType());
    }

    /** Get the sensor type for this sensor (from {@link Sensor}) */
    public abstract int getSensorType();

    public void startRecording() throws SensorUnavailableException {
        if (sensor == null) sensor = sManager.getDefaultSensor(getSensorType());
        if (sensor == null) {
            throw new SensorUnavailableException("Could not access sensor " + getSensorType());
        }

        /*  Register the sensor listener to listen to the gyroscope sensor, use
         *  the callbacks defined in this class, and gather the sensor
         *  information as quickly as possible */
        sManager.registerListener(this,
            sManager.getDefaultSensor(getSensorType()),
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

    public static class SensorUnavailableException extends Exception {}
}
