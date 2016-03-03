package com.simpsonstrial2.utils;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import com.simpsonstrial2.MyApplication;

public class Measure {

    public static void loadScreenDimensions(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        MyApplication.screenWidth = size.x;
        MyApplication.screenHeight = size.y;
        System.out.println("Screen Width: " + MyApplication.screenWidth);
        System.out.println("Screen Height: " + MyApplication.screenHeight);
    }


}
