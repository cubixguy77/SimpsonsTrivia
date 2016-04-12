package com.triviabilities.presenters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.triviabilities.MyApplication;
import com.triviabilities.R;
import com.triviabilities.interfaces.ScoreModelListener;
import com.triviabilities.views.CustomAnimationDrawable;


public class ScorePresenter implements ScoreModelListener {
    private TextView scoreText;
    private ImageView multiplier;
    private TextView multiplierText;

    CustomAnimationDrawable multStartup;
    CustomAnimationDrawable multOneToTwo;
    CustomAnimationDrawable multTwoToThree;
    CustomAnimationDrawable multThreeToFour;
    CustomAnimationDrawable multFourToFive;
    CustomAnimationDrawable multGlowing;
    CustomAnimationDrawable multCollapse;
     CustomAnimationDrawable multTwoToOne;
     CustomAnimationDrawable multThreeToOne;
     CustomAnimationDrawable multFourToOne;
     CustomAnimationDrawable multFiveToOne;

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

    public void loadUiComponents(Activity mainActivity) {
        scoreText = (TextView) mainActivity.findViewById(R.id.score);
        multiplier = (ImageView) mainActivity.findViewById(R.id.multiplier);
        multiplierText = (TextView) mainActivity.findViewById(R.id.multiplierText);
        multiplierText.setAlpha(0);
        if (speedBonusEnabled)
        {
            multiplier.setVisibility(View.GONE);
            multiplierText.setVisibility(View.GONE);
        }

        if (speedBonusEnabled)
            return;

        Resources res = mainActivity.getResources();

        final ObjectAnimator fadeInMultText = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_multiplier_text_fade_in);
        fadeInMultText.setTarget(multiplierText);

        multGlowing = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_glowing), false)
        {
            @Override
            public void onAnimationStart() {}
            @Override
            public void onAnimationFinish() {}
        }; multGlowing.setOneShot(false);

        multStartup = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_startup), false)
        {
            @Override
            public void onAnimationStart() {
                multiplier.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationFinish() {
                multiplierText.setText("1x");
                multiplierText.setAlpha(0f);
                multiplierText.setVisibility(View.VISIBLE);

                fadeInMultText.start();
            }
        };

        multOneToTwo = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_one_to_two), false)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 1.0f, 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }

            @Override
            public void onAnimationFinish() {
                multiplierText.setText("2x");
                fadeInMultText.start();
            }
        };

        multTwoToThree = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_two_to_three), false)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 1.0f, 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }

            @Override
            public void onAnimationFinish() {
                multiplierText.setText("3x");
                fadeInMultText.start();
            }
        };

        multThreeToFour = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_three_to_four), false)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 1.0f, 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }

            @Override
            public void onAnimationFinish() {
                multiplierText.setText("4x");
                fadeInMultText.start();
            }
        };

        multFourToFive = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_four_to_five), false)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 1.0f, 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }

            @Override
            public void onAnimationFinish() {
                multiplierText.setText("5x");

                ObjectAnimator fadeInMultText = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.trivia_multiplier_text_fade_in);
                fadeInMultText.setTarget(multiplierText);
                fadeInMultText.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        play(multGlowing);
                    }
                });
                fadeInMultText.start();
            }
        };

        multCollapse = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_collapse), false)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 1.0f, 0f);
                hideMultTextAnim.setDuration(300);
                hideMultTextAnim.start();
            }
            @Override
            public void onAnimationFinish() {
                multiplier.setVisibility(View.INVISIBLE);
                multiplierText.setVisibility(View.INVISIBLE);
            }
        };

        multTwoToOne = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_one_to_two), true)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }
            @Override
            public void onAnimationFinish() {
                multiplierText.setText("1x");
                fadeInMultText.start();
            }
        };

        multThreeToOne = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_two_to_three), true)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }
            @Override
            public void onAnimationFinish() {
                play(multTwoToOne);
            }
        };

        multFourToOne = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_three_to_four), true)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }
            @Override
            public void onAnimationFinish() {
                play(multThreeToOne);
            }
        };

        multFiveToOne = new CustomAnimationDrawable((AnimationDrawable) ContextCompat.getDrawable(mainActivity, R.drawable.animation_mult_four_to_five), true)
        {
            @Override
            public void onAnimationStart() {
                ObjectAnimator hideMultTextAnim = ObjectAnimator.ofFloat(multiplierText, "alpha", 0f);
                hideMultTextAnim.setDuration(this.getTotalDuration());
                hideMultTextAnim.start();
            }
            @Override
            public void onAnimationFinish() {
                play(multFourToOne);
            }
        };
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
                    presentMultiplierTransition(200);
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

        play(multStartup);
    }

    public void collapseMultiplier() {
        play(multCollapse);
    }

    private CustomAnimationDrawable getBackToOneAnim(int m)
    {
        switch (m)
        {
            case 5:
                return multFiveToOne;
            case 4:
                return multFourToOne;
            case 3:
                return multThreeToOne;
            case 2:
                return multTwoToOne;
            default:
                return null;
        }
    }


    public void presentMultiplierTransition(int delayInMillis) {
        if (oldMultiplier == newMultiplier)
            return;

        if (isMultiplierGlowing() && newMultiplier > 1)
            return;

        final CustomAnimationDrawable anim;

        if (oldMultiplier == 0 && newMultiplier == 1)
            anim = multStartup;
        else if (oldMultiplier == 1 && newMultiplier == 2)
            anim = multOneToTwo;
        else if (oldMultiplier == 2 && newMultiplier == 3)
            anim = multTwoToThree;
        else if (oldMultiplier == 3 && newMultiplier == 4)
            anim = multThreeToFour;
        else if (oldMultiplier == 4 && newMultiplier == 5)
            anim = multFourToFive;
        else if (oldMultiplier > 1 && newMultiplier == 1)
            anim = getBackToOneAnim(oldMultiplier);
        else
            return;

        anim.setStartDelay(delayInMillis);
        play(anim);
    }


    public void presentBonusScoreResultAnimations(final int bonusPointsEarned, final int totalScore) {
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
            presentMultiplierTransition(0);
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
        multiplier.setVisibility(View.GONE);
    }

    private boolean isMultAnimInProgress() {
        return getMultAnim().isRunning();
    }
    private AnimationDrawable getMultAnim() {
        return (AnimationDrawable) multiplier.getBackground();
    }
    private boolean isMultiplierGlowing()
    {
        return getMultAnim() != null && getMultAnim() == multGlowing && isMultAnimInProgress();
    }
    private void setMultiplierBackground(CustomAnimationDrawable anim)
    {
        resetMultiplierAnimation();
        multiplier.setBackgroundDrawable(anim);
    }
    private void resetMultiplierAnimation()
    {
        AnimationDrawable currentAnim = getMultAnim();
        if (currentAnim != null)
        {
            currentAnim.stop();
            currentAnim.setVisible(false, true);
            multiplier.setBackgroundDrawable(null);
        }
    }
    private void startMultiplierAnimation(CustomAnimationDrawable anim)
    {
        if(isMultAnimInProgress())
        {
            anim.stop();
        }

        anim.start();
    }

    public void play(CustomAnimationDrawable anim)
    {
        setMultiplierBackground(anim);
        startMultiplierAnimation(anim);
    }
}
