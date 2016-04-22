package com.triviabilities.models;

import com.triviabilities.GameMode;

import java.net.MalformedURLException;
import java.net.URL;

public class HighScoreListRequest
{
    private GameMode gameMode;

    public HighScoreListRequest(GameMode gameMode)
    {
        this.gameMode = gameMode;
    }

    public boolean isValidRequest()
    {
        return gameMode != null;
    }

    public String getRequestMethod()
    {
        return "GET";
    }

    public URL getURL()
    {
        String urlString = gameMode.getHighScoreURL();
        URL url;
        try
        {
            url = new URL(urlString);
        }
        catch (MalformedURLException e)
        {
            return null;
        }

        return url;
    }
}
