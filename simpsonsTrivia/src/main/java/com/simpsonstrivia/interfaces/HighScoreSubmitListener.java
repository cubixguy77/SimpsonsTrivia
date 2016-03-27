package com.simpsonstrivia.interfaces;

import com.simpsonstrivia.enums.HighScoreSubmitResult;

public interface HighScoreSubmitListener
{
    void onScoreSubmitting();
    void onScoreSubmitResults(HighScoreSubmitResult result);
}
