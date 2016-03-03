package com.simpsonstrial2.interfaces;

public interface ScoreModelListener
{
    public void onScoreUpdated(int numPointsEarned, int newScore);
    public void onBonusScoreUpdated(int numPointsEarned, int newBonusScore);
    public void onMultiplierUpdated(int newMultiplier);
    public void onBonusNumCorrectShow(int numCorrect);
}
