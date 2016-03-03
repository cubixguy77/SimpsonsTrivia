package com.simpsonstrial2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.enums.HighScoreSubmitResult;
import com.simpsonstrial2.interfaces.HighScoreRequestListener;
import com.simpsonstrial2.interfaces.HighScoreSubmitListener;
import com.simpsonstrial2.interfaces.PostHighScoreDataListener;
import com.simpsonstrial2.models.GameMode;
import com.simpsonstrial2.models.HighScoreItem;
import com.simpsonstrial2.models.HighScoreList;
import com.simpsonstrial2.network.HighScoreDataFetcher;
import com.simpsonstrial2.network.HighScoreDataPoster;

import java.util.HashMap;

public class HighScoreUtils {

    private static HashMap<Integer, HighScoreList> cachedList;

    /* Tests whether the given score is a high score */
    public static void submitScore(final int score, final HighScoreSubmitListener listener)
    {
        System.out.println("Submitting score: " + score);

        if (cachedList == null)
            cachedList = new HashMap<>();

        if (score <= 0)
        {
            listener.onScoreSubmitResults(HighScoreSubmitResult.NOT_HIGH_SCORE);
            return;
        }

        final HighScoreDataFetcher dataFetcher = new HighScoreDataFetcher(new HighScoreRequestListener() {
            @Override
            public void onScoresRequested() {}

            @Override
            public void onScoresReturned(HighScoreList list) {
                putCache(list);

                HighScoreItem userPreviousHighScore = list.getUserHighScore();
                if ((list.getNumHighScores() < 50 || score > list.getLowestHighScore().getIntScore()) &&
                    (userPreviousHighScore == null || score > userPreviousHighScore.getIntScore()))
                {
                    listener.onScoreSubmitResults(HighScoreSubmitResult.HIGH_SCORE);
                }
            }
        }, GameMode.getGameMode());

        dataFetcher.execute();
    }



    public static void postScore(final int score, final HighScoreRequestListener listener)
    {
        System.out.println("Posting score: " + score);

        if (cachedList == null)
            cachedList = new HashMap<>();

        HighScoreDataPoster highScoreDataPoster = new HighScoreDataPoster(new PostHighScoreDataListener() {
            @Override
            public void onRemoteCallComplete(String highScoreResponse)
            {
                if (highScoreResponse.equals("High Score"))
                {
                    refreshCache(listener);
                    /*
                    HighScoreList list = cachedList.get(GameMode.getGameMode().getId());
                    list.insertScore(score);
                    cachedList.put(GameMode.getGameMode().getId(), list);
                    */
                }
            }
        }, GameMode.getGameMode(), score);

        highScoreDataPoster.execute();
    }

    private static void refreshCache(final HighScoreRequestListener listener)
    {
        HighScoreDataFetcher dataFetcher = new HighScoreDataFetcher(new HighScoreRequestListener() {
            @Override
            public void onScoresRequested() {}
            @Override
            public void onScoresReturned(HighScoreList list) {
                System.out.println("Scores refreshed");
                putCache(list);
                if (listener != null)
                    listener.onScoresReturned(list);
            }
        }, GameMode.getGameMode());

        dataFetcher.execute();
    }

    private static void putCache(HighScoreList list)
    {
        cachedList.put(GameMode.getGameMode().getId(), list);
    }



    public static void requestScores(GameMode gameMode, final HighScoreRequestListener listener)
    {
        if (cachedList == null)
            cachedList = new HashMap<>();

        final int gameKey = GameMode.getGameMode().getId();

        /* Return cached copy */
        if (cachedList.get(gameKey) != null)
        {
            listener.onScoresReturned(cachedList.get(gameKey));
            return;
        }

        HighScoreDataFetcher dataFetcher = new HighScoreDataFetcher(new HighScoreRequestListener() {
            @Override
            public void onScoresRequested() {
                listener.onScoresRequested();
            }
            @Override
            public void onScoresReturned(HighScoreList list) {
                cachedList.put(gameKey, list);
                listener.onScoresReturned(list);
            }
        }, gameMode);
        dataFetcher.execute();
    }




    public static boolean isNetworkAvailable() {
        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    HaveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    HaveConnectedMobile = true;
        }
        return HaveConnectedWifi || HaveConnectedMobile;
    }
}
