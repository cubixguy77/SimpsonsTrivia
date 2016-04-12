package com.triviabilities.models;

import com.triviabilities.interfaces.AnswerVisibilityChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Question
{
    private String questionText;
    private ArrayList<Answer> answers;

    public Question(String questionText, String a1, String a2, String a3, String a4, String correct)
    {
        this.questionText = questionText;

        answers = new ArrayList<>();
        answers.add(0, new Answer(a1, correct.equals("A")));
        answers.add(1, new Answer(a2, correct.equals("B")));
        answers.add(2, new Answer(a3, correct.equals("C")));
        answers.add(3, new Answer(a4, correct.equals("D")));
        this.shuffleAnswers();
    }
    
    public String getQuestionText()
    {
        return this.questionText;
    }
    
    public String getAnswerText(int answerPosition)
    {
        return answers.get(answerPosition).getAnswerText();
    }

    public boolean isCorrectAnswer(int answerPosition)
    {
        return answers.get(answerPosition).isCorrectAnswer();
    }

    public boolean isAnswerVisible(int answerPosition)
    {
        return answers.get(answerPosition).isVisible();
    }

    public void shuffleAnswers()
    {
        Collections.shuffle(answers);
    }

    public void runFiftyFifty(AnswerVisibilityChangeListener listener)
    {
        this.setAnswerVisibility(false, getRandomVisibleAnswerPosition(), listener);
        this.setAnswerVisibility(false, getRandomVisibleAnswerPosition(), listener);
    }


    public int getRandomVisibleAnswerPosition()
    {
        Random randomGenerator = new Random();
        int randomAnswerPosition = randomGenerator.nextInt(answers.size());
        while (this.isAnswerVisible(randomAnswerPosition) == false)
        {
            randomAnswerPosition = randomGenerator.nextInt(answers.size());
        }

        return randomAnswerPosition;
    }

    public void setAnswerVisibility(boolean visible, int answerPosition, AnswerVisibilityChangeListener listener)
    {
        Answer answer = this.answers.get(answerPosition);
        if (answer.isVisible() && visible == false) // hide a visible button
        {
            this.answers.get(answerPosition).setAnswerVisibility(false);
            listener.onAnswerHide(answerPosition);
        }
        else if (answer.isVisible() == false && visible == true) // show a hidden button
        {
            this.answers.get(answerPosition).setAnswerVisibility(true);
            listener.onAnswerShow(answerPosition);
        }

    }
}