package com.triviabilities.interfaces;

import com.triviabilities.models.HighScoreList;

public interface GetHighScoreDataListener
{
    public void onRemoteCallComplete(HighScoreList list);
}
