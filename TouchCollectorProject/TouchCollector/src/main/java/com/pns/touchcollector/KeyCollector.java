package com.pns.touchcollector;

import android.view.KeyEvent;

import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 */
public class KeyCollector extends DataStreamCollector<KeyEvent> implements KeyImeListener {
    private final EditTextKeyRegister register;

    public KeyCollector(EditTextKeyRegister r) {
        register = r;
    }

    @Override
    public void startRecording(String s) {
        register.setKeyImeListener(this);
    }

    @Override
    public void stopRecording() {
        register.setKeyImeListener(null);
    }

    @Override
    public void onKeyIme(int keyCode, KeyEvent e) {
        int action = e.getAction();
        if ((action == KeyEvent.ACTION_UP || action == KeyEvent.ACTION_DOWN) &&
             e.isPrintingKey()) {
                registerEvent(e);
        }
    }

    @Override
    public String getName() {
        return "keyEvents";
    }

    @Override
    public DataConverter<KeyEvent> getConverter() {
        return new DataConverter<KeyEvent>() {
            public JSONObject toJson(KeyEvent e) throws JSONException {
                return new JSONObject()
                    .put("actionDown", e.getAction() == KeyEvent.ACTION_DOWN)
                    .put("char", e.getUnicodeChar())
                    .put("time", e.getEventTime());
            }
        };
    }
}
