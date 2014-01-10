package com.pns.touchcollector;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class EditTextKeyRegister extends android.widget.EditText {
    private KeyImeListener kil;

    public EditTextKeyRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeListener(KeyImeListener listener){
        kil = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event){
        if (kil != null){
            kil.onKeyIme(keyCode, event);
        }
        return false;
    }
}
