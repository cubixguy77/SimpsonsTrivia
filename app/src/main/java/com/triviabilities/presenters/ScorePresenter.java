package com.triviabilities.presenters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.triviabilities.MyApplication;
import com.triviabilities.R;
import com.triviabilities.interfaces.ScoreModelListener;
import com.triviabilities.views.DonutProgress;


public class ScorePresenter implements ScoreModelListener {
    private TextView scoreText;
    private DonutProgress donut;

    private int newScore;
    private int newBonusScore;
    private int numPointsEarned;
    private int oldMultiplier = 0;
    private int newMultiplier = 1;

    private boolean speedBonusEnabled;

    public ScorePresenter(Activity mainActivity, boolean speedBonusEnabled) {
        this.speedBonusEnabled = speedBonusEnabled;
        loadUiComponents(mainActivity);
    }

    public void removeListeners() {
    }

    private void loadUiComponents(Activity mainActivity) {
        scoreText = (TextView) mainActivity.findViewById(R.id.score);
        donut = (DonutProgress) mainActivity.findViewById(R.id.donut_progress);

        if (speedBonusEnabled)
        {
            donut.setVisibility(View.GONE);
        }
    }

    public void presentCorrectScoreAnimations(AnimatorListenerAdapter listener) {
        ObjectAnimator pointsEarnedAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_points_earned);
        pointsEarnedAnim.setTarget(scoreText);
        pointsEarnedAnim.addListener(listener);
        pointsEarnedAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                presentPointsEarned();
                if (!speedBonusEnabled)
                    presentMultiplierTransition();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                presentCurrentScore();
            }
        });
        pointsEarnedAnim.start();
    }

    public void startupMultiplier() {
        if (speedBonusEnabled)
            return;

        donut.startUp();
    }

    public void collapseMultiplier() {
        donut.collapse();
    }

    private void presentMultiplierTransition() {
        if (oldMultiplier == newMultiplier)
            return;

        if (donut.isGlowing() && newMultiplier > 1)
            return;

        donut.setProgress(newMultiplier);
    }


    private void presentBonusScoreResultAnimations(final int bonusPointsEarned, final int totalScore) {
        numPointsEarned = newBonusScore;

        ObjectAnimator pointsEarnedAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_points_earned);
        pointsEarnedAnim.setTarget(scoreText);
        pointsEarnedAnim.setStartDelay(1000L);
        pointsEarnedAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                numPointsEarned = bonusPointsEarned;
                newScore = totalScore;
                presentBonusPointsEarned();
            }

            @Override
            public void onAnimationEnd(Animator animation) {}
        });
        pointsEarnedAnim.start();
    }

    public void presentCorrectScoreBonusAnimations(AnimatorListenerAdapter listener)
    {
        ObjectAnimator pointsEarnedAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_points_earned);
        pointsEarnedAnim.setTarget(scoreText);
        pointsEarnedAnim.addListener(listener);
        pointsEarnedAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                presentBonusPointsEarned();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                presentCurrentBonusScore();
            }
        });
        pointsEarnedAnim.start();
    }

    private void presentPointsEarned() {
        scoreText.setVisibility(View.VISIBLE);
        scoreText.setText("+" + numPointsEarned);
        scoreText.setTextColor(Color.GREEN);
    }

    private void presentBonusPointsEarned() {
        scoreText.setVisibility(View.VISIBLE);
        scoreText.setText("+" + numPointsEarned);
        scoreText.setTextColor(Color.GREEN);
    }

    public void presentCurrentScore() {
        scoreText.setText(Integer.toString(newScore));
        scoreText.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.trivia_toolbar_text_standard));
        scoreText.setVisibility(View.VISIBLE);
        if (scoreText.getY() > 0)
        {
            ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_toolbar_text_reveal);
            anim.setTarget(scoreText);
            anim.start();
        }
    }

    private void presentCurrentBonusScore() {
        scoreText.setText(Integer.toString(newBonusScore));
        scoreText.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.trivia_toolbar_text_standard));
    }

    public void presentCurrentMultiplier() {
        if (!speedBonusEnabled)
            presentMultiplierTransition();
    }

    @Override
    public void onScoreUpdated(int numPointsEarned, int newScore)
    {
        this.newScore = newScore;
        this.numPointsEarned = numPointsEarned;
    }

    @Override
    public void onBonusScoreUpdated(int numPointsEarned, int newBonusScore)
    {
        this.newBonusScore = newBonusScore;
        this.numPointsEarned = numPointsEarned;
    }

    @Override
    public void onMultiplierUpdated(int newMultiplier) {
        this.oldMultiplier = this.newMultiplier;
        this.newMultiplier = newMultiplier;
    }

    public void onShowBonusRoundResults(int bonusPointsEarned, int totalScore)
    {
        presentBonusScoreResultAnimations(bonusPointsEarned, totalScore);
    }

    public void hideMultiplier()
    {
        donut.setVisibility(View.GONE);
    }

    public void showMultiplier() {
        donut.setVisibility(View.VISIBLE);
    }
}