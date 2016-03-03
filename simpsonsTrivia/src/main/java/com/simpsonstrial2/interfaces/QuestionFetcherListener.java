package com.simpsonstrial2.interfaces;

import com.simpsonstrial2.models.Question;

public interface QuestionFetcherListener
{
    public void onQuestionReturned(Question question);
    public void onBonusQuestionReturned(Question question);
}
