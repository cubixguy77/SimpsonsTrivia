package com.simpsonstrial2.interfaces;

import com.simpsonstrial2.enums.HighScoreSubmitResult;

public interface HighScoreSubmitListener
{
    void onScoreSubmitting();
    void onScoreSubmitResults(HighScoreSubmitResult result);
}
