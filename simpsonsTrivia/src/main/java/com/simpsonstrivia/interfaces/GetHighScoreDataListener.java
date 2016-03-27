package com.simpsonstrivia.interfaces;

import com.simpsonstrivia.models.HighScoreList;

public interface GetHighScoreDataListener
{
    public void onRemoteCallComplete(HighScoreList list);
}
