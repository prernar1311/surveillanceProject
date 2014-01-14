package com.pns.touchcollector;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Arrays;

/**
 * Created by nicolascrowell on 2014/1/10.
 */
public class DataCollection {
    private static final String LTAG = "DataCollection";
    private final String name;
    //private static final DataCollection instance;
    private SensorManager sManager;

    private AccessGyroscope aGyro;
    private AudioRecorder aRecorder;
    private AccessAccelerometer aAccel;
    private KeyCollector kCollector;

    private DataCollector[] collectors;
    private DataStreamCollector[] streamCollectors;

    //List<SensorEvent> gyroEvents;
    //List<SensorEvent> accelEvents;
    String recordingFilename;
    //List<KeyEvent> keyEvents;
    long startTime;

    public DataCollection(Context context, EditTextKeyRegister r, String name) {
        Log.i(LTAG, "Starting data collection!");
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        aGyro = new AccessGyroscope(sManager);
        aAccel = new AccessAccelerometer(sManager);
        aRecorder = AudioRecorder.getInstance();
        kCollector = new KeyCollector(r);
        this.name = name;

        streamCollectors = new DataStreamCollector[3];
        collectors       = new DataCollector[4];


        /*streamCollectors = array(aGyro, aAccel, kCollector);
        collectors = array(aGyro, aAccel, kCollector, aRecorder);*/

        collectors[0] = streamCollectors[0] = aGyro;
        collectors[1] = streamCollectors[1] = aAccel;
        collectors[2] = streamCollectors[2] = kCollector;
        collectors[3] = aRecorder;
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        for (DataCollector c : collectors) {
            if (c == null) throw new IllegalStateException("Had a null DataCollector at start!");
            try {
                c.startRecording(name);
                Log.d(LTAG, "Starting to recording with DataCollector " + c.toString());
            }
            catch (SensorUnavailableException e) {
                Log.e(LTAG, "Sensor was unavailable: " + e.getMessage());
            }
        }
    }

    public JSONObject stopAndGetSession() {
        for (DataCollector c : collectors) {
            if (c == null) throw new IllegalStateException("Had a null DataCollector at stop!");
            try {
                c.stopRecording();
            }
            catch (SensorUnavailableException s) {
                Log.e(LTAG, "Sensor " + c + " failed at stop recording " + s.getMessage());
            }
        }

        JSONObject j = new JSONObject();
        jsonPutReliable(j, "startTime", startTime);
        for (DataStreamCollector c : streamCollectors) {
            JSONObject serialized;
            try {
                serialized = c.getSerializedData();
            } catch (JSONException je) {
                Log.w(LTAG, "Failed trying to serialize " + c.getName(), je);
                serialized = (JSONObject) JSONObject.NULL;
            }
            jsonPutReliable(j, c.getName(), serialized);
        }
        jsonPutReliable(j, "mic_filename",
                recordingFilename == null ? JSONObject.NULL : recordingFilename);

        return j;
    }

    static void jsonPutReliable(JSONObject j, String key, Object value) {
        try {
            j.put(key, value);
        } catch (JSONException je) {
            Log.w(LTAG, "Reliable json put failed: " + key, je);
        }
        try {
            j.put(key, JSONObject.NULL);
        } catch (JSONException je) {
            Log.w(LTAG, "Reliable json failed to put placeholder.", je);
        }
    }

    /** Magic array literals */
    static  <T> T[] array(T... elems) {
        return elems;
    }

    static interface DataCollector <Data> {
        public void startRecording(String s) throws SensorUnavailableException;
        public void stopRecording()  throws SensorUnavailableException;
        public JSONObject getSerializedData() throws JSONException;
    }

    static class SensorUnavailableException extends Exception {
        public SensorUnavailableException(String s) {
            super(s);
        }

        public SensorUnavailableException(String s, Throwable t) {
            super(s, t);
        }
    }
}
