package com.simpsonstrial2.interfaces;

public interface GameStateListener extends BonusRoundListener
{
    void onGameStart();
    void onReadyForNextQuestion();
}
