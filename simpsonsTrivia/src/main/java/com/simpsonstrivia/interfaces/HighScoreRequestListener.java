package com.simpsonstrivia.interfaces;

import com.simpsonstrivia.models.HighScoreList;

public interface HighScoreRequestListener
{
    void onScoresRequested();
    void onScoresReturned(HighScoreList list);
}
