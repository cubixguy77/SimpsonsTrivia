package com.triviabilities.interfaces;

public interface AnswerResultListener
{
    void onCorrectAnswer();
    void onWrongAnswer();
    void onCorrectBonusAnswer();
    void onWrongBonusAnswer();
}
