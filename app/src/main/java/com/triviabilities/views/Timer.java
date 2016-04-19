package com.triviabilities.views;

import android.os.CountDownTimer;

import com.triviabilities.presenters.TimerPresenter;
import com.triviabilities.enums.TimerState;
import com.triviabilities.interfaces.AnswerResultListener;
import com.triviabilities.interfaces.TimerListener;

public class Timer extends CountDownTimer implements AnswerResultListener
{
    private TimerListener timerListener;
    private boolean bonusRound;
    private TimerPresenter timerPresenter;
    private TimerState timerState;

    public Timer(long millisInFuture, long countDownInterval, TimerListener timerListener, boolean bonusRound, TimerPresenter timerPresenter)
    {
        super(millisInFuture, countDownInterval);
        this.timerListener = timerListener;
        this.bonusRound = bonusRound;
        this.timerPresenter = timerPresenter;
        this.timerState = TimerState.LOADED;
    }

    public void removeListeners()
    {
        cancelTimer();
        this.timerListener = null;
        this.timerPresenter = null;
    }

    public void onRestartTimer()
    {
        timerPresenter.showTime();
        timerPresenter.updateTime(30);
        onStart();
    }

    public void reset()
    {
        this.timerState = TimerState.LOADED;
    }

    public void onStart()
    {
        this.timerState = TimerState.RUNNING;
        this.start();
    }

    @Override
    public void onFinish()
    {
        //timerPresenter.hideTime();

        cancelTimer();

        if (bonusRound)
        {
            if (timerListener != null)
                timerListener.onBonusRoundTimeExpired();
        }
        else
        {
            if (timerListener != null)
                timerListener.onTimeExpired();
        }
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        long seconds = millisUntilFinished / 1000;
        timerPresenter.updateTime(seconds);
    }

    private void cancelTimer()
    {
        this.timerState = TimerState.EXPIRED;
        this.cancel();
    }

    public boolean isLoaded() { return this.timerState == TimerState.LOADED; }

    public boolean isRunning()
    {
        return this.timerState == TimerState.RUNNING;
    }

    public boolean isTimeExpired()
    {
        return this.timerState == TimerState.EXPIRED;
    }

    @Override
    public void onCorrectAnswer()
    {
        this.cancelTimer();
    }

    @Override
    public void onWrongAnswer()
    {
        this.cancelTimer();
    }

    @Override
    public void onCorrectBonusAnswer()
    {
    }

    @Override
    public void onWrongBonusAnswer()
    {
    }
}



