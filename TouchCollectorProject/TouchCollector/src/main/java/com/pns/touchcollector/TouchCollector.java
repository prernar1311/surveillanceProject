package com.pns.touchcollector;

import android.view.MotionEvent;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by nicolascrowell on 2014/1/12.
 */
public class TouchCollector extends DataStreamCollector<MotionEvent> implements TouchListener {
    private boolean recordingNow;
    EditTextKeyRegister register;

    TouchCollector(EditTextKeyRegister register) {
        if (register == null) throw new IllegalArgumentException("Passed a null key register.");
        this.register = register;
    }

    public void startRecording(String s) {
        if (recordingNow) throw new IllegalStateException("Already recording");
        register.setTouchListener(this);
    }

    public void stopRecording() {
        if (!recordingNow) throw new IllegalStateException("Wasn't recording!");
        register.setTouchListener(null);
    }

    public void onTouch(MotionEvent me) {
        final int action = me.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            registerEvent(me);
        }
    }

    public String getName() { return "MotionEvents"; }

    public DataConverter<MotionEvent> getConverter() {
        return new DataConverter<MotionEvent>() {
            public JSONObject toJson(MotionEvent e) throws JSONException {
                JSONObject j = new JSONObject();
                j.put("actionDown", e.getAction() == MotionEvent.ACTION_DOWN);
                j.put("timeMillis", e.getEventTime());
                j.put("coord", (new JSONObject()).put("x", e.getX())
                        .put("y", e.getY()));
                return j;
            }
        };
    }
}
