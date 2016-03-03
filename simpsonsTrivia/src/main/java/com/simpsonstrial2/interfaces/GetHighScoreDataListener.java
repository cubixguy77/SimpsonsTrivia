package com.simpsonstrial2.interfaces;

import com.simpsonstrial2.models.HighScoreList;

public interface GetHighScoreDataListener
{
    public void onRemoteCallComplete(HighScoreList list);
}
