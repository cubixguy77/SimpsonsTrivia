package com.simpsonstrial2.presenters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.R;
import com.simpsonstrial2.interfaces.AnswerResultListener;
import com.simpsonstrial2.interfaces.AnswerVisibilityChangeListener;
import com.simpsonstrial2.interfaces.GameStateListener;
import com.simpsonstrial2.interfaces.TimerListener;
import com.simpsonstrial2.models.Question;
import com.simpsonstrial2.utils.ResizeAnimation;
import com.simpsonstrial2.views.SingleTapGesture;

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class QuestionPresenter implements AnswerVisibilityChangeListener {
    private GameStateListener gameStateListener;
    private TimerListener timerListener;
    private ScorePresenter scorePresenter;
    private LinearLayout QuestionCard;
    private LinearLayout AnswerContainer;
    private RevealFrameLayout QuestionContainer;
    private FrameLayout ToolbarContainer;

    private ImageView ExtendedToolbarReveal;

    private RevealFrameLayout QuestionContainerContainer;

    private TextView questionText;
    private ArrayList<Button> answerButtons;

    private AnswerResultListener answerResultListener;

    private ImageView woohooText;
    private ImageView dohText;

    /* Toolbar */
    private TextView questionNumberText;
    private TextView scoreText;
    private TextView timerText;

    private int questionCardPositionLeft;
    private int cardWidth;
    private int toolbarHeight;

    private String nextQuestionNumberText;

    private boolean answerResponseAnimationsInProgress = false;
    private boolean nextQuestionReady = false;
    private Question nextQuestion;
    private boolean bonusRoundTriggered = false;
    private boolean bonusRound = false;
    private boolean timeExpired = false;

    private GestureDetector gestureDetector;

    /* Bonus Round Instructions */
    private LinearLayout BonusInstructionsCard;
    private RevealFrameLayout InstructionsContainer;
    private TextView bonusRoundTitleText;
    private TextView instructionText;
    private FloatingActionButton goButton;
    private TextView goText;

    /* Bonus Round Results Card */
    private RevealFrameLayout bonusRoundResultsContainer;
    private FrameLayout bonusRoundResultsCard;

    public QuestionPresenter(Activity mainActivity, AnswerResultListener answerResultListener, GameStateListener gameStateListener, TimerListener timerListener, ScorePresenter scorePresenter) {
        this.answerResultListener = answerResultListener;
        this.gameStateListener = gameStateListener;
        this.timerListener = timerListener;
        this.scorePresenter = scorePresenter;
        loadUiComponents(mainActivity);
    }

    public void removeListeners() {
        this.answerResultListener = null;
        this.gameStateListener = null;
        this.timerListener = null;
    }




/************* Loading ************/

    private void loadUiComponents(final Activity mainActivity) {
        QuestionCard = (LinearLayout) mainActivity.findViewById(R.id.QuestionCard);
        QuestionCard.setVisibility(View.INVISIBLE);
        QuestionCard.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            QuestionCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            QuestionCard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        int coordinate[] = new int[2];
                        QuestionCard.getLocationInWindow(coordinate);
                        if (coordinate[0] > 0) {
                            questionCardPositionLeft = coordinate[0];
                            cardWidth = QuestionCard.getWidth();
                            toolbarHeight = ToolbarContainer.getHeight();

                            prepViewsForIntroTransition();
                        }
                    }
                }
        );

        gestureDetector = new GestureDetector(mainActivity, new SingleTapGesture());

        BonusInstructionsCard = (LinearLayout) mainActivity.findViewById(R.id.BonusRoundInstructionsLayout);
        AnswerContainer = (LinearLayout) mainActivity.findViewById(R.id.AnswerContainer);
        QuestionContainer = (RevealFrameLayout) mainActivity.findViewById(R.id.QuestionContainer);
        InstructionsContainer = (RevealFrameLayout) mainActivity.findViewById(R.id.InstructionsContainer);
        QuestionContainerContainer = (RevealFrameLayout) mainActivity.findViewById(R.id.QuestionContainerContainer);
        ToolbarContainer = (FrameLayout) mainActivity.findViewById(R.id.ToolbarFrame);

        ExtendedToolbarReveal = (ImageView) mainActivity.findViewById(R.id.ExtendedToolbarReveal);

        /* Toolbar */
        questionNumberText = (TextView) mainActivity.findViewById(R.id.question_number);
        scoreText = (TextView) mainActivity.findViewById(R.id.score);
        timerText = (TextView) mainActivity.findViewById(R.id.TimerText);

        /* Question Card */
        questionText = (TextView) mainActivity.findViewById(R.id.QuestionText);
        woohooText = (ImageView) mainActivity.findViewById(R.id.woohoo);
        dohText = (ImageView) mainActivity.findViewById(R.id.doh);
        answerButtons = new ArrayList<>();
        answerButtons.add((Button) mainActivity.findViewById(R.id.AnswerA));
        answerButtons.add((Button) mainActivity.findViewById(R.id.AnswerB));
        answerButtons.add((Button) mainActivity.findViewById(R.id.AnswerC));
        answerButtons.add((Button) mainActivity.findViewById(R.id.AnswerD));

        /* Bonus Round Instructions */
        bonusRoundTitleText = (TextView) mainActivity.findViewById(R.id.BonusRoundTitleText);
        instructionText = (TextView) mainActivity.findViewById(R.id.bonus_round_instructions);
        goText = (TextView) mainActivity.findViewById(R.id.Go_Text);
        goButton = (FloatingActionButton) mainActivity.findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { gameStateListener.onBonusRoundInstructionsHide(); }
        });

        /* Bonus Round Results Card */
        bonusRoundResultsContainer = (RevealFrameLayout) mainActivity.findViewById(R.id.BonusResultsContainer);
        bonusRoundResultsCard = (FrameLayout) mainActivity.findViewById(R.id.BonusRoundResultsCard);
    }

    public Animator getReplaceQuestionWithQuestionAnim() {
        Animator slideOutLeft = getCardSlideOutLeft(QuestionCard, null);
        Animator slideInRight = getCardSlideInRight(QuestionCard, null);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(slideOutLeft, slideInRight);
        return set;
    }

    public Animator getReplaceBonusInstructionsWithQuestionAnim() {
        Animator slideOutLeft = getCardSlideOutLeft(BonusInstructionsCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                QuestionContainerContainer.setVisibility(View.VISIBLE);
                InstructionsContainer.setVisibility(View.GONE);
            }
        });
        Animator slideInRight = getCardSlideInRight(QuestionCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                QuestionCard.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(slideOutLeft, slideInRight);
        return animSet;
    }

/******** End Loading **********/


/******* Intro Animation *******/




    private void prepViewsForIntroTransition()
    {
        QuestionCard.setVisibility(View.INVISIBLE);
        ToolbarContainer.setVisibility(View.INVISIBLE);
        ExtendedToolbarReveal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ExtendedToolbarReveal.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void presentIntroTransition()
    {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                1f, 0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(500);
        anim.setStartOffset(50);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ExtendedToolbarReveal.setVisibility(View.GONE);
                revealToolbar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ExtendedToolbarReveal.startAnimation(anim);
    }

    private void revealToolbar()
    {
        ToolbarContainer.getLayoutParams().height = 0;
        ToolbarContainer.setVisibility(View.VISIBLE);
        ResizeAnimation anim = new ResizeAnimation(ToolbarContainer, toolbarHeight);
        anim.setDuration(500);
        anim.setStartOffset(100);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                scorePresenter.startupMultiplier();
                if (gameStateListener != null)
                    gameStateListener.onReadyForNextQuestion();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        ToolbarContainer.startAnimation(anim);
    }



/******* End Intro Animation ******/






/******* Question Presentation **************/

    public void SetQuestionNumber(int newQuestionNumber, int quizLength)
    {
        nextQuestionNumberText = Integer.toString(newQuestionNumber) + "/" + Integer.toString(quizLength);
    }

    public void presentQuestionNumber()
    {
        questionNumberText.setText(nextQuestionNumberText);

        if (questionNumberText.getVisibility() != View.VISIBLE)
        {
            ObjectAnimator anim = getToolbarRevealAnimator(questionNumberText, null);
            anim.start();
        }
    }

    public void presentQuestion(final Question newQuestion, final boolean bonusRound)
    {
        Log.d("present question", newQuestion.getQuestionText());
        Log.d("ansResponseAnim", answerResponseAnimationsInProgress + "");
        Log.d("nextQuestionReady", nextQuestionReady + "");
        Log.d("bonusRoundTriggered", bonusRoundTriggered + "");
        Log.d("bonusRound", bonusRound + "");
        Log.d("timeExpired", timeExpired + "");
        Log.d("question card", "visibility: " + QuestionCard.getVisibility());

        if (answerResponseAnimationsInProgress)
        {
            nextQuestionReady = true;
            nextQuestion = newQuestion;
            return;
        }

        nextQuestionReady = false;
        nextQuestion = null;

        Animator anim;
        questionText.setVisibility(View.INVISIBLE);
        AnswerContainer.setVisibility(View.INVISIBLE);

        /* Case 1: First question reveal */
        if (QuestionCard.getVisibility() == View.GONE || QuestionCard.getVisibility() == View.INVISIBLE)
        {
            Log.d("case 1", "First question reveal");
            QuestionCard.setX(MyApplication.screenWidth);
            QuestionCard.setVisibility(View.VISIBLE);
            anim = getCardSlideInRight(QuestionCard, null);
            Log.d("case 1", "First question reveal");
        }
        /* Case 2: Replace Bonus Round Instructions with new question*/
        else if (QuestionContainerContainer.getVisibility() == View.GONE)
        {
            anim = getReplaceBonusInstructionsWithQuestionAnim();
            Log.d("case 2", "Replace Bonus Round Instructions with new question");
        }
        /* Case 3: Replace current question with new question */
        else
        {
            anim = getReplaceQuestionWithQuestionAnim();
            Log.d("case 3", "Replace current question with new question");
        }

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("anim", "sliding in starting");
                System.out.println(QuestionCard.getVisibility() + " visible");
            }

            @Override
            /* Card has now moved into place */
            public void onAnimationEnd(Animator animation) {
                if (!bonusRound)
                    presentQuestionNumber();

                revealQuestionText(newQuestion.getQuestionText(), new SupportAnimator.SimpleAnimatorListener() {
                    @Override
                    /* Question Text is now visible */
                    public void onAnimationEnd() {
                        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
                            loadAnswerButton(answerButtons.get(answerPosition), answerPosition, newQuestion.getAnswerText(answerPosition), newQuestion.isCorrectAnswer(answerPosition), bonusRound);
                        }

                        revealAnswerButtons(new SupportAnimator.SimpleAnimatorListener() {
                            @Override
                            /* Answer Buttons are now visible */
                            public void onAnimationEnd() {
                                enableAnswerButtons(bonusRound);
                            }
                        });
                    }
                });
            }
        });

        /* Slide card in, reveal question text, reveal answer buttons */
        anim.start();
    }

    public void presentBonusQuestion(Question newQuestion) {
        bonusRound = true;
        presentQuestion(newQuestion, true);
    }

    private void loadAnswerButton(final Button answerButton, final int pos, String answerText, final boolean isCorrectAnswer, final boolean bonusRound) {
        answerButton.setVisibility(View.VISIBLE);
        answerButton.setText(answerText);

        if (timeExpired)
            return;

        answerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    disableAnswerButtons();

                    presentAnswerResponseAnimations(answerButton, pos, isCorrectAnswer, (int) motionEvent.getX(), (int) motionEvent.getY());

                    if (bonusRound) {
                        if (isCorrectAnswer)
                            answerResultListener.onCorrectBonusAnswer();
                        else
                            answerResultListener.onWrongBonusAnswer();
                    } else {
                        if (isCorrectAnswer)
                            answerResultListener.onCorrectAnswer();
                        else
                            answerResultListener.onWrongAnswer();
                    }
                }

                return false;
            }
        });
    }



    private void revealQuestionText(String text, SupportAnimator.AnimatorListener listener)
    {
        questionText.setText(text);
        questionText.setVisibility(View.VISIBLE);
        int centerX = (int) questionText.getX() + questionText.getWidth() / 2;
        int centerY = (int) questionText.getY() + questionText.getHeight()  / 2;
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(questionText, centerX, centerY, 10, questionText.getWidth());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        animator.addListener(listener);
        animator.start();
    }

    public void revealAnswerButtons(SupportAnimator.SimpleAnimatorListener listener)
    {

        AnswerContainer.setVisibility(View.VISIBLE);
        int centerX = (int) AnswerContainer.getX() + AnswerContainer.getWidth() / 2;
        int Y = (int) AnswerContainer.getY();
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(AnswerContainer, centerX, Y, 5, AnswerContainer.getHeight());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(listener);
        animator.start();
    }

/************ End Question Presentation ***************/



/************ Answer Response Animations ************************/

    public void presentAnswerResponseAnimations(final Button clickedButton, final int pos, final boolean isCorrectAnswer, int startX, int startY) {
        answerResponseAnimationsInProgress = true;

        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
            setSelectionStateAnswerButtonTextColor(answerButtons.get(answerPosition), false);
        }

        if (isCorrectAnswer) {
            setSelectedAnswerButtonBackground(clickedButton, pos, true);
            setSelectionStateAnswerButtonTextColor(clickedButton, true);
            questionText.setVisibility(View.INVISIBLE);
            woohooText.setVisibility(View.VISIBLE);
        } else {
            setSelectedAnswerButtonBackground(clickedButton, pos, false);
            setSelectionStateAnswerButtonTextColor(clickedButton, true);
            questionText.setVisibility(View.INVISIBLE);
            dohText.setVisibility(View.VISIBLE);
        }

        presentClickedButtonAnimation(clickedButton, startX, startY, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationStart() {
                answerResponseAnimationsInProgress = true;
            }

            @Override
            public void onAnimationEnd() {
                if (!bonusRoundTriggered)
                    presentQuestionTeardown(1500, clickedButton, pos, isCorrectAnswer);
            }
        });
    }

    public boolean isAnimationsRunning()
    {
        return answerResponseAnimationsInProgress;
    }

    private void presentQuestionTeardown(int delay, final SupportAnimator.SimpleAnimatorListener listener)
    {
        disableAnswerButtons();
        presentAnswerButtonCollapseAnimation(delay, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                presentResponseTextCollapseAnimation(questionText, listener);
            }
        });
    }

    private void presentQuestionTeardown(int delayInMilliseconds, final Button clickedButton, final int pos, final boolean isCorrectAnswer) {
        Log.d("teardown", "Present question teardown");
        presentAnswerButtonCollapseAnimation(delayInMilliseconds, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                Log.d("teardown down", "answer buttons torn down");
                restoreAnswerButtons(pos, clickedButton);
                presentResponseTextCollapseAnimation(isCorrectAnswer ? woohooText : dohText, new SupportAnimator.SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd() {
                        hideAnswerResultText();
                        answerResponseAnimationsInProgress = false;

                        // TODO: handle this logic elsewhere so the method can stand alone
                        if (timeExpired) {
                            presentBonusRoundResultsCard();
                        } else if (nextQuestionReady && nextQuestion != null) {
                            presentQuestion(nextQuestion, bonusRound);
                        } else if (bonusRoundTriggered) {
                            onBonusRoundInstructionsLaunch();
                        }
                    }
                });
            }
        });
    }

    /******* End Answer Response Animations ************/



    /************ Bonus Round Instructions ************************/
    public void onBonusRoundInstructionsLaunch() {
        if (answerResponseAnimationsInProgress) {
            bonusRoundTriggered = true;
            return;
        }

        bonusRoundTriggered = false;

        presentToolbarCollapse(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                presentBonusBackground(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        presentBonusInstructionCard();
                    }
                });
            }
        });
    }


    private void presentToolbarCollapse(AnimatorListenerAdapter listener)
    {
        ObjectAnimator questionNumberTextAnim = getToolbarHideAnimator(questionNumberText, 500, null);
        ObjectAnimator scoreTextAnim = getToolbarHideAnimator(scoreText, 500, null);

        AnimatorSet toolbar = new AnimatorSet();
        toolbar.playTogether(questionNumberTextAnim, scoreTextAnim);
        toolbar.addListener(listener);
        toolbar.start();
    }

    private void presentBonusBackground(AnimatorListenerAdapter listener)
    {
        ObjectAnimator anim = ObjectAnimator.ofInt(QuestionContainer,
                "backgroundColor",
                MyApplication.getAppContext().getResources().getColor(R.color.trivia_background_standard),
                MyApplication.getAppContext().getResources().getColor(R.color.trivia_background_bonus));
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addListener(listener);
        anim.start();
    }

    private void presentStandardBackground()
    {
        ObjectAnimator anim = ObjectAnimator.ofInt(QuestionContainer,
                "backgroundColor",
                MyApplication.getAppContext().getResources().getColor(R.color.trivia_background_bonus),
                MyApplication.getAppContext().getResources().getColor(R.color.trivia_background_standard));
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }

    private void showBonusRoundViews()
    {
        QuestionContainerContainer.setVisibility(View.GONE);
        InstructionsContainer.setVisibility(View.VISIBLE);
    }

    private void hideBonusRoundViews() {
        QuestionContainerContainer.setVisibility(View.VISIBLE);
        QuestionCard.setVisibility(View.INVISIBLE);
        InstructionsContainer.setVisibility(View.GONE);
    }

    private void presentBonusInstructionCard()
    {
        final Animator instructionCardSlideInAnim = getCardSlideInRight(BonusInstructionsCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bonusRoundTitleText.setVisibility(View.INVISIBLE);
                instructionText.setVisibility(View.INVISIBLE);
                timerText.setVisibility(View.INVISIBLE);
                timerText.setY(timerText.getY() + 200);
                goButton.setVisibility(View.INVISIBLE);
                goText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator titleTextAnim = getRevealAnimator(bonusRoundTitleText, 300, true, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final ObjectAnimator revealInstructions = getRevealAnimator(instructionText, 300, false, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                instructionText.setVisibility(View.VISIBLE);
                            }
                        });

                        final Animator revealTimer = getToolbarRevealAnimator(timerText, null);

                        final Animation fabReveal = AnimationUtils.loadAnimation(MyApplication.getAppContext(), R.anim.fab_show);
                        fabReveal.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                goButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ObjectAnimator revealGoText = getRevealAnimator(goText, 300, false, null);
                                revealGoText.start();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                        AnimatorSet revealInstructionsAndTimerSet = new AnimatorSet();
                        revealInstructionsAndTimerSet.playTogether(revealInstructions);//, revealTimer);
                        revealInstructionsAndTimerSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                goButton.startAnimation(fabReveal);
                            }
                        });
                        revealTimer.start();
                        revealInstructionsAndTimerSet.start();
                    }
                });
                titleTextAnim.start();
            }
        });

        Animator questionCardSlideOutAnim = getCardSlideOutLeft(QuestionCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showBonusRoundViews();
                BonusInstructionsCard.setX(MyApplication.screenWidth);
                instructionCardSlideInAnim.start();
            }
        });

        questionCardSlideOutAnim.start();
    }




    public void onBonusRoundInstructionsHide(AnimatorListenerAdapter listener)
    {
        Animator anim = getCardSlideOutLeft(BonusInstructionsCard, listener);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                QuestionCard.setX(MyApplication.screenWidth);
                hideBonusRoundViews();
            }
        });
        anim.start();

        ObjectAnimator revealBonusLabel = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_toolbar_text_reveal);
        revealBonusLabel.setTarget(questionNumberText);
        revealBonusLabel.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                questionNumberText.setVisibility(View.VISIBLE);
                questionNumberText.setText(MyApplication.getAppContext().getResources().getString(R.string.trivia_bonus_title_brief));
            }
        });

        ObjectAnimator revealBonusScore = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_toolbar_text_reveal);
        revealBonusScore.setTarget(scoreText);
        revealBonusScore.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                scoreText.setVisibility(View.VISIBLE);
                scoreText.setText("0");
            }
        });

        AnimatorSet toolbarElementsShowAnim = new AnimatorSet();
        toolbarElementsShowAnim.playTogether(revealBonusLabel, revealBonusScore);
        toolbarElementsShowAnim.start();
    }






    /* Highlights the clicked button */
    private void presentClickedButtonAnimation(Button clickedButton, int startX, int startY, SupportAnimator.SimpleAnimatorListener listener)
    {
        SupportAnimator clickedButtonAnimator = ViewAnimationUtils.createCircularReveal(clickedButton, startX, startY, 10, QuestionCard.getHeight());
        clickedButtonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        clickedButtonAnimator.setDuration(300);
        clickedButtonAnimator.addListener(listener);
        clickedButtonAnimator.start();
    }

    /* collapses the answer buttons */
    private void presentAnswerButtonCollapseAnimation(final int delayInMilliseconds, final SupportAnimator.SimpleAnimatorListener listener)
    {
        /* Start the collapse 1500 milliseconds after the answer result has been shown */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                int centerX = (int) AnswerContainer.getX() + (AnswerContainer.getWidth() / 2);
                int centerY = 0; //(int) AnswerContainer.getY() + AnswerContainer.getHeight()*2;
                if (isViewAttached(AnswerContainer))
                {
                    final SupportAnimator collapseAnswers = ViewAnimationUtils.createCircularReveal(AnswerContainer, centerX, centerY, 0, MyApplication.screenHeight).reverse();
                    collapseAnswers.setInterpolator(new AccelerateDecelerateInterpolator());
                    collapseAnswers.setDuration(500);
                    collapseAnswers.addListener(listener);
                    collapseAnswers.start();
                }
            }
        }, delayInMilliseconds);
    }

    private boolean isViewAttached(View view)
    {
        return view.isShown();
    }

    /* collapses the answer response text - woohoo/doh - */
    private void presentResponseTextCollapseAnimation(View view, SupportAnimator.SimpleAnimatorListener listener)
    {
        AnswerContainer.setVisibility(View.INVISIBLE);
        int centerX = (int) AnswerContainer.getX() + (AnswerContainer.getWidth() / 2);
        int centerY = (int) AnswerContainer.getY() + AnswerContainer.getHeight() / 2;
        if (isViewAttached(view))
        {
            SupportAnimator collapseResponseAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0, questionText.getWidth()).reverse();
            collapseResponseAnimator.setDuration(300);
            collapseResponseAnimator.addListener(listener);
            collapseResponseAnimator.start();
        }
    }



    private void setSelectionStateAnswerButtonTextColor(Button button, boolean isSelected)
    {
        if (isSelected)
            button.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.trivia_answer_button_text_selected));
        else
            button.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.trivia_answer_button_text_faded));

    }

    private void setSelectedAnswerButtonBackground(Button button, int pos, boolean isCorrectAnswer)
    {
        if (pos == 0)
            button.setBackgroundResource(isCorrectAnswer ? R.drawable.shape_button_answer_top_correct : R.drawable.shape_button_answer_top_wrong);
        else if (pos == 1 || pos == 2)
            button.setBackgroundResource(isCorrectAnswer ? R.drawable.shape_button_answer_middle_correct : R.drawable.shape_button_answer_middle_wrong);
        else
            button.setBackgroundResource(isCorrectAnswer ? R.drawable.shape_button_answer_bottom_correct : R.drawable.shape_button_answer_bottom_wrong);
    }

    private void hideAnswerResultText() {
        woohooText.setVisibility(View.INVISIBLE);
        dohText.setVisibility(View.INVISIBLE);
    }


    public void restoreAnswerButtons(int pos, Button clickedButton)
    {
        if (pos == 0)
            clickedButton.setBackgroundResource(R.drawable.selector_answer_top_button);
        else if (pos == 1 || pos == 2)
            clickedButton.setBackgroundResource(R.drawable.selector_answer_middle_button);
        else clickedButton.setBackgroundResource(R.drawable.selector_answer_bottom_button);

        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
            answerButtons.get(answerPosition).setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.trivia_answer_button_text_standard));
        }
    }



/******* Bonus Round Results Animations ***********/

    public void onShowBonusRoundResults()
    {
        disableAnswerButtons();
        timeExpired = true;

        Animator hideZeros = getToolbarHideAnimator(timerText, 300, null);
        Animator showTimesUp = getToolbarRevealAnimator(timerText, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                timerText.setText(MyApplication.getAppContext().getResources().getString(R.string.trivia_bonus_times_up));
            }
        });

        AnimatorSet timesUpReveal = new AnimatorSet();
        timesUpReveal.playSequentially(hideZeros, showTimesUp);
        timesUpReveal.start();

        if (answerResponseAnimationsInProgress)
            timeExpired = true;
        else
            presentQuestionTeardown(3000, new SupportAnimator.SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd() {
                    questionText.setVisibility(View.INVISIBLE);
                    presentBonusRoundResultsCard();
                }
            });
    }



    private void presentBonusRoundResultsCard()
    {
        timeExpired = false;

        Animator anim = getCardSlideOutLeft(QuestionCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bonusRoundResultsCard.setX(MyApplication.screenWidth);
                QuestionContainerContainer.setVisibility(View.GONE);
                bonusRoundResultsContainer.setVisibility(View.VISIBLE);
                bonusRoundResultsCard.setVisibility(View.VISIBLE);
                bonusRoundResultsSlideInRight();
            }
        });
        anim.setStartDelay(1000);
        anim.start();
    }

    private void bonusRoundResultsSlideInRight() {
        Animator anim = getCardSlideInRight(bonusRoundResultsCard, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                gameStateListener.onBonusRoundResultsVisible();
            }});

        anim.start();
    }

    public void onBonusRoundResultsHide(final AnimatorListenerAdapter listener)
    {
        Animator anim = getCardSlideOutLeft(bonusRoundResultsCard, null);
        anim.setStartDelay(6000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bonusRoundResultsContainer.setVisibility(View.GONE);
                QuestionCard.setX(MyApplication.screenWidth);
                QuestionCard.setVisibility(View.INVISIBLE);
                QuestionContainerContainer.setVisibility(View.VISIBLE);

                Animator hideBonusLabelAnim = getToolbarHideAnimator(questionNumberText, 500, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        questionNumberText.setVisibility(View.INVISIBLE);
                    }
                });

                Animator hideTimerAnim = getToolbarHideAnimator(timerText, 500, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        timerText.setVisibility(View.GONE);
                    }
                });
                Animator hideScoreAnim = getToolbarHideAnimator(scoreText, 500, null);

                AnimatorSet hideBonusToolbar = new AnimatorSet();
                hideBonusToolbar.playTogether(hideBonusLabelAnim, hideTimerAnim, hideScoreAnim);
                hideBonusToolbar.setStartDelay(500);
                hideBonusToolbar.addListener(listener);
                hideBonusToolbar.start();
            }
        });
        anim.start();

        presentStandardBackground();

        bonusRound = false;
    }


    /* Utils */

    private Animator getCardSlideOutLeft(View view, AnimatorListenerAdapter listener) {
        ObjectAnimator slide = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_card_slide_out_left);
        slide.setTarget(view);
        slide.setFloatValues((-1) * cardWidth);

        if (listener != null)
            slide.addListener(listener);
        return slide;
    }

    private Animator getCardSlideInRight(View view, AnimatorListenerAdapter listener) {
        ObjectAnimator slide = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_card_slide_in_right);
        slide.setTarget(view);
        slide.setFloatValues(MyApplication.screenWidth, questionCardPositionLeft);

        if (listener != null)
            slide.addListener(listener);
        return slide;
    }

    private ObjectAnimator getRevealAnimator(final View view, int duration, boolean fromTop, AnimatorListenerAdapter listener)
    {
        float endY = view.getY();
        float height = view.getHeight();
        final float startY = fromTop ? endY - height : endY + height;
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", startY, endY);
        anim.setDuration(duration);
        if (listener != null)
            anim.addListener(listener);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            view.setVisibility(View.VISIBLE);
            }
        });
        return anim;
    }

    private ObjectAnimator getHideAnimator(View view, int duration, boolean exitDown, AnimatorListenerAdapter listener)
    {
        float startY = view.getY();
        float height = view.getHeight();
        float endY = exitDown ? startY + height : startY - height;
        view.setY(startY);
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", startY, endY);
        anim.setDuration(duration);
        if (listener != null)
            anim.addListener(listener);
        return anim;
    }

    private ObjectAnimator getToolbarRevealAnimator(final View view, AnimatorListenerAdapter listener)
    {
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_toolbar_text_reveal);
        anim.setTarget(view);

        if (listener != null)
            anim.addListener(listener);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return anim;
    }

    private ObjectAnimator getToolbarHideAnimator(final View view, int duration, AnimatorListenerAdapter listener)
    {
        return getHideAnimator(view, duration, true, listener);
    }

    private void disableAnswerButtons()
    {
        for (Button answerButton : answerButtons)
        {
            if (answerButton != null)
                answerButton.setOnTouchListener(null);
        }
    }

    private void enableAnswerButtons(boolean bonusRound)
    {
        for (Button answerButton : answerButtons)
        {
            if (answerButton != null)
                answerButton.setClickable(true);
        }

        if (timerListener != null)
        {
            if (bonusRound)
                timerListener.onBonusQuestionLive();
            else
                timerListener.onQuestionLive();
        }
    }



    /* AnswerVisibilityChangeListener */
    public void onAnswerHide(int answerPosition)
    {
        answerButtons.get(answerPosition).setVisibility(View.GONE);
    }
    public void onAnswerShow(int answerPosition)
    {
        answerButtons.get(answerPosition).setVisibility(View.VISIBLE);
    }
    /* End AnswerVisibilityChangeListener */
}
