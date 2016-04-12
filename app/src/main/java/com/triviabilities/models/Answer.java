package com.triviabilities.models;

public class Answer
{
    private boolean correct;
    private String answerText;
    private boolean visible;

    public Answer(String answerText, boolean correct)
    {
        this.answerText = answerText;
        this.correct = correct;
    }

    public boolean isCorrectAnswer()
    {
        return this.correct;
    }

    public String getAnswerText()
    {
        return this.answerText;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setAnswerVisibility(boolean visible)
    {
        this.visible = visible;
    }
}
