package com.triviabilities.presenters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.triviabilities.R;

public class TimerPresenter
{
    TextView timerText;

    public TimerPresenter(Activity mainActivity)
    {
        timerText = (TextView) mainActivity.findViewById(R.id.TimerText);
    }

    public void removeListeners()
    {
    }

    public void updateTime(long seconds)
    {
        if (seconds < 10)
            timerText.setText(":0" + seconds);
        else
            timerText.setText(":" + seconds);
    }

    public void reset()
    {
        timerText.setText(":30");
    }

    public int getIntTime()
    {
        return Integer.parseInt( timerText.getText().toString().substring(1) );
    }

    public void showTime()
    {
        timerText.setVisibility(View.VISIBLE);
    }

    public void hideTime()
    {
        timerText.setVisibility(View.GONE);
    }
}
