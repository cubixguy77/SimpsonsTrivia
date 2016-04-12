package com.triviabilities.models;

import com.triviabilities.enums.AnswerResult;

public class QuestionHistory
{
    private Question question;
    //private int questionIndex;
    private AnswerResult answerResult;

    public QuestionHistory(Question question, AnswerResult answerResult)
    {
        this.question = question;
       // this.questionIndex = questionIndex;
        this.answerResult = answerResult;
    }

    public Question GetQuestion()
    {
        return this.question;
    }

    public void SetQuestion(Question question)
    {
        this.question = question;
    }

    //public int GetQuestionIndex()
    //{
    //    return this.questionIndex;
    //}

    //public void SetQuestionIndex(int questionIndex)
    //{
    //    this.questionIndex = questionIndex;
    //}

    public AnswerResult GetAnswerResult()
    {
        return this.answerResult;
    }

    public void SetAnswerResult(AnswerResult answerResult)
    {
        this.answerResult = answerResult;
    }
}
