package com.triviabilities;

import com.triviabilities.enums.Difficulty;
import com.triviabilities.enums.GamePlayType;

import java.util.ArrayList;

public class GameMode
{
    private static GameMode gameMode = null;

    public static GameMode getGameMode()
    {
        return gameMode;
    }

    static void destroyInstance()
    {
        gameMode = null;
    }

    public static GameMode newInstance(Difficulty difficulty, GamePlayType gamePlayType)
    {
        gameMode = new GameMode(difficulty, gamePlayType);
        return gameMode;
    }

    public static GameMode newInstance(int id)
    {
        switch (id)
        {
            case 0:
                gameMode = new GameMode(Difficulty.EASY, GamePlayType.CHALLENGE);
                break;
            case 1:
                gameMode = new GameMode(Difficulty.EASY, GamePlayType.SPEED);
                break;
            default:
                gameMode = new GameMode(Difficulty.EASY, GamePlayType.CHALLENGE);
                break;
        }

        return gameMode;
    }

    public static final boolean difficultyEnabled = false;
    private Difficulty difficulty;
    private GamePlayType gamePlayType;
    private boolean bonusRoundEnabled;
    private boolean timerEnabled;
    private int quizLength;
    private int numQuestionsAvailable;
    private int numQuotesAvailable;
    private String questionXmlFileName;
    private String quoteXmlFileName;
    private boolean debugMode = false;
    private String getHighScoreURL;
    private String putHighScoreURL;
    private String gameModeTitle;
    private int id;

    private GameMode(Difficulty difficulty, GamePlayType gamePlayType)
    {
        this.difficulty = difficulty;
        this.gamePlayType = gamePlayType;

        if (difficulty == Difficulty.EASY && gamePlayType == GamePlayType.CHALLENGE)
        {
            this.id = 0;
            this.quizLength = 10;
            this.timerEnabled = false;
            this.bonusRoundEnabled = true;
            this.numQuestionsAvailable = 171;
            this.numQuotesAvailable = 138;
            this.questionXmlFileName = "Questions-Android-DieHard.xml";
            this.quoteXmlFileName = "Quotes-Android-DieHard.xml";
            this.gameModeTitle = "Challenge";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/ShortChallenge.php" : "http://triviabilities.com/DieHard/ShortChallenge.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=ShortChallenge&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/DieHard/put_score.php?table=ShortChallenge&secret=dbsecret&name={{NAME}}&score={{SCORE}}";
        }
        else if (difficulty == Difficulty.EASY && gamePlayType == GamePlayType.SPEED)
        {
            this.id = 1;
            this.quizLength = 10;
            this.timerEnabled = true;
            this.bonusRoundEnabled = false;
            this.numQuestionsAvailable = 171;
            this.numQuotesAvailable = 0;
            this.questionXmlFileName = "Questions-Android-DieHard.xml";
            this.quoteXmlFileName = "Quotes-Android-DieHard.xml";
            this.gameModeTitle = "Speed";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/ShortSpeed.php" : "http://triviabilities.com/DieHard/ShortSpeed.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=ShortSpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/DieHard/put_score.php?table=ShortSpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}";
        }
    }

    public int getId()
    {
        return this.id;
    }

    public GamePlayType getGamePlayType()
    {
        return this.gamePlayType;
    }

    public boolean BonusRoundEnabled()
    {
        return this.bonusRoundEnabled;
    }

    public int getQuizLength()
    {
        return this.quizLength;
    }

    public String getQuestionXmlFileName()
    {
        return this.questionXmlFileName;
    }

    public String getQuoteXmlFileName()
    {
        return this.quoteXmlFileName;
    }

    public int GetNumQuestionsAvailable()
    {
        return this.numQuestionsAvailable;
    }

    public int GetNumQuotesAvailable()
    {
        return this.numQuotesAvailable;
    }

    public boolean TimerEnabled()
    {
        return this.timerEnabled;
    }

    public String getHighScoreURL() { return this.getHighScoreURL; }

    public String putHighScoreURL() { return this.putHighScoreURL; }

    public boolean getDebugMode() { return this.debugMode; }

    public String getGameModeTitle()
    {
        return this.gameModeTitle;
    }

    public static ArrayList<String> getGameModeArray()
    {
        ArrayList<String> list = new ArrayList<>(4);
        list.add("Easy - Challenge");
        list.add("Easy - Speed");
        return list;
    }
}
