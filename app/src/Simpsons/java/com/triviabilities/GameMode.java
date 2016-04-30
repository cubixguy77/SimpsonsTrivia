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

    public static void destroyInstance()
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
                gameMode = new GameMode(Difficulty.HARD, GamePlayType.CHALLENGE);
                break;
            case 2:
                gameMode = new GameMode(Difficulty.EASY, GamePlayType.SPEED);
                break;
            case 3:
                gameMode = new GameMode(Difficulty.HARD, GamePlayType.SPEED);
                break;
            default:
                gameMode = new GameMode(Difficulty.EASY, GamePlayType.CHALLENGE);
                break;
        }

        return gameMode;
    }

    public static final boolean difficultyEnabled = true;
    private Difficulty difficulty;
    private GamePlayType gamePlayType;
    private boolean bonusRoundEnabled;
    private boolean timerEnabled;
    private int quizLength;
    private int numQuestionsAvailable;
    private int numQuotesAvailable;
    private String questionXmlFileName;
    private String quoteXmlFileName;
    private boolean fiftyFiftyEnabled;
    private boolean debugMode = BuildConfig.DEBUG;
    private String getHighScoreURL;
    private String putHighScoreURL;
    private String gameModeTitle;
    private int id;

    private GameMode(Difficulty difficulty, GamePlayType gamePlayType)
    {
        this.difficulty = difficulty;
        this.gamePlayType = gamePlayType;
        this.fiftyFiftyEnabled = false;

        if (difficulty == Difficulty.EASY && gamePlayType == GamePlayType.CHALLENGE)
        {
            this.id = 0;
            this.quizLength = debugMode? 2 : 15;
            this.timerEnabled = false;
            this.bonusRoundEnabled = true;
            this.numQuestionsAvailable = 1251;
            this.numQuotesAvailable = 703;
            this.questionXmlFileName = "Easy.xml";
            this.quoteXmlFileName = "QuotesEasy.xml";
            this.gameModeTitle = "Challenge - Easy";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/ShortChallenge.php" : "http://triviabilities.com/Simpsons/EasyShortGame.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=ShortChallenge&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/Simpsons/put_score.php?table=EasyShortGame&secret=dbsecret&name={{NAME}}&score={{SCORE}}";


        }
        else if (difficulty == Difficulty.EASY && gamePlayType == GamePlayType.SPEED)
        {
            this.id = 2;
            this.quizLength = debugMode? 2 : 10;
            this.timerEnabled = true;
            this.bonusRoundEnabled = false;
            this.numQuestionsAvailable = 1251;
            this.numQuotesAvailable = 703;
            this.questionXmlFileName = "Easy.xml";
            this.quoteXmlFileName = "QuotesEasy.xml";
            this.gameModeTitle = "Speed - Easy";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/ShortSpeed.php" : "http://triviabilities.com/Simpsons/EasySpeed.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=ShortSpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/Simpsons/put_score.php?table=EasySpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}";
        }
        else if (difficulty == Difficulty.HARD && gamePlayType == GamePlayType.CHALLENGE)
        {
            this.id = 1;
            this.quizLength = debugMode? 2 : 15;
            this.timerEnabled = false;
            this.bonusRoundEnabled = true;
            this.numQuestionsAvailable = 1419;
            this.numQuotesAvailable = 566;
            this.questionXmlFileName = "Hard.xml";
            this.quoteXmlFileName = "QuotesHard.xml";
            this.gameModeTitle = "Challenge - Hard";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/LongChallenge.php" : "http://triviabilities.com/Simpsons/HardShortGame.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=LongChallenge&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/Simpsons/put_score.php?table=HardShortGame&secret=dbsecret&name={{NAME}}&score={{SCORE}}";
        }
        else if (difficulty == Difficulty.HARD && gamePlayType == GamePlayType.SPEED)
        {
            this.id = 3;
            this.quizLength = debugMode? 2 : 10;
            this.timerEnabled = true;
            this.bonusRoundEnabled = false;
            this.numQuestionsAvailable = 1419;
            this.numQuotesAvailable = 566;
            this.questionXmlFileName = "Hard.xml";
            this.quoteXmlFileName = "QuotesHard.xml";
            this.gameModeTitle = "Speed - Hard";
            this.getHighScoreURL = debugMode ? "http://triviabilities.com/Test/LongSpeed.php" : "http://triviabilities.com/Simpsons/HardSpeed.php";
            this.putHighScoreURL = debugMode ? "http://triviabilities.com/Test/put_score.php?table=LongSpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}" : "http://triviabilities.com/Simpsons/put_score.php?table=HardSpeed&secret=dbsecret&name={{NAME}}&score={{SCORE}}";
        }
    }

    public Difficulty getDifficulty()
    {
        return this.difficulty;
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

    public boolean getFiftyFiftyEnabled() { return this.fiftyFiftyEnabled; }

    public boolean isDebugMode() { return this.debugMode; }

    public String getGameModeTitle()
    {
        return this.gameModeTitle;
    }

    public static ArrayList<String> getGameModeArray()
    {
        ArrayList<String> list = new ArrayList<>(4);
        list.add("Easy - Challenge");
        list.add("Hard - Challenge");
        list.add("Easy - Speed");
        list.add("Hard - Speed");
        return list;
    }
}
