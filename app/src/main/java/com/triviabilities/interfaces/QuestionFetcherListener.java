package com.triviabilities.interfaces;

import com.triviabilities.models.Question;

public interface QuestionFetcherListener
{
    public void onQuestionReturned(Question question);
    public void onBonusQuestionReturned(Question question);
}
