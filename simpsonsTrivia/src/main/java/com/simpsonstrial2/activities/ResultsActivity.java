package com.simpsonstrial2.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.simpsonstrial2.MyApplication;
import com.simpsonstrial2.R;
import com.simpsonstrial2.adapters.ResultsViewPagerAdapter;
import com.simpsonstrial2.models.GameMode;
import com.simpsonstrial2.utils.IntentManager;
import com.simpsonstrial2.views.CircularTransition;
import com.simpsonstrial2.views.SingleTapGesture;

public class ResultsActivity extends AppCompatActivity
{
    private CharSequence Titles[];
    private ViewPager viewPager;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        setupTitles();
        setupToolBar();
        setupTabs();
        setupButtons();
    }

    public void setupTitles() {
        Titles = new CharSequence[2];
        Titles[0] = MyApplication.getAppContext().getResources().getString(R.string.results_tab_results);
        Titles[1] = MyApplication.getAppContext().getResources().getString(R.string.results_tab_leaderboard);
    }

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.results_tool_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        getSupportActionBar().setTitle(GameMode.getGameMode().getGameModeTitle());
    }

    public void setupTabs() {
        viewPager = (ViewPager) findViewById(R.id.results_view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.results_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupButtons() {
        gestureDetector = new GestureDetector(this, new SingleTapGesture());

        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        playAgainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    float touchX = motionEvent.getRawX();
                    float touchY = motionEvent.getRawY() - MyApplication.screenHeight;

                    goPlayAgain(touchX, touchY);
                }

                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ResultsViewPagerAdapter adapter = new ResultsViewPagerAdapter(getSupportFragmentManager(), Titles, getIntent());
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome()
    {
        startActivity(IntentManager.getMainMenuIntent(this));
    }

    private void goPlayAgain(float touchX, float touchY)
    {
        CircularTransition transition = new CircularTransition(this, findViewById(R.id.RootLayout), touchX, touchY);
        transition.start(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(IntentManager.getQuestionIntent(ResultsActivity.this));
                finish();
            }
        });
    }
}
