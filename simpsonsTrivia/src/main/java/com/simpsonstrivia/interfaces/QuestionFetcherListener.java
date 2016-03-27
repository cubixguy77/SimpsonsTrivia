package com.simpsonstrivia.interfaces;

import com.simpsonstrivia.models.Question;

public interface QuestionFetcherListener
{
    public void onQuestionReturned(Question question);
    public void onBonusQuestionReturned(Question question);
}
