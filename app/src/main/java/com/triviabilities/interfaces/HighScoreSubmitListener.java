package com.triviabilities.interfaces;

import com.triviabilities.enums.HighScoreSubmitResult;

public interface HighScoreSubmitListener
{
    void onScoreSubmitting();
    void onScoreSubmitResults(HighScoreSubmitResult result);
}
