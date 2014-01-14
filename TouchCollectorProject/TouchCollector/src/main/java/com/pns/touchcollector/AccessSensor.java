package com.pns.touchcollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.pns.touchcollector.DataCollection.SensorUnavailableException;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

abstract class AccessSensor extends DataStreamCollector<SensorEvent> implements
        SensorEventListener {
    protected final SensorManager sManager;
    protected Sensor sensor;

    public AccessSensor(SensorManager sm) {
        if (sm == null) {
            throw new IllegalArgumentException("SensorManager was null.");
        }
        sManager = sm;
        sensor = sm.getDefaultSensor(getSensorType());
    }

    /** Get the sensor type for this sensor (from {@link Sensor}) */
    protected abstract int getSensorType();
    /** Get the name for this sensor. */
    public abstract String getName();

    public void startRecording(String s) throws SensorUnavailableException {
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
    public void onSensorChanged(SensorEvent event) {
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


    @Override
    public DataConverter<SensorEvent> getConverter() {
        return new DataConverter<SensorEvent>() {
            // sensor timestamps are in nanoseconds of uptime
            private static final long NANO2MILLI = 1000000L;
            public JSONObject toJson(SensorEvent e) throws JSONException {
                float[] vals = e.values;
                return new JSONObject()
                    .put("accuracy", e.accuracy)
                    .put("timestamp", e.timestamp / NANO2MILLI)
                    .put("x", vals[0])
                    .put("y", vals[1])
                    .put("z", vals[2]);
            }
        };
    }
}
