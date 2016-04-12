package com.triviabilities.models;

import android.os.Bundle;

public class ScoreDataModel
{
    public int standardScore;
    public int numStandardCorrect;
    public int totalAnswers;

    public int bonusScore;
    public int numBonusCorrect;
    public int totalBonusAnswers;

    public ScoreDataModel() {}

    public ScoreDataModel(Bundle bundle)
    {
        this.setStandardScore(bundle.getInt("Score"));
        this.setNumStandardCorrect(bundle.getInt("CorrectAnswers"));
        this.setTotalAnswers(bundle.getInt("TotalAnswers"));

        this.setBonusScore(bundle.getInt("BonusScore"));
        this.setNumBonusCorrect(bundle.getInt("CorrectBonusAnswers"));
        this.setTotalBonusAnswers(bundle.getInt("TotalBonusAnswers"));
    }

    public Bundle getBundle()
    {
        Bundle bundle = new Bundle();

        bundle.putInt("Score", this.getStandardScore());
        bundle.putInt("CorrectAnswers", this.getNumStandardCorrect());
        bundle.putInt("TotalAnswers", this.getTotalAnswers());

        bundle.putInt("BonusScore", this.getBonusScore());
        bundle.putInt("CorrectBonusAnswers", this.getNumBonusCorrect());
        bundle.putInt("TotalBonusAnswers", this.getTotalBonusAnswers());

        return bundle;
    }

    public int getNumStandardCorrect() {
        return numStandardCorrect;
    }

    public void setNumStandardCorrect(int numStandardCorrect) {
        this.numStandardCorrect = numStandardCorrect;
    }

    public int getNumStandardIncorrect() {
        return totalAnswers - numStandardCorrect;
    }

    public int getStandardScore() {
        return standardScore;
    }

    public void setStandardScore(int standardScore) {
        this.standardScore = standardScore;
    }

    public int getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(int totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public int getNumBonusCorrect() {
        return numBonusCorrect;
    }

    public void setNumBonusCorrect(int numBonusCorrect) {
        this.numBonusCorrect = numBonusCorrect;
    }

    public int getNumBonusIncorrect() {
        return totalBonusAnswers - numBonusCorrect;
    }

    public int getBonusScore() {
        return bonusScore;
    }

    public void setBonusScore(int bonusScore) {
        this.bonusScore = bonusScore;
    }

    public int getTotalBonusAnswers() {
        return totalBonusAnswers;
    }

    public void setTotalBonusAnswers(int totalBonusAnswers) {
        this.totalBonusAnswers = totalBonusAnswers;
    }

    public int getFinalScore() {
        return standardScore + bonusScore;
    }

    public int getSpeedBonusDeficit() {
        return (totalAnswers * 30) - bonusScore;
    }
}
