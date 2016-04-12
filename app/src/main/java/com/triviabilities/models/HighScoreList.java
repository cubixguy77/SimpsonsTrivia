package com.triviabilities.models;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class HighScoreList
{
    private ArrayList<HighScoreItem> highScoreList;

    public HighScoreList(ArrayList<HighScoreItem> highScoreList)
    {
        this.highScoreList = highScoreList;
    }

    public HighScoreList(Intent intent)
    {
        this((ArrayList<HighScoreItem>) intent.getSerializableExtra("HighScoreList"));
    }

    public HighScoreList()
    {
        this.highScoreList = new ArrayList<>();
    }

    public void add(HighScoreItem item)
    {
        this.highScoreList.add(item);
    }

    public void insertScore(int score)
    {
        if (highScoreList == null)
            return;

        for (int i=0; i<highScoreList.size(); i++)
        {

            if (score > highScoreList.get(i).getIntScore())
            {
                for (int j=i; j<highScoreList.size(); j++)
                {
                    highScoreList.get(j).setRank(Integer.toString(Integer.parseInt(highScoreList.get(j).getRank()) + 1));
                }

                highScoreList.add(i, new HighScoreItem(Integer.toString(i+1), Integer.toString(score), User.getInstance().getUserName(), true));
                return;
            }
        }

        /* adding score to the last slot */
        this.add(new HighScoreItem(Integer.toString(highScoreList.size()+1), Integer.toString(score), User.getInstance().getUserName(), true));
    }

    public HighScoreItem getUserHighScore()
    {
        for (HighScoreItem item : highScoreList)
        {
            if (item.getIsUser())
                return item;
        }

        return null;
    }

    public HighScoreItem getLowestHighScore()
    {
        return highScoreList.get(highScoreList.size() - 1);
    }

    public List<HighScoreItem> getHighScoreList()
    {
        return this.highScoreList;
    }

    public int getNumHighScores()
    {
        return this.highScoreList.size();
    }
}
