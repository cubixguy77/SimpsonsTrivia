package com.triviabilities.network;

import android.os.AsyncTask;

import com.triviabilities.GameMode;
import com.triviabilities.models.HighScorePostRequest;
import com.triviabilities.interfaces.PostHighScoreDataListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HighScoreDataPoster extends AsyncTask<Boolean, Void, String>
{
    private PostHighScoreDataListener postHighScoreDataListener;
    private GameMode gameMode;
    private int score;

    public HighScoreDataPoster(PostHighScoreDataListener postHighScoreDataListener, GameMode gameMode, int score)
    {
        this.postHighScoreDataListener = postHighScoreDataListener;
        this.gameMode = gameMode;
        this.score = score;
    }

    protected String doInBackground(Boolean... bool)
    {
        HighScorePostRequest request = new HighScorePostRequest(gameMode, score);

        if(request.isValidRequest())
        {
            String serverDataString = getServerDataString(request);
            return serverDataString;
        }

        return null;
    }

    public static String getServerDataString(HighScorePostRequest request)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String serverDataString = null;
        try
        {
            URL url = request.getURL();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(request.getRequestMethod());
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) { return null; }

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }

            if (buffer.length() == 0) { return null; }

            serverDataString = buffer.toString();
        }
        catch (IOException e)
        {
            return null;
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (final IOException e)
                {
                }
            }

            return serverDataString;
        }
    }

    protected void onPostExecute(String dataString)
    {
        postHighScoreDataListener.onRemoteCallComplete(dataString);
        postHighScoreDataListener = null;
    }
}
