package com.simpsonstrial2.interfaces;

public interface AnswerResultListener
{
    void onCorrectAnswer();
    void onWrongAnswer();
    void onCorrectBonusAnswer();
    void onWrongBonusAnswer();
}
