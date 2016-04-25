package com.triviabilities.views;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.triviabilities.MyApplication;
import com.triviabilities.R;

public class CircularTransition extends ImageView {

    public CircularTransition(Context context, View root, float x, float y) {
        super(context);

        this.setImageResource(R.drawable.circle);
        if (root instanceof LinearLayout)
            this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        else
            this.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        this.setVisibility(View.INVISIBLE);

        if (root instanceof LinearLayout)
            ((LinearLayout) root).addView(this);
        else if (root instanceof RelativeLayout)
            ((RelativeLayout) root).addView(this);
        else
            ((FrameLayout) root).addView(this);

        this.setX(x);
        this.setY(y);
    }

    public void start(AnimatorListenerAdapter listener) {

        AnimatorSet set = new AnimatorSet();
        float screenHeight = (float) MyApplication.screenHeight * 2 + 200;
        set.play(ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, screenHeight)).with(ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, screenHeight));
        set.setDuration(1000);
        if (listener != null)
            set.addListener(listener);

        this.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            this.setElevation(50f);
        }

        set.start();
    }
}
