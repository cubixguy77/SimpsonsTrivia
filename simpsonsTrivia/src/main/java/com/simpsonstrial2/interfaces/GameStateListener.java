package com.simpsonstrial2.interfaces;

public interface GameStateListener extends BonusRoundListener
{
    public void onGameStart();
    public void onReadyForNextQuestion();
}
