package com.m.apk.extractor.skech.helpers.toast;

import java.lang.CharSequence;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    private android.content.Context context;
    private int durationShort = Toast.LENGTH_SHORT;
    private int durationLong = Toast.LENGTH_LONG;
    
    public ToastHelper() {}
        
    public ToastHelper(Context context) {
        this.setContext(context);
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public int getDurationShort() {
        return this.durationShort;
    }
    
    public int getDurationLong() {
        return this.durationLong;
    }
    
    public void showToast(CharSequence text) {
        if (context != null) {
            Toast.makeText(context, text, this.getDurationLong()).show();
        } else {
            Log.e("CONTEXT", "Content is null, please pass the context correctly.");
        }
    }
    
    public void showLongToast(CharSequence text) {
        if (context != null) {
            Toast.makeText(context, text, this.getDurationLong()).show();
        } else {
            Log.e("CONTEXT", "Content is null, please pass the context correctly.");
        }
    }
}
