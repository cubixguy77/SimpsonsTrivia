package com.simpsonstrial2.interfaces;

public interface BonusRoundListener
{
    public void onBonusRoundTriggered();
    public void onBonusRoundInstructionsLaunch();
    public void onBonusRoundInstructionsHide();
    public void onBonusRoundStart();
    public void onBonusRoundNewQuestion();
    public void onBonusRoundTimeExpired();
    public void onBonusRoundResultsShow();
    public void onBonusRoundResultsVisible();
    public void onBonusRoundResultsHide();
    public void onBonusRoundHidden();
    public void onBonusRoundFinished();
}