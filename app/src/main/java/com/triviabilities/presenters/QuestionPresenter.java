package com.triviabilities.presenters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.triviabilities.GameMode;
import com.triviabilities.MyApplication;
import com.triviabilities.R;
import com.triviabilities.interfaces.AnswerResultListener;
import com.triviabilities.interfaces.AnswerVisibilityChangeListener;
import com.triviabilities.interfaces.GameStateListener;
import com.triviabilities.interfaces.TimerListener;
import com.triviabilities.models.Question;
import com.triviabilities.utils.ResizeAnimation;
import com.triviabilities.views.SingleTapGesture;

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
    private android.support.design.widget.AppBarLayout ToolbarContainer;

    private ImageView ExtendedToolbarReveal;

    private RevealFrameLayout QuestionContainerContainer;

    private TextView questionText;
    private float mMinTextSize;
    private float mMidTextSize;
    private float mMaxTextSize;
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

    /* Animation timing values */
    private final int answerResponseDisplayDuration = 1500;
    private final int correctAnswerShowDelay = 900;
    private final int answerContainerCollapseDuration = 500;
    private final int answerResponseTextCollapseDuration = 300;
    private final int answerButtonClickResponseDuration = 200;
    private final int toolbarShowHideDuration = 500;
    private final int toolbarIntroDuration = 200;
    private final int toolbarIntroRevealDuration = 500;

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




/************* Load UI Components ************/
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
        ToolbarContainer = (android.support.design.widget.AppBarLayout) mainActivity.findViewById(R.id.ToolbarFrame);

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

        mMinTextSize = mainActivity.getResources().getDimension(R.dimen.question_question_text_font_size_minimum);
        mMidTextSize = mainActivity.getResources().getDimension(R.dimen.question_question_text_font_size_medium);
        mMaxTextSize = mainActivity.getResources().getDimension(R.dimen.question_question_text_font_size_max);

        /* Bonus Round Instructions */
        bonusRoundTitleText = (TextView) mainActivity.findViewById(R.id.BonusRoundTitleText);
        instructionText = (TextView) mainActivity.findViewById(R.id.bonus_round_instructions);
        goButton = (FloatingActionButton) mainActivity.findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { gameStateListener.onBonusRoundInstructionsHide(); }
        });
    }
/************* End Loading UI Components *************/







/******* Intro Animation *******/
    private void prepViewsForIntroTransition()
    {
        QuestionCard.setVisibility(View.INVISIBLE);
        ToolbarContainer.setVisibility(View.INVISIBLE);
        ExtendedToolbarReveal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ExtendedToolbarReveal.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void presentIntroTransition(final GameMode gameMode)
    {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                1f, 0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(toolbarIntroDuration);
        anim.setStartOffset(0);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                questionNumberText.setText("1/" + Integer.toString(gameMode.getQuizLength()));
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
        anim.setDuration(toolbarIntroRevealDuration);
        anim.setStartOffset(0);
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
/************* End Intro Animation ***********/







    private void next()
    {
        if (timeExpired)
        {
            onBonusRoundResultsHide();
        }
        if (nextQuestionReady && nextQuestion != null) {
            presentQuestion(nextQuestion, bonusRound);
        } else if (bonusRoundTriggered) {
            onBonusRoundInstructionsLaunch();
        }
    }






/************* Question Presentation **************/

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
            QuestionCard.setX(MyApplication.screenWidth);
            QuestionCard.setVisibility(View.VISIBLE);
            anim = getCardSlideInRight(QuestionCard, null);
        }
        /* Case 2: Replace Bonus Round Instructions with new question*/
        else if (QuestionContainerContainer.getVisibility() == View.GONE)
        {
            anim = getReplaceBonusInstructionsWithQuestionAnim();
        }
        /* Case 3: Replace current question with new question */
        else
        {
            anim = getReplaceQuestionWithQuestionAnim();
        }

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            /* Card has now moved into place */
            public void onAnimationEnd(Animator animation) {
                if (!bonusRound)
                    presentQuestionNumber();

                revealQuestionText(newQuestion.getQuestionText(), new SupportAnimator.SimpleAnimatorListener() {
                    @Override
                    /* Question Text is now visible */
                    public void onAnimationEnd() {
                        int correctPos = 0;
                        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
                            if(newQuestion.isCorrectAnswer(answerPosition))
                                correctPos = answerPosition;
                        }

                        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
                            loadAnswerButton(answerButtons.get(answerPosition), answerPosition, newQuestion.getAnswerText(answerPosition), newQuestion.isCorrectAnswer(answerPosition), bonusRound, correctPos);
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

    public Animator getReplaceQuestionWithQuestionAnim() {
        Animator slideOutLeft = getCardSlideOutLeft(QuestionCard, null);
        Animator slideInRight = getCardSlideInRight(QuestionCard, null);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(slideOutLeft, slideInRight);
        return set;
    }

    public void presentBonusQuestion(Question newQuestion) {
        bonusRound = true;
        presentQuestion(newQuestion, true);
    }

    private void loadAnswerButton(final Button answerButton, final int pos, String answerText, final boolean isCorrectAnswer, final boolean bonusRound, final int correctPos) {
        answerButton.setVisibility(View.VISIBLE);
        answerButton.setText(answerText);

        if (timeExpired)
            return;

        answerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    disableAnswerButtons();

                    presentAnswerResponseAnimations(answerButton, pos, isCorrectAnswer, (int) motionEvent.getX(), (int) motionEvent.getY(), correctPos);

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
        questionText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(text));
        questionText.setText(text);
        questionText.setVisibility(View.VISIBLE);

        int centerX = (int) questionText.getX() + questionText.getWidth() / 2;
        int centerY = (int) questionText.getY() + questionText.getHeight()  / 2;

        if (questionText == null || !isViewAttached(questionText))
            return;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(questionText, centerX, centerY, 10, questionText.getWidth());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        animator.addListener(listener);
        animator.start();
    }

    private float getTextSize(String text)
    {
        if (text.length() < 100) {
            return mMaxTextSize;
        }
        else if (text.length() < 180) {
            return mMidTextSize;
        }
        else {
            return mMinTextSize;
        }
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
    public void presentAnswerResponseAnimations(final Button clickedButton, final int pos, final boolean isCorrectAnswer, int startX, int startY, final int correctPos) {
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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    setAnswerButtonBackgroundCorrectAfterWrong(answerButtons.get(correctPos), correctPos);
                    setSelectionStateAnswerButtonTextColor(answerButtons.get(correctPos), true);
                }
            }, correctAnswerShowDelay);

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
                    presentQuestionTeardown(isCorrectAnswer);
            }
        });
    }

    /* Highlights the clicked button */
    private void presentClickedButtonAnimation(Button clickedButton, int startX, int startY, SupportAnimator.SimpleAnimatorListener listener)
    {
        SupportAnimator clickedButtonAnimator = ViewAnimationUtils.createCircularReveal(clickedButton, startX, startY, 10, QuestionCard.getHeight());
        clickedButtonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        clickedButtonAnimator.setDuration(answerButtonClickResponseDuration);
        clickedButtonAnimator.addListener(listener);
        clickedButtonAnimator.start();
    }

    private void setSelectionStateAnswerButtonTextColor(Button button, boolean isSelected)
    {
        if (isSelected)
            button.setTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_answer_button_text_selected));
        else
            button.setTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_answer_button_text_faded));

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

    private void setAnswerButtonBackgroundCorrectAfterWrong(Button button, int pos)
    {
        if (pos == 0)
            button.setBackgroundResource(R.drawable.transition_to_green_answer_top);
        else if (pos == 1 || pos == 2)
            button.setBackgroundResource(R.drawable.transition_to_green_answer_middle);
        else
            button.setBackgroundResource(R.drawable.transition_to_green_answer_bottom);

        TransitionDrawable d = (TransitionDrawable) button.getBackground();
        d.startTransition(100);
    }


    public boolean isAnimationsRunning()
    {
        return answerResponseAnimationsInProgress;
    }
    public void stopAnimations()
    {
        presentQuestionTeardown(0, null);
        answerResponseAnimationsInProgress = false;
        hideAnswerResultText();
        restoreAnswerButtons();
        next();
    }
/************ End Answer Response Animations ************/







/************ Start Question Teardown ***************/

    public void presentQuestionTeardown(int delay, final SupportAnimator.SimpleAnimatorListener listener)
    {
        answerResponseAnimationsInProgress = true;
        disableAnswerButtons();
        presentAnswerButtonCollapseAnimation(delay, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                presentResponseTextCollapseAnimation(questionText, listener);
            }
        });
    }

    private void presentQuestionTeardown(final boolean isCorrectAnswer) {
        presentAnswerButtonCollapseAnimation(answerResponseDisplayDuration, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                restoreAnswerButtons();
                presentResponseTextCollapseAnimation(isCorrectAnswer ? woohooText : dohText, new SupportAnimator.SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd() {
                        hideAnswerResultText();
                        answerResponseAnimationsInProgress = false;
                        next();
                    }
                });
            }
        });
    }


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
                    collapseAnswers.setDuration(answerContainerCollapseDuration);
                    collapseAnswers.addListener(listener);
                    collapseAnswers.start();
                }
            }
        }, delayInMilliseconds);
    }


    private void presentResponseTextCollapseAnimation(View view, SupportAnimator.SimpleAnimatorListener listener)
    {
        AnswerContainer.setVisibility(View.INVISIBLE);
        int centerX = (int) AnswerContainer.getX() + (AnswerContainer.getWidth() / 2);
        int centerY = (int) AnswerContainer.getY() + AnswerContainer.getHeight() / 2;
        if (view != null && isViewAttached(view))
        {
            SupportAnimator collapseResponseAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0, questionText.getWidth()).reverse();
            collapseResponseAnimator.setDuration(answerResponseTextCollapseDuration);
            if (listener != null)
                collapseResponseAnimator.addListener(listener);
            collapseResponseAnimator.start();
        }
    }

    private void hideAnswerResultText() {
        woohooText.setVisibility(View.INVISIBLE);
        dohText.setVisibility(View.INVISIBLE);
    }

    public void restoreAnswerButtons()
    {
        for (int answerPosition = 0; answerPosition <= 3; answerPosition++) {
            if (answerPosition == 0)
                answerButtons.get(answerPosition).setBackgroundResource(R.drawable.selector_answer_top_button);
            else if (answerPosition == 1 || answerPosition == 2)
                answerButtons.get(answerPosition).setBackgroundResource(R.drawable.selector_answer_middle_button);
            else
                answerButtons.get(answerPosition).setBackgroundResource(R.drawable.selector_answer_bottom_button);

            answerButtons.get(answerPosition).setTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_answer_button_text_standard));
        }
    }

/************ End Question Teardown ************/







/**************** Bonus Round Instructions ************************/
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
        ObjectAnimator questionNumberTextAnim = getToolbarHideAnimator(questionNumberText, toolbarShowHideDuration, null);
        ObjectAnimator scoreTextAnim = getToolbarHideAnimator(scoreText, toolbarShowHideDuration, null);

        AnimatorSet toolbar = new AnimatorSet();
        toolbar.playTogether(questionNumberTextAnim, scoreTextAnim);
        toolbar.addListener(listener);
        toolbar.start();
    }

    private void presentBonusBackground(AnimatorListenerAdapter listener)
    {
        ObjectAnimator anim = ObjectAnimator.ofInt(QuestionContainer,
                "backgroundColor",
                ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_background_standard),
                ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_background_bonus));
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addListener(listener);
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
/**************** End Bonus Round Instructions ************************/





















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
            hideBonusRoundResults();
    }

    public void hideBonusRoundResults()
    {
        presentQuestionTeardown(2000, new SupportAnimator.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd() {
                answerResponseAnimationsInProgress = false;
                questionText.setVisibility(View.INVISIBLE);
                timeExpired = false;

                gameStateListener.onBonusRoundComplete();
            }
        });
    }

    public void onBonusRoundResultsHide()
    {
        timeExpired = false;
        answerResponseAnimationsInProgress = false;
        Animator anim = getCardSlideOutLeft(QuestionCard, null);
        anim.setStartDelay(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                QuestionCard.setX(MyApplication.screenWidth);
                QuestionCard.setVisibility(View.INVISIBLE);

                Animator hideBonusLabelAnim = getToolbarHideAnimator(questionNumberText, toolbarShowHideDuration, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        questionNumberText.setVisibility(View.INVISIBLE);
                    }
                });

                Animator hideTimerAnim = getToolbarHideAnimator(timerText, toolbarShowHideDuration, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        timerText.setVisibility(View.GONE);
                    }
                });
                Animator hideScoreAnim = getToolbarHideAnimator(scoreText, toolbarShowHideDuration, null);

                AnimatorSet hideBonusToolbar = new AnimatorSet();
                hideBonusToolbar.playTogether(hideBonusLabelAnim, hideTimerAnim, hideScoreAnim);
                hideBonusToolbar.setStartDelay(500);
                hideBonusToolbar.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gameStateListener.onBonusRoundHidden();
                    }
                });
                hideBonusToolbar.start();
            }
        });
        anim.start();

        presentStandardBackground();

        bonusRound = false;
    }

    private void presentStandardBackground()
    {
        ObjectAnimator anim = ObjectAnimator.ofInt(
                QuestionContainer,
                "backgroundColor",
                ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_background_bonus),
                ContextCompat.getColor(MyApplication.getAppContext(), R.color.trivia_background_standard));
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
/******* End Bonus Round Results Animations ***********/






    /* Utils */

    private boolean isViewAttached(View view)
{
    return view.isShown();
}

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
