package com.simpsonstrivia.interfaces;

public interface GameStateListener extends BonusRoundListener
{
    void onGameStart();
    void onReadyForNextQuestion();
}
