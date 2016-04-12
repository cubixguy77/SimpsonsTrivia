package com.triviabilities;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {

    private static Context context;
    public static int screenWidth;
    public static int screenHeight;

    public void onCreate(){
        super.onCreate();
        LeakCanary.install(this);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
