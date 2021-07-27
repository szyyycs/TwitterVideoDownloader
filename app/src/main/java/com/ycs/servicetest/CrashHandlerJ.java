package com.ycs.servicetest;


import android.util.Log;

import androidx.annotation.NonNull;


class CrashHandlerJ implements Thread.UncaughtExceptionHandler {

    static CrashHandlerJ cc=null;
    public CrashHandlerJ() {

    }
    static CrashHandlerJ getInstant(){
        if(cc==null){
            cc=new CrashHandlerJ();
        }
        return cc;
    }
    static void init() {
        Thread.setDefaultUncaughtExceptionHandler(cc);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

        Log.e(MainActivity.TAG, "*** FATAL EXCEPTION IN SYSTEM PROCESS: " + t.getName(), e);
        Log.println(Log.ERROR,"FATAL:",e.getMessage());
        LogUtil.writeLog();

    }
}