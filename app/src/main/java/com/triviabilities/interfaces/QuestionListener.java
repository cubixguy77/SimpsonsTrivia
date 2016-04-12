package com.triviabilities.interfaces;

import com.triviabilities.models.Question;
import com.triviabilities.enums.AnswerResult;

public interface QuestionListener
{
    public void onQuestionNumChanged(int newQuestionNumber);
    public void onQuestionReturned(Question nextQuestion);
    public void onBonusQuestionReturned(Question nextQuestion);
    public void onGameOver();
    public void onQuestionHistoryUpdated(AnswerResult answerResult);
}
