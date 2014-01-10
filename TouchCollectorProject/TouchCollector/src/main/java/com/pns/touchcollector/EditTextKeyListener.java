package com.pns.touchcollector;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;


public class EditTextKeyListener extends android.widget.EditText {
    private KeyImeChange keyImeChangeListener;

    public EditTextKeyListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}
