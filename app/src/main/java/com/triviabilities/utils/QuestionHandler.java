package com.triviabilities.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.triviabilities.enums.AnswerResult;
import com.triviabilities.interfaces.AnswerResultListener;
import com.triviabilities.interfaces.AnswerVisibilityChangeListener;
import com.triviabilities.interfaces.QuestionFetcherListener;
import com.triviabilities.interfaces.QuestionListener;
import com.triviabilities.GameMode;
import com.triviabilities.models.Question;
import com.triviabilities.models.QuestionHistory;
import com.triviabilities.network.QuestionFetcher;

import java.util.ArrayList;
import java.util.Random;

public class QuestionHandler implements AnswerResultListener, QuestionFetcherListener
{
    private GameMode gameMode;
    private int currentQuestionNum;
    private int currentBonusQuestionNum = 0;
    private QuestionListener questionListener;
    private Context context;
    private int quizLength;
    private int numSurplusQuestions = 0;
    private int numBonusSurplusQuestions = 0;


    private ArrayList<QuestionHistory> questionHistory;
    private ArrayList<QuestionHistory> bonusQuestionHistory;

    private boolean immediateQuestionRequested = false;
    private boolean immediateBonusQuestionRequested = false;

    private QuestionFetcher questionFetcher;
    private QuestionFetcher bonusQuestionFetcher;

    public QuestionHandler(GameMode gameMode, int currentQuestionNum, QuestionListener questionListener, Context context)
    {
        this.gameMode = gameMode;
        this.currentQuestionNum = currentQuestionNum;
        this.questionListener = questionListener;
        this.context = context;

        quizLength = gameMode.getQuizLength();

        questionHistory = new ArrayList<>(quizLength);
        questionHistory.add(0, new QuestionHistory(null, AnswerResult.UNKNOWN)); // The 0th question does not exist, so the array index will correspond with current question number

        bonusQuestionHistory = new ArrayList<>(5);
        bonusQuestionHistory.add(0, new QuestionHistory(null, AnswerResult.UNKNOWN));
    }

    public QuestionFetcher GetQuestionFetcher(int[] questionIDs)
    {
        return new QuestionFetcher(this.gameMode.getQuestionXmlFileName(), this.context, this, false, questionIDs);
    }

    public QuestionFetcher GetBonusQuestionFetcher(int[] questionIDs)
    {
        return new QuestionFetcher(gameMode.getQuoteXmlFileName(), this.context, this, true, questionIDs);
    }

    public void onFiftyFifty(AnswerVisibilityChangeListener listener)
    {
        this.GetCurrentQuestion().runFiftyFifty(listener);
    }

    public void removeListeners()
    {
        this.questionListener = null;
        if (questionFetcher != null) {
            questionFetcher.cancel(true);
            questionFetcher = null;
        }
        if (bonusQuestionFetcher != null) {
            bonusQuestionFetcher.cancel(true);
            bonusQuestionFetcher = null;
        }
    }




    public int[] getQuestionIDs(int numQuestions, boolean bonus)
    {
        int[] questionIDs = new int[numQuestions];
        for (int i=0; i<questionIDs.length; i++)
        {
            if (bonus)
            {
                questionIDs[i] = getRandomQuestion(gameMode.GetNumQuotesAvailable(), questionIDs);
            }
            else
            {
                questionIDs[i] = getRandomQuestion(gameMode.GetNumQuestionsAvailable(), questionIDs);
            }
        }
        return questionIDs;
    }

    public void loadQuestions()
    {
        numSurplusQuestions = 0;
        int[] questionIDs = getQuestionIDs(quizLength, false);
        questionFetcher = GetQuestionFetcher(questionIDs);
        questionFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void loadBonusQuestions()
    {
        currentBonusQuestionNum = 0;
        numBonusSurplusQuestions = 0;
        bonusQuestionHistory.clear();
        bonusQuestionHistory.add(0, new QuestionHistory(null, AnswerResult.UNKNOWN));

        int[] usedBonusQuoteIDs = getQuestionIDs(10, true);
        bonusQuestionFetcher = GetBonusQuestionFetcher(usedBonusQuoteIDs);
        bonusQuestionFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }





    private int getRandomQuestion(int numQuestionsAvailable, int[] usedQuestionIDs)
    {
        Random randomGenerator = new Random();
        int randomQuestionNumber = randomGenerator.nextInt(numQuestionsAvailable);
        while (questionDuplicate(randomQuestionNumber, usedQuestionIDs))
        {
            randomQuestionNumber = randomGenerator.nextInt(numQuestionsAvailable);
        }

        return randomQuestionNumber;
    }


    private boolean questionDuplicate(int questionIndex, int[] usedQuestionIDs)
    {
        for (int i=0; i<usedQuestionIDs.length; i++)
        {
            if (usedQuestionIDs[i] == questionIndex)
                return true;
        }
        return false;
    }



    /* QuestionFetcherListener */
    @Override
    public void onQuestionReturned(Question nextQuestion)
    {
        questionHistory.add(new QuestionHistory(nextQuestion, AnswerResult.UNKNOWN));
        numSurplusQuestions++;
        if (immediateQuestionRequested)
        {
            returnQuestionToClient(nextQuestion);
        }

        if (numSurplusQuestions > 1 && questionFetcher != null && questionFetcher.isPaused() == false)
            questionFetcher.pause();
    }

    @Override
    public void onBonusQuestionReturned(Question nextQuestion)
    {
        bonusQuestionHistory.add(new QuestionHistory(nextQuestion, AnswerResult.UNKNOWN));
        numBonusSurplusQuestions++;
        if (immediateBonusQuestionRequested)
        {
            returnBonusQuestionToClient(nextQuestion);
        }

        if (numBonusSurplusQuestions > 1 && bonusQuestionFetcher.isPaused() == false)
            bonusQuestionFetcher.pause();
    }
    /* End QuestionFetcherListener */



    private void returnQuestionToClient(Question nextQuestion)
    {
        if (questionListener != null)
        {
            questionListener.onQuestionReturned(nextQuestion);
            questionListener.onQuestionNumChanged(currentQuestionNum);
            numSurplusQuestions--;
            immediateQuestionRequested = false;

            if (numSurplusQuestions < 1 && questionFetcher.isPaused())
                questionFetcher.resume();
        }
    }

    private void returnBonusQuestionToClient(Question nextQuestion)
    {
        questionListener.onBonusQuestionReturned(nextQuestion);
        numBonusSurplusQuestions--;
        immediateBonusQuestionRequested = false;

        if (numBonusSurplusQuestions < 1 && bonusQuestionFetcher.isPaused())
            bonusQuestionFetcher.resume();
    }




    /* AnswerResultListener */
    public void onCorrectAnswer()
    {
        questionHistory.get(currentQuestionNum).SetAnswerResult(AnswerResult.CORRECT);
        questionListener.onQuestionHistoryUpdated(AnswerResult.CORRECT);
    }

    public void onWrongAnswer()
    {
        questionHistory.get(currentQuestionNum).SetAnswerResult(AnswerResult.WRONG);
        questionListener.onQuestionHistoryUpdated(AnswerResult.WRONG);
    }

    public void onCorrectBonusAnswer()
    {
        bonusQuestionHistory.get(currentBonusQuestionNum).SetAnswerResult(AnswerResult.WRONG);
    }

    public void onWrongBonusAnswer()
    {
        bonusQuestionHistory.get(currentBonusQuestionNum).SetAnswerResult(AnswerResult.CORRECT);
    }
    /* End AnswerResultListener */


    public boolean isGameOver() {
        return currentQuestionNum >= gameMode.getQuizLength();
    }

    public void GetNextQuestion()
    {
        if (isGameOver())
        {
            questionListener.onGameOver();
            return;
        }

        currentQuestionNum++;
        Question nextQuestion = GetCurrentQuestion();
        if (nextQuestion == null)
        {
            immediateQuestionRequested = true;
        }
        else
        {
            returnQuestionToClient(nextQuestion);
        }
    }

    public void GetNextBonusQuestion()
    {
        currentBonusQuestionNum++;
        Question nextQuestion = GetCurrentBonusQuestion();
        if (nextQuestion == null)
        {
            immediateBonusQuestionRequested = true;
        }
        else
        {
            returnBonusQuestionToClient(nextQuestion);
        }
    }







/*
    private int GetNextQuestionIndex()
    {
        Random randomGenerator = new Random();
        int randomQuestionNumber = randomGenerator.nextInt(gameMode.GetNumQuestionsAvailable());
        while (questionAlreadyUsed(randomQuestionNumber))
        {
            randomQuestionNumber = randomGenerator.nextInt(gameMode.GetNumQuestionsAvailable());
        }

        return randomQuestionNumber;
    }
*/

/*
    private boolean questionAlreadyUsed(int questionIndex)
    {
        for (QuestionHistory question : questionHistory)
        {
            if (question.GetQuestionIndex() == questionIndex)
                return true;
        }
        return false;
    }
*/
    private Question GetCurrentQuestion()
    {
        if (currentQuestionNum >= questionHistory.size())
            return null;

        return questionHistory.get(this.currentQuestionNum).GetQuestion();
    }

    private Question GetCurrentBonusQuestion()
    {
        if (currentBonusQuestionNum >= bonusQuestionHistory.size())
            return null;

        return bonusQuestionHistory.get(this.currentBonusQuestionNum).GetQuestion();
    }

}
