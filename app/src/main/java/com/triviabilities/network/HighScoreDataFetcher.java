package com.triviabilities.network;

import android.os.AsyncTask;

import com.triviabilities.interfaces.HighScoreRequestListener;
import com.triviabilities.GameMode;
import com.triviabilities.models.HighScoreItem;
import com.triviabilities.models.HighScoreList;
import com.triviabilities.models.HighScoreListRequest;
import com.triviabilities.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HighScoreDataFetcher extends AsyncTask<Boolean, Void, HighScoreList>
{
    private HighScoreRequestListener listener;
    private GameMode gameMode;

    public HighScoreDataFetcher(HighScoreRequestListener listener, GameMode gameMode)
    {
        this.listener = listener;
        this.gameMode = gameMode;
    }

    @Override
    protected  void onPreExecute()
    {
        listener.onScoresRequested();
    }

    protected HighScoreList doInBackground(Boolean... flags)
    {
        HighScoreListRequest request = new HighScoreListRequest(gameMode);

        if(request.isValidRequest())
        {
            String serverDataString = getServerDataString(request);
            return HighScoreDataFetcher.parseServerDataString(serverDataString);
        }

        return null;
    }

    public static String getServerDataString(HighScoreListRequest request)
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
                buffer.append(line + "\n");
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

    public static HighScoreList parseServerDataString(String serverDataString)
    {
        if (serverDataString == null || serverDataString.equals(""))
            return null;

        String[] tokens = serverDataString.split(",", -1);

        HighScoreList highScoreList = new HighScoreList();

        int i=0;
        while ((i+2) < tokens.length)
        {
            String rank = tokens[i];
            String name = tokens[i+1];
            String score = tokens[i+2];
            boolean isUser = name.equals(User.getInstance().getUserName());

            highScoreList.add(new HighScoreItem(rank, score, name, isUser));

            i += 3;
        }

        return highScoreList;
    }

    protected void onPostExecute(HighScoreList list)
    {
        if (listener != null)
            listener.onScoresReturned(list);

        listener = null;
    }
}
