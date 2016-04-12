package com.triviabilities.interfaces;

public interface BonusRoundListener
{
    void onBonusRoundTriggered();
    void onBonusRoundInstructionsLaunch();
    void onBonusRoundInstructionsHide();
    void onBonusRoundStart();
    void onBonusRoundNewQuestion();
    void onBonusRoundTimeExpired();
    void onBonusRoundResultsShow();
    void onBonusRoundComplete();
    void onBonusRoundHidden();
    void onBonusRoundFinished();
}