package com.simpsonstrial2.interfaces;

import com.simpsonstrial2.models.HighScoreList;

public interface HighScoreRequestListener
{
    void onScoresRequested();
    void onScoresReturned(HighScoreList list);
}
