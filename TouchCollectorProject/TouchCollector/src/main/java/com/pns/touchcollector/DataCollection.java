package com.pns.touchcollector;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import com.pns.touchcollector.KeyCollector.KeyCodeEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nicolascrowell on 2014/1/10.
 */
public class DataCollection {
    private SensorManager sManager;

    private AccessGyroscope aGyro;
    private AudioRecorder aRecorder;
    private AccessAccelerometer aAccel;
    private KeyCollector kCollector;

    private DataCollector[] collectors;

    List<SensorEvent> gyroEvents;
    List<SensorEvent> accelEvents;
    String recordingFilename;
    List<KeyCodeEvent> keyEvents;
    long startTime;

    public DataCollection(Context context, EditTextKeyRegister r) {
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        aGyro = new AccessGyroscope(sManager);
        aAccel = new AccessAccelerometer(sManager);
        kCollector = new KeyCollector(r);

        collectors = new DataCollector[4];
        collectors[0] = aGyro;
        collectors[1] = aAccel;
        collectors[2] = aRecorder;
        collectors[3] = kCollector;
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        for (DataCollector c : collectors)
            c.startRecording();
    }

    public DataSession stopAndGetSession() {
        for (DataCollector c : collectors)
            c.stopRecording();

        gyroEvents = aGyro.getData();
        accelEvents = aAccel.getData();
        recordingFilename = aRecorder.getData();
        keyEvents = kCollector.getData();

        return new DataSession(gyroEvents, accelEvents, recordingFilename, keyEvents, startTime);
    }

    /** Magic array literals */
    static  <T> T[] array(T... elems) {
        return elems;
    }

    static interface DataCollector <Data> {
        public void startRecording();
        public void stopRecording();
        public Data getData();
    }

    static abstract class DataStreamCollector<Event> implements DataCollector<ArrayList<Event>> {
        private final LinkedBlockingQueue<Event> q = new LinkedBlockingQueue<Event>();
        protected void registerEvent(Event d) {
            try {
                q.put(d);
            } catch (InterruptedException e) {
                try {
                    q.put(d);
                } catch (InterruptedException f) {
                    Log.e("DataStreamCollector",
                            "Interrupted while trying to enqueue event " + d.toString(), e);
                }
            }
        }

        /** Flushes old data and returns in an ordered list. */
        public List<Event> getData() {
            List<Event> l = new ArrayList<Event>();
            q.drainTo(l);
            return l;
        }
    }

    static class DataSession {
        private final List<SensorEvent> gyro;
        private final List<SensorEvent> accel;
        private final List<KeyCodeEvent> keys;
        private final String recording;
        private final long startTime;

        private final JSONObject j;

        public DataSession(List<SensorEvent> Gyro, List<SensorEvent> Accel,
                           String Mic, List<KeyCodeEvent> Keys, long startingTimestamp) {
            gyro = Gyro;
            accel = Accel;
            recording = Mic;
            startTime = startingTimestamp;
            try {
                j = buildSerializedEvents();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public JSONObject serializedEvents() {
            return j;
        }

        private JSONObject buildSerializedEvents() throws JSONException {
            return new JSONObject()
                    .put("startTimestamp", startTime)
                    .put("gyro",          serializeSensors(gyro,  gyroToJSON))
                    .put("accelerometer", serializeSensors(accel, accelToJSON))
                    .put("keys", serializeKeycodes(keys));
        }

        private static JSONObject serializeKeycodes(List<KeyCodeEvent> l) throws JSONException {
            JSONObject j = new JSONObject();
            for (KeyCodeEvent kce : l) {;
                j.accumulate("events", new JSONObject()
                        .put("keycode", kce.e.getUnicodeChar())
                        .put("time", kce.e.getEventTime()));
            }
            return j;
        }

        private static JSONObject serializeSensors(List<SensorEvent> le, EventJSONer converter)
                throws JSONException {
            return new JSONObject()
                    .put("events", eventListToJSON(le, converter))
                    .put("name", le.size() > 0 ? le.get(0).sensor.getName() : "no_events");
        }

        EventJSONer<SensorEvent> accelToJSON = new EventJSONer<SensorEvent>() {
            public JSONObject toJSON(SensorEvent se) throws JSONException {
                float[] vals = se.values;
                return new JSONObject()
                    .put("accuracy", se.accuracy)
                    .put("timestamp", se.timestamp / 1000L)
                    .put("x", vals[0])
                    .put("y", vals[1])
                    .put("z", vals[2]);
            }
        };

        EventJSONer<SensorEvent> gyroToJSON = accelToJSON;

        private static JSONArray eventListToJSON(List<SensorEvent> le, EventJSONer<SensorEvent> converter)
                throws JSONException {
            JSONArray a = new JSONArray();
            for (SensorEvent e : le) {
                a.put(converter.toJSON(e));
            }
            return a;
        }

        private interface EventJSONer<EventType> {
            JSONObject toJSON(EventType e) throws JSONException;
        }
    }
}