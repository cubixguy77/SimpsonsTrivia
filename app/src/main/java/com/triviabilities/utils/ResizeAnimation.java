package com.triviabilities.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation
{
    private int startHeight;
    private int deltaHeight; // distance between start and end height
    private View view;

    public ResizeAnimation (View v, int targetHeight) {
        this.view = v;
        this.setParams(v.getLayoutParams().height, targetHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
        view.requestLayout();
    }

    /**
     * set the starting and ending height for the resize animation
     * starting height is usually the views current height, the end height is the height
     * we want to reach after the animation is completed
     * @param start height in pixels
     * @param end height in pixels
     */
    public void setParams(int start, int end) {

        this.startHeight = start;
        deltaHeight = end - startHeight;
    }

    /**
     * set the duration for the animation
     */
    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
