package com.triviabilities.models;

import com.triviabilities.GameMode;

import java.net.MalformedURLException;
import java.net.URL;

public class HighScorePostRequest
{
    private GameMode gameMode;
    private int score;

    public HighScorePostRequest(GameMode gameMode, int score)
    {
        this.gameMode = gameMode;
        this.score = score;
    }

    public boolean isValidRequest()
    {
        return gameMode != null && score > 0 && User.getInstance().isRegisteredUser();
    }

    // Yes, we're posting using GET. Why that is, I can't say, some people just like it better that way.
    public String getRequestMethod()
    {
        return "GET";
    }

    public URL getURL()
    {
        String urlString = gameMode.putHighScoreURL();
        urlString = urlString.replaceFirst("\\{\\{NAME\\}\\}", User.getInstance().getUserNameURI());
        urlString = urlString.replaceFirst("\\{\\{SCORE\\}\\}", Integer.toString(score));

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
