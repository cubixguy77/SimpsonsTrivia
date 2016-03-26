package com.simpsonstrial2.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.adapters.HighScoreListAdapter;
import com.simpsonstrial2.R;
import com.simpsonstrial2.enums.Difficulty;
import com.simpsonstrial2.enums.GamePlayType;
import com.simpsonstrial2.enums.HighScoreSubmitResult;
import com.simpsonstrial2.interfaces.HighScoreRequestListener;
import com.simpsonstrial2.interfaces.HighScoreSubmitListener;
import com.simpsonstrial2.models.DataResult;
import com.simpsonstrial2.models.GameMode;
import com.simpsonstrial2.models.HighScoreList;
import com.simpsonstrial2.models.ScoreDataModel;
import com.simpsonstrial2.models.User;
import com.simpsonstrial2.utils.HighScoreUtils;

public class LeaderboardTab extends ListFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, HighScoreSubmitListener, HighScoreRequestListener {

    private HighScoreListAdapter highScoreListAdapter;
    private boolean showSpinner;

    private boolean spinnerSetup = false;
    private boolean listSetup = false;

    private View root;
    private ListView highScoreList;
    private ImageView noWiFiImage;
    private TextView noConnectionText;
    private TextView tapToRefreshText;
    private RelativeLayout listContainer;
    private Spinner gameModePicker;

    private int finalScore;
    private boolean scorePosted = false;
    private boolean firstViewing = true;

    public static LeaderboardTab newInstance(boolean showGameModePicker, Bundle scoreModelBundle) {
        LeaderboardTab myFragment = new LeaderboardTab();
        System.out.println("New leaderboard fragment instance created");

        Bundle args = new Bundle();
        args.putBoolean("ShowGameModePicker", showGameModePicker);
        args.putInt("FinalScore", new ScoreDataModel(scoreModelBundle).getFinalScore());
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_high_score_list, container, false);

        highScoreList = (ListView) root.findViewById(android.R.id.list);
        noWiFiImage = (ImageView) root.findViewById(R.id.noWiFi);
        noConnectionText = (TextView) root.findViewById(R.id.no_connection_text);
        tapToRefreshText = (TextView) root.findViewById(R.id.tap_to_refresh_text);
        listContainer = (RelativeLayout) root.findViewById(R.id.ListContainer);

        Bundle args = getArguments();
        if (args == null || args.getBoolean("ShowGameModePicker", false))
        {
            /* Called from Main Menu */
            GameMode.newInstance(Difficulty.EASY, GamePlayType.CHALLENGE);
            showSpinner = true;
            finalScore = 0;
        }
        else
        {
            /* Called after completing a quiz */
            GameMode.getGameMode();
            finalScore = args.getInt("FinalScore");
        }

        if (showSpinner)
            setSpinnerContent();

        if (HighScoreUtils.isNetworkAvailable())
        {
            setupHighScoreList();
            //refreshList();
            //System.out.println("Results retrieved for leaderboard");
        }
        else
        {
            showNoWiFi();
        }

        return root;
    }

    private void setupHighScoreList()
    {
        highScoreListAdapter = new HighScoreListAdapter(getActivity(), R.layout.high_score_list_item, new HighScoreList().getHighScoreList());
        setListAdapter(highScoreListAdapter);
        listSetup = true;
    }

    private void setSpinnerContent()
    {
        gameModePicker = (Spinner) root.findViewById( R.id.gameModePicker );
        gameModePicker.setVisibility(View.VISIBLE);
        ArrayAdapter<String> gameModeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, GameMode.getGameModeArray());

        gameModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameModePicker.setAdapter(gameModeAdapter);
        gameModePicker.setOnItemSelectedListener(this);

        spinnerSetup = true;
    }

    private void showNoWiFi()
    {
        if (spinnerSetup)
            gameModePicker.setVisibility(View.GONE);
        highScoreList.setVisibility(View.INVISIBLE);
        noWiFiImage.setVisibility(View.VISIBLE);
        noConnectionText.setVisibility(View.VISIBLE);
        tapToRefreshText.setVisibility(View.VISIBLE);

        listContainer.setOnClickListener(this);
    }

    private void hideNoWifi()
    {
        listContainer.setOnClickListener(null);
        noWiFiImage.setVisibility(View.GONE);
        noConnectionText.setVisibility(View.GONE);
        tapToRefreshText.setVisibility(View.GONE);
        highScoreList.setVisibility(View.VISIBLE);
        if (showSpinner)
            gameModePicker.setVisibility(View.VISIBLE);
    }

    private void refreshList()
    {
        if (HighScoreUtils.isNetworkAvailable())
        {
            HighScoreUtils.requestScores(GameMode.getGameMode(), this);
        }
        else
        {
            showNoWiFi();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        GameMode.newInstance(pos);
        refreshList();
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
        highScoreListAdapter.clear();
    }


    private void refreshAdapterData(HighScoreList highScoreList)
    {
        highScoreListAdapter.setNotifyOnChange(false);
        highScoreListAdapter.clear();
        highScoreListAdapter.addAll(highScoreList.getHighScoreList());
        highScoreListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (HighScoreUtils.isNetworkAvailable()) {
            hideNoWifi();

            if (showSpinner && !spinnerSetup)
                setSpinnerContent();
            if (!listSetup)
                setupHighScoreList();

            if (finalScore > 0 && !scorePosted)
                submitScore();
            else
                refreshList();
        }
        else {
            Toast.makeText(getActivity(), "Still no connection", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !firstViewing)
        {
            refreshList();
        }

        if (firstViewing)
            firstViewing = false;
    }

    private void onShowHighScore()
    {
        Snackbar snackbar = Snackbar.make(listContainer, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView text = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(ContextCompat.getColor(getActivity(), R.color.results_tab_snack_text));
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.results_tab_snack_background));
        snackbar.show();

        refreshList();
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
                if (!userNameValidation.result) {
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
        HighScoreUtils.submitScore(finalScore, this);
    }

    private void postHighScore()
    {
        HighScoreUtils.postScore(finalScore, this);
    }



    @Override
    public void onScoreSubmitting() {}

    @Override
    public void onScoreSubmitResults(HighScoreSubmitResult result) {
        scorePosted = true;

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




    @Override
    public void onScoresRequested() {

    }

    @Override
    public void onScoresReturned(HighScoreList list) {
        refreshAdapterData(list);
    }
}