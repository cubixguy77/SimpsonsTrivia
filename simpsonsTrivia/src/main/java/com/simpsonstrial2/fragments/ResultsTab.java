package com.simpsonstrial2.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.R;
import com.simpsonstrial2.enums.GamePlayType;
import com.simpsonstrial2.enums.HighScoreSubmitResult;
import com.simpsonstrial2.interfaces.HighScoreSubmitListener;
import com.simpsonstrial2.models.DataResult;
import com.simpsonstrial2.models.GameMode;
import com.simpsonstrial2.models.ScoreDataModel;
import com.simpsonstrial2.models.User;
import com.simpsonstrial2.utils.HighScoreUtils;

import java.util.ArrayList;

public class ResultsTab extends Fragment implements HighScoreSubmitListener
{
    private int finalScore;

    private ProgressDialog progress;
    private LinearLayout rootLayout;

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
        View view = inflater.inflate(R.layout.fragment_tab_results_alt, container, false);

        PieChart finalChart = (PieChart) view.findViewById(R.id.finalScoreChart);
        PieChart rawChart = (PieChart) view.findViewById(R.id.rawChart);
        PieChart bonusChart = (PieChart) view.findViewById(R.id.bonusChart);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_final_score_layout);

        TextView finalScore = (TextView) view.findViewById(R.id.finalScore);
        TextView rawScore = (TextView) view.findViewById(R.id.rawScore);
        TextView bonusScore = (TextView) view.findViewById(R.id.bonusScore);



        TextView finalScoreText = (TextView) view.findViewById(R.id.finalScoreText);
        TextView rawScoreText = (TextView) view.findViewById(R.id.rawScoreText);
        TextView bonusScoreText = (TextView) view.findViewById(R.id.bonusScoreText);



        TextView rawScoreNumCorrect = (TextView) view.findViewById(R.id.rawScoreNumCorrect);
        TextView bonusScoreNumCorrect = (TextView) view.findViewById(R.id.bonusScoreNumCorrect);




        progress = new ProgressDialog(getActivity());
        progress.setMessage("Downloading High Scores");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        setChartStyles(finalChart, true, "Final Results");
        setChartStyles(rawChart, false, "Raw Score");
        setChartStyles(bonusChart, false, "Bonus");

        ScoreDataModel scoreModel = new ScoreDataModel(getArguments().getBundle("ScoreModelBundle"));
        GameMode gameMode = GameMode.getGameMode();
        this.finalScore = scoreModel.getFinalScore();

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

        rawScoreNumCorrect.setText(scoreModel.getStandardScore() + " / " + scoreModel.getTotalAnswers());

        if (gameMode.getGamePlayType() == GamePlayType.SPEED)
            rawScoreNumCorrect.setText(scoreModel.getBonusScore() + " / " + Integer.toString(scoreModel.getTotalAnswers() * 30));
        else
            rawScoreNumCorrect.setText(scoreModel.getNumBonusCorrect() + " / " + Integer.toString(scoreModel.getNumBonusCorrect() + scoreModel.getNumBonusIncorrect()));

        hideText(finalScore);
        hideText(rawScore);
        hideText(bonusScore);

        hideText(finalScoreText);
        hideText(rawScoreText);
        hideText(bonusScoreText);

        hideText(rawScoreNumCorrect);
        hideText(bonusScoreNumCorrect);

        animateChart(finalChart);
        animateChart(rawChart);
        animateChart(bonusChart);

        return view;
    }


    private void hideText(TextView view)
    {
        System.out.println(view.getY() + " " + view.getHeight());
        view.setY(view.getY() - view.getHeight());
    }




    private void animateChart(PieChart chart)
    {
        chart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
    }

    private void setChartStyles(PieChart chart, boolean mainChart, String description) {
        chart.setUsePercentValues(false);
        chart.setDescription("");
        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setTransparentCircleColor(getResources().getColor((R.color.results_chart_backing_circle)));

        chart.setDrawHoleEnabled(mainChart);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setTransparentCircleAlpha(0);

        chart.setDrawCenterText(false);
        chart.setRotationAngle(0);
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
            colors.add(getResources().getColor(R.color.results_chart_standard));
            colors.add(getResources().getColor(R.color.results_chart_bonus));
        }
        else if (chart.getTag().equals("Raw Score"))
        {
            colors.add(getResources().getColor(R.color.results_chart_standard));
            colors.add(getResources().getColor(R.color.results_chart_backing_circle));
        }
        else
        {
            colors.add(getResources().getColor(R.color.results_chart_bonus));
            colors.add(getResources().getColor(R.color.results_chart_backing_circle));
        }

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }




    private void onShowNoWifi()
    {
        Toast.makeText(MyApplication.getAppContext(), "No wifi!", Toast.LENGTH_LONG).show();
    }

    private void onShowHighScore()
    {
        Snackbar snackbar = Snackbar.make(rootLayout, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView text = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(getResources().getColor(R.color.results_tab_snack_text));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        sbView.setBackgroundColor(getResources().getColor(R.color.results_tab_snack_background));
        snackbar.show();
    }

    private void showUserNameDialog()
    {
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
                if (userNameValidation.result == false) {
                    Toast.makeText(MyApplication.getAppContext(), userNameValidation.message, Toast.LENGTH_LONG).show();
                    showUserNameDialog();
                    return;
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
        if (HighScoreUtils.isNetworkAvailable() == false)
        {
            onShowNoWifi();
            return;
        }

        HighScoreUtils.submitScore(finalScore, this);
    }

    private void postHighScore()
    {
        HighScoreUtils.postScore(finalScore, null);
    }

    @Override
    public void onScoreSubmitting() {
        progress.show();
    }

    @Override
    public void onScoreSubmitResults(HighScoreSubmitResult result) {
        progress.setProgress(100);

        if (result == HighScoreSubmitResult.NOT_HIGH_SCORE)
            return;

        if(User.getInstance().isRegisteredUser())
        {
            onShowHighScore();
            postHighScore();
        }
        else
        {
            showUserNameDialog();
        }
    }
}