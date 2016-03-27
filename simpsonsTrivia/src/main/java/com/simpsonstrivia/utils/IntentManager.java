package com.simpsonstrivia.utils;

import android.content.Context;
import android.content.Intent;

import com.simpsonstrivia.activities.HighScoreActivity;
import com.simpsonstrivia.activities.MainMenuActivity;
import com.simpsonstrivia.activities.QuestionActivity;
import com.simpsonstrivia.activities.ResultsActivity;
import com.simpsonstrivia.models.GameMode;
import com.simpsonstrivia.models.ScoreDataModel;


public class IntentManager {

    public static Intent getMainMenuIntent(Context context)
    {
        GameMode.destroyInstance();
        final Intent mainMenuIntent = new Intent(context, MainMenuActivity.class);
        mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return mainMenuIntent;
    }

    public static Intent getQuestionIntent(Context context)
    {
        Intent questionIntent = new Intent(context, QuestionActivity.class);
        questionIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return questionIntent;
    }

    public static Intent getResultsIntent(Context context, ScoreDataModel scoreModel)
    {
        Intent resultsIntent = new Intent(context, ResultsActivity.class);
        resultsIntent.putExtra("ScoreModelBundle", scoreModel.getBundle());
        return resultsIntent;
    }

    public static Intent getHighScoresIntent(Context context)
    {
        Intent highScoresIntent = new Intent(context, HighScoreActivity.class);
        return highScoresIntent;
    }
}
