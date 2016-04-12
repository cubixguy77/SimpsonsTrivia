package com.triviabilities.interfaces;

import com.triviabilities.models.HighScoreList;

public interface HighScoreRequestListener
{
    void onScoresRequested();
    void onScoresReturned(HighScoreList list);
}
