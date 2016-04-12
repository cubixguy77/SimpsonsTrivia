package com.triviabilities.interfaces;

public interface TimerListener
{
    public void onTimeExpired();
    public void onBonusRoundTimeExpired();
    public void onQuestionLive();
    public void onBonusQuestionLive();
}
