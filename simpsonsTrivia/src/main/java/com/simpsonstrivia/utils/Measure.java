package com.simpsonstrivia.utils;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import com.simpsonstrivia.MyApplication;

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
