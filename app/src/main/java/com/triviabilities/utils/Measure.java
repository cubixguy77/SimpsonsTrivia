package com.triviabilities.utils;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import com.triviabilities.MyApplication;

public class Measure {

    public static void loadScreenDimensions(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        MyApplication.screenWidth = size.x;
        MyApplication.screenHeight = size.y;
    }


}
