package com.triviabilities.views;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SingleTapGesture extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }
}
