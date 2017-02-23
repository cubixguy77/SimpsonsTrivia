package com.triviabilities.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.triviabilities.MyApplication;
import com.triviabilities.R;
import com.triviabilities.enums.GamePlayType;
import com.triviabilities.enums.HighScoreSubmitResult;
import com.triviabilities.interfaces.HighScoreSubmitListener;
import com.triviabilities.models.DataResult;
import com.triviabilities.GameMode;
import com.triviabilities.models.ScoreDataModel;
import com.triviabilities.models.User;
import com.triviabilities.utils.HighScoreUtils;

import java.util.ArrayList;


public class ResultsTab extends Fragment implements HighScoreSubmitListener
{
    private int finalScoreValue;
    private LinearLayout rootLayout;

    private final int animGraphDuration = 800;
    private final int animStartDelay = 600;
    private final int animRawCircleOffset = 500;
    private final int animBonusCircleOffset = animRawCircleOffset + 100;

    private final int animTextRevealDuration = 400;
    private final int animTextRevealStartDelay = animStartDelay + animBonusCircleOffset + animGraphDuration + 200;

    private boolean loadingAnimationsInProgress = true;
    private boolean showUserNameDialogOnAnimationsComplete = false;

    public static ResultsTab newInstance(Bundle scoreModelBundle) {
        ResultsTab myFragment = new ResultsTab();

        Bundle args = new Bundle();
        args.putBundle("ScoreModelBundle", scoreModelBundle);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tab_results, container, false);

        final PieChart finalChart = (PieChart) view.findViewById(R.id.finalScoreChart);
        final PieChart rawChart = (PieChart) view.findViewById(R.id.rawChart);
        final PieChart bonusChart = (PieChart) view.findViewById(R.id.bonusChart);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_final_score_layout);

        final RelativeLayout finalCircle = (RelativeLayout) view.findViewById(R.id.FinalChartContainer);
        final FrameLayout rawCircle = (FrameLayout) view.findViewById(R.id.RawScoreChartContainer);
        final FrameLayout bonusCircle = (FrameLayout) view.findViewById(R.id.BonusScoreChartContainer);

        final TextView finalScore = (TextView) view.findViewById(R.id.finalScore);
        final TextView rawScore = (TextView) view.findViewById(R.id.rawScore);
        final TextView bonusScore = (TextView) view.findViewById(R.id.bonusScore);

        final TextView finalScoreText = (TextView) view.findViewById(R.id.finalScoreText);
        final TextView rawScoreText = (TextView) view.findViewById(R.id.rawScoreText);
        final TextView bonusScoreText = (TextView) view.findViewById(R.id.bonusScoreText);

        final TextView rawScoreNumCorrect = (TextView) view.findViewById(R.id.rawScoreNumCorrect);
        final TextView bonusScoreNumCorrect = (TextView) view.findViewById(R.id.bonusScoreNumCorrect);

        setChartStyles(finalChart, true, "Final Results");
        setChartStyles(rawChart, false, "Raw Score");
        setChartStyles(bonusChart, false, "Bonus");

        ScoreDataModel scoreModel = new ScoreDataModel(getArguments().getBundle("ScoreModelBundle"));
        GameMode gameMode = GameMode.getGameMode();
        this.finalScoreValue = scoreModel.getFinalScore();

        submitScore();

        setData(finalChart, scoreModel.getStandardScore(), scoreModel.getBonusScore());
        setData(rawChart, scoreModel.getNumStandardCorrect(), scoreModel.getNumStandardIncorrect());
        if (gameMode.getGamePlayType() == GamePlayType.SPEED)
            setData(bonusChart, scoreModel.getBonusScore(), scoreModel.getSpeedBonusDeficit());
        else
            setData(bonusChart, scoreModel.getNumBonusCorrect(), scoreModel.getNumBonusIncorrect());

        finalScore.setText(Integer.toString(scoreModel.getFinalScore()));
        rawScore.setText(Integer.toString(scoreModel.getStandardScore()));
        bonusScore.setText(Integer.toString(scoreModel.getBonusScore()));

        rawScoreNumCorrect.setText(scoreModel.getNumStandardCorrect() + " / " + scoreModel.getTotalAnswers());

        if (gameMode.getGamePlayType() == GamePlayType.SPEED)
            bonusScoreNumCorrect.setText(scoreModel.getBonusScore() + " / " + Integer.toString(scoreModel.getTotalAnswers() * 30));
        else
            bonusScoreNumCorrect.setText(scoreModel.getNumBonusCorrect() + " / " + Integer.toString(scoreModel.getNumBonusCorrect() + scoreModel.getNumBonusIncorrect()));


        new Handler().postDelayed(new Runnable() {
            public void run() {
                hideChart(finalChart);
                hideChart(rawChart);
                hideChart(bonusChart);

                hideText(finalScore);
                hideText(rawScore);
                hideText(bonusScore);

                hideText(finalScoreText);
                hideText(rawScoreText);
                hideText(bonusScoreText);

                hideText(rawScoreNumCorrect);
                hideText(bonusScoreNumCorrect);

                animateCircle(finalCircle, finalChart, animStartDelay);
                animateCircle(rawCircle, rawChart, animStartDelay + animRawCircleOffset);
                animateCircle(bonusCircle, bonusChart, animStartDelay + animBonusCircleOffset);

                AnimatorSet revealLineOne = new AnimatorSet();
                AnimatorSet revealLineTwo = new AnimatorSet();
                AnimatorSet revealLineThree = new AnimatorSet();

                revealLineOne.playTogether(getRevealAnimator(finalScore), getRevealAnimator(rawScore), getRevealAnimator(bonusScore));
                revealLineTwo.playTogether(getRevealAnimator(finalScoreText), getRevealAnimator(rawScoreText), getRevealAnimator(bonusScoreText));
                revealLineThree.playTogether(getRevealAnimator(rawScoreNumCorrect), getRevealAnimator(bonusScoreNumCorrect));

                AnimatorSet textReveal = new AnimatorSet();
                textReveal.playSequentially(revealLineOne, revealLineTwo, revealLineThree);
                textReveal.setStartDelay(animTextRevealStartDelay);
                textReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingAnimationsInProgress = false;
                        if (showUserNameDialogOnAnimationsComplete)
                            showUserNameDialog();
                    }
                });
                textReveal.start();
            }
        }, 0);

        return view;
    }

    private ObjectAnimator getRevealAnimator(final View view)
    {
        float endY = view.getY();
        float height = view.getHeight();
        final float startY = endY - height;
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", startY, endY);
        anim.setDuration(animTextRevealDuration);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return anim;
    }


    private void hideChart(PieChart chart) {
        chart.setVisibility(View.INVISIBLE);
    }

    private void hideText(TextView view)
    {
        view.setVisibility(View.INVISIBLE);
    }


    private void animateCircle(View circle, final PieChart chart, int delay)
    {
        final Animation circleReveal = AnimationUtils.loadAnimation(MyApplication.getAppContext(), R.anim.chart_circle_show);
        circleReveal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                chart.setVisibility(View.VISIBLE);
                animateChart(chart);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        circleReveal.setStartOffset(delay);
        circle.startAnimation(circleReveal);
    }


    private void animateChart(PieChart chart)
    {
        chart.animateY(animGraphDuration, Easing.EasingOption.EaseInOutQuad);
    }

    private void setChartStyles(PieChart chart, boolean mainChart, String description) {
        chart.setUsePercentValues(false);
        chart.setDescription("");
        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(mainChart);
        chart.setHoleColorTransparent(true);
        chart.setTransparentCircleColor(ContextCompat.getColor(getActivity(), R.color.results_chart_backing_circle));
        chart.setHoleRadius(64f);
        chart.setTransparentCircleRadius(16f);
        chart.setAlpha(1);

        chart.setDrawCenterText(false);
        chart.setRotationAngle(270f);
        chart.setRotationEnabled(false);
        chart.setDrawSliceText(false);
        chart.setTag(description);
        chart.getLegend().setEnabled(false);
    }

    private void setData(PieChart chart, int numCorrect, int numWrong) {
        ArrayList<Entry> yVals1 = new ArrayList<>();

        yVals1.add(new Entry(numCorrect / (float) (numCorrect + numWrong), 0));
        yVals1.add(new Entry(numWrong / (float) (numCorrect + numWrong), 1));

        ArrayList<String> xVals = new ArrayList<>();

        xVals.add("Correct");
        xVals.add("Wrong");

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(1f);

        ArrayList<Integer> colors = new ArrayList<>();
        if (chart.getTag().equals("Final Results"))
        {
            colors.add(ContextCompat.getColor(getActivity(), R.color.results_chart_standard));
            colors.add(ContextCompat.getColor(getActivity(), R.color.results_chart_bonus));
        }
        else if (chart.getTag().equals("Raw Score"))
        {
            colors.add(ContextCompat.getColor(getActivity(), R.color.results_chart_standard));
            colors.add(ContextCompat.getColor(getActivity(), R.color.blue_sixthly));
        }
        else
        {
            colors.add(ContextCompat.getColor(getActivity(), R.color.results_chart_bonus));
            colors.add(ContextCompat.getColor(getActivity(), R.color.blue_sixthly));
        }

        dataSet.setColors(colors);
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(0);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }




    private void onShowNoWifi()
    {
        Toast.makeText(MyApplication.getAppContext(), "Can't submit score, no internet connection!", Toast.LENGTH_SHORT).show();
    }

    private void onShowHighScore()
    {
        Snackbar snackbar = Snackbar.make(rootLayout, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView text = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(ContextCompat.getColor(getActivity(), R.color.results_tab_snack_text));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.results_tab_snack_background));
        snackbar.show();
    }

    private void showUserNameDialog()
    {
        if (getActivity() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter name");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUserName = input.getText().toString();
                DataResult userNameValidation = User.isValidUsername(newUserName);
                if (!userNameValidation.result) {
                    Toast.makeText(MyApplication.getAppContext(), userNameValidation.message, Toast.LENGTH_LONG).show();
                    showUserNameDialog();
                } else {
                    User.getInstance().setUserName(newUserName);
                    postHighScore();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        Toast.makeText(MyApplication.getAppContext(), "You got a high Score! Enter your name for the leaderboard.", Toast.LENGTH_LONG).show();
    }

    private void submitScore() {
        if (!HighScoreUtils.isNetworkAvailable())
        {
            onShowNoWifi();
            return;
        }

        HighScoreUtils.submitScore(finalScoreValue, this);
    }

    private void postHighScore()
    {
        HighScoreUtils.postScore(finalScoreValue, null);
    }

    @Override
    public void onScoreSubmitting() {}

    @Override
    public void onScoreSubmitResults(HighScoreSubmitResult result) {
        if (result == HighScoreSubmitResult.NOT_HIGH_SCORE)
            return;

        if(User.getInstance().isRegisteredUser())
        {
            onShowHighScore();
            postHighScore();
        }
        else
        {
            if (loadingAnimationsInProgress)
                showUserNameDialogOnAnimationsComplete = true;
            else
                showUserNameDialog();
        }
    }
}