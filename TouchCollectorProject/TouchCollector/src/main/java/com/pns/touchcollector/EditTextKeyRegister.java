package com.pns.touchcollector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class EditTextKeyRegister extends android.widget.EditText {
    private KeyImeListener kiListener;
    private TouchListener tListener;

    public EditTextKeyRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeListener(KeyImeListener listener){
        kiListener = listener;
    }

    public void setTouchListener(TouchListener tListener) {
        this.tListener = tListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event){
        if (kiListener != null) kiListener.onKeyIme(keyCode, event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (tListener != null) tListener.onTouch(me);
        return false;
    }
}
