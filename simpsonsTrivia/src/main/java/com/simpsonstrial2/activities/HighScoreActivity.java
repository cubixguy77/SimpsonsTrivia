package com.simpsonstrial2.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.R;
import com.simpsonstrial2.utils.IntentManager;
import com.simpsonstrial2.views.CircularTransition;
import com.simpsonstrial2.views.SingleTapGesture;

public class HighScoreActivity extends AppCompatActivity
{
    private GestureDetector gestureDetector;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_high_score);
        setupToolBar();
        setupButtons();
    }

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        getSupportActionBar().setTitle("Leaderboard");
    }

    private void setupButtons() {
        gestureDetector = new GestureDetector(this, new SingleTapGesture());

        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        playAgainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    float touchX = motionEvent.getRawX();
                    float touchY = motionEvent.getRawY() - MyApplication.screenHeight;

                    goPlayAgain(touchX, touchY);
                }

                return false;
            }
        });
    }

    private void goHome()
    {
        startActivity(IntentManager.getMainMenuIntent(this));
    }

    private void goPlayAgain(float touchX, float touchY)
    {
        CircularTransition transition = new CircularTransition(this, findViewById(R.id.RootLayout), touchX, touchY);
        transition.start(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(IntentManager.getQuestionIntent(HighScoreActivity.this));
                finish();
            }
        });
    }
}
