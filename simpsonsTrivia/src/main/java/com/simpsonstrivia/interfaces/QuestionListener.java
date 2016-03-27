package com.simpsonstrivia.interfaces;

import com.simpsonstrivia.models.Question;
import com.simpsonstrivia.enums.AnswerResult;

public interface QuestionListener
{
    public void onQuestionNumChanged(int newQuestionNumber);
    public void onQuestionReturned(Question nextQuestion);
    public void onBonusQuestionReturned(Question nextQuestion);
    public void onGameOver();
    public void onQuestionHistoryUpdated(AnswerResult answerResult);
}
