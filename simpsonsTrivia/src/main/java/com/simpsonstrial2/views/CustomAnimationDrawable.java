package com.simpsonstrial2.views;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class CustomAnimationDrawable extends AnimationDrawable
{

    /** Handles the animation callback. */
    private Handler mAnimationHandler;

    private int mDelayInMillis = 0;

    public CustomAnimationDrawable(AnimationDrawable aniDrawable, boolean reverse) {
        /* Add each frame to our animation drawable */
        if (reverse)
        {
            for (int i = aniDrawable.getNumberOfFrames() - 1; i > 0; i--)
                this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
        }
        else
        {
            for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++)
                this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
        }

        this.setOneShot(true);
    }

    public void setStartDelay(int delay)
    {
        this.mDelayInMillis = delay;
    }

    @Override
    public void start() {
        /*
        onAnimationStart();
        super.start();
        mAnimationHandler = new Handler();
        mAnimationHandler.postDelayed(new Runnable() {

            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());
        */

        Handler mDelayHandler = new Handler();
        mDelayHandler.postDelayed(new Runnable() {
            public void run() {
                onAnimationStart();
                CustomAnimationDrawable.super.start();

                mAnimationHandler = new Handler();
                mAnimationHandler.postDelayed(new Runnable() {

                    public void run() {
                        onAnimationFinish();
                    }
                }, getTotalDuration());
            }
        }, mDelayInMillis);
    }

    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            iDuration += this.getDuration(i);
        }

        return iDuration;
    }

    /* Called when the animation starts. */
    abstract public void onAnimationStart();

    /* Called when the animation finishes. */
    abstract public void onAnimationFinish();
}