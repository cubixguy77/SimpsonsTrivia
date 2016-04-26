package com.triviabilities.models;

import com.triviabilities.BuildConfig;
import com.triviabilities.interfaces.ScoreModelListener;

public class ScoreModel
{
    private ScoreDataModel scoreDataModel;

    private int maxMultiplier = 5;

    private int multiplier;
    private int speedBonus;

    private int currentBonusScore;

    private ScoreModelListener scoreModelListener;
    private boolean speedBonusEnabled;
    private boolean bonusRoundEnabled;

    public ScoreModel(ScoreModelListener scoreModelListener, boolean speedBonusEnabled, boolean bonusRoundEnabled)
    {
        this.scoreModelListener = scoreModelListener;
        this.speedBonusEnabled = speedBonusEnabled;
        this.bonusRoundEnabled = bonusRoundEnabled;
        this.multiplier = 1;
        this.speedBonus = 0;
        this.scoreDataModel = new ScoreDataModel();
        this.maxMultiplier = BuildConfig.DEBUG ? 2 : 5;
    }

    public ScoreDataModel getScoreDataModel()
    {
        return this.scoreDataModel;
    }

    public void removeListeners()
    {
        this.scoreModelListener = null;
    }

    public void setMultiplier(int newMultiplierValue)
    {
        this.multiplier = newMultiplierValue;
        scoreModelListener.onMultiplierUpdated(newMultiplierValue);
    }

    public void setScore(int questionValue, int speedBonus)
    {
        int numPointsEarned = questionValue + speedBonus;
        scoreDataModel.standardScore += questionValue;
        scoreDataModel.bonusScore += speedBonus;
        this.scoreModelListener.onScoreUpdated(numPointsEarned, scoreDataModel.getFinalScore());
        this.SetSpeedBonus(0);
    }

    public void setBonusScore(int questionValue)
    {
        scoreDataModel.bonusScore += questionValue;
        currentBonusScore += questionValue;
        this.scoreModelListener.onBonusScoreUpdated(questionValue, currentBonusScore);
    }

    public int getCurrentBonusScore()
    {
        return currentBonusScore;
    }

    public int getCurrentGameScore()
    {
        return scoreDataModel.getFinalScore();
    }

    public int getPointValue()
    {
        return 100 * this.multiplier;
    }

    public void SetSpeedBonus(int speedBonus)
    {
        this.speedBonus = speedBonus;
    }

    public void onCorrectAnswer()
    {
        scoreDataModel.totalAnswers++;
        scoreDataModel.numStandardCorrect++;

        this.setScore(this.getPointValue(), this.speedBonus);

        if (bonusRoundEnabled == false)
            return;

        if (multiplier < maxMultiplier)
        {
            if (!speedBonusEnabled)
                this.setMultiplier(multiplier+1);
        }
    }

    public boolean isBonusRoundTriggered()
    {
        return multiplier >= maxMultiplier && bonusRoundEnabled;
    }

    public void onWrongAnswer()
    {
        scoreDataModel.totalAnswers++;
        if (!speedBonusEnabled)
            this.setMultiplier(1);
    }

    public void onCorrectBonusAnswer()
    {
        scoreDataModel.totalBonusAnswers++;
        scoreDataModel.numBonusCorrect++;

        this.setBonusScore(this.getPointValue());
    }

    public void onWrongBonusAnswer()
    {
        scoreDataModel.totalBonusAnswers++;
    }


    public void onBonusRoundStart() {
        currentBonusScore = 0;
    }

    public void onBonusRoundHidden() {
        setMultiplier(1);
    }
}
