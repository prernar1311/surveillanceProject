package com.pns.touchcollector;

import android.view.KeyEvent;

import com.pns.touchcollector.DataCollection.DataStreamCollector;
import com.pns.touchcollector.KeyCollector.KeyCodeEvent;

/**
 *
 */
public class KeyCollector extends DataStreamCollector<KeyCodeEvent>
        implements KeyImeListener {
    private final EditTextKeyRegister register;

    public static class KeyCodeEvent {
        public final int keycode;
        public final KeyEvent e;
        public KeyCodeEvent(int keycode, KeyEvent e) {
            this.keycode = keycode;
            this.e = e;
        }
    }

    public KeyCollector(EditTextKeyRegister r) {
        register = r;
    }

    public void startRecording() {
        register.setKeyImeListener(this);
    }

    public void stopRecording() {
        register.setKeyImeListener(null);
    }

    public void onKeyIme(int keyCode, KeyEvent e) {
        int action = e.getAction();
        if ((action == KeyEvent.ACTION_UP || action == KeyEvent.ACTION_DOWN) &&
             e.isPrintingKey()) {
                registerEvent(new KeyCodeEvent(keyCode, e));
        }
    }
}
