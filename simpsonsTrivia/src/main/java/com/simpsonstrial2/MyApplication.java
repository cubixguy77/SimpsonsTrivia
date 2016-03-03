package com.simpsonstrial2;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    public static int screenWidth;
    public static int screenHeight;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
