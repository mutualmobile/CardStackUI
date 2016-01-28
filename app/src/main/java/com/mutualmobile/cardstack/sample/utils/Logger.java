package com.mutualmobile.cardstack.sample.utils;

import android.util.Log;

import java.io.Serializable;

import static com.mutualmobile.cardstack.sample.utils.Config.LOG_DETAILED;
import static com.mutualmobile.cardstack.sample.utils.Config.LOG_LEVEL;
import static com.mutualmobile.cardstack.sample.utils.Config.LOG_LEVEL_DEBUG;
import static com.mutualmobile.cardstack.sample.utils.Config.LOG_LEVEL_ERROR;

public class Logger implements Serializable {
    String TAG;

    public Logger(String TAG) {
        this.TAG = TAG;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    private String getLogDetails() {
        if (LOG_DETAILED) {
            StackTraceElement e = Thread.currentThread().getStackTrace()[4];
            String fullClassName = e.getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = e.getMethodName();
            int lineNumber = e.getLineNumber();
            return "(" + className.split("\\$")[0] + ".java:" + lineNumber + ") " + methodName + "()";
        } else {
            return TAG;
        }
    }

    public void d() {
        d(".");
    }

    public void e() {
        e(".");
    }

    @SuppressWarnings("all")
    public void e(String message) {
        Log.e(getLogDetails(), message);
        if ((LOG_LEVEL & LOG_LEVEL_ERROR) != 0)
            Log.e(getLogDetails(), message);
    }

    @SuppressWarnings("all")
    public void d(String message) {
        Log.d(getLogDetails(), message);
        if ((LOG_LEVEL & LOG_LEVEL_DEBUG) != 0)
            Log.d(getLogDetails(), message);
    }
}
