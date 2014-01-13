package com.pns.touchcollector;

import android.view.MotionEvent;

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
        registerEvent(me);
    }
}
