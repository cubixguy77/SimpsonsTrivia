package com.simpsonstrial2.utils;

import android.content.Context;
import android.content.Intent;

import com.simpsonstrial2.activities.HighScoreActivity;
import com.simpsonstrial2.activities.MainMenuActivity;
import com.simpsonstrial2.activities.QuestionActivity;
import com.simpsonstrial2.activities.ResultsActivity;
import com.simpsonstrial2.models.GameMode;
import com.simpsonstrial2.models.ScoreDataModel;


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
