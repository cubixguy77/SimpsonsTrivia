package com.simpsonstrial2.interfaces;

import com.simpsonstrial2.models.Question;
import com.simpsonstrial2.enums.AnswerResult;

public interface QuestionListener
{
    public void onQuestionNumChanged(int newQuestionNumber);
    public void onQuestionReturned(Question nextQuestion);
    public void onBonusQuestionReturned(Question nextQuestion);
    public void onGameOver();
    public void onQuestionHistoryUpdated(AnswerResult answerResult);
}
