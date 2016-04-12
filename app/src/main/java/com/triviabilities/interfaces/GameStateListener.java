package com.triviabilities.interfaces;

public interface GameStateListener extends BonusRoundListener
{
    void onGameStart();
    void onReadyForNextQuestion();
}
