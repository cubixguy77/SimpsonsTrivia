package com.triviabilities.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.triviabilities.fragments.LeaderboardTab;
import com.triviabilities.fragments.ResultsTab;

public class ResultsViewPagerAdapter extends FragmentPagerAdapter {

    CharSequence Titles[];
    Intent intent;
    FragmentManager fragmentManager;

    public ResultsViewPagerAdapter(FragmentManager fragmentManager, CharSequence mTitles[], Intent intent) {
        super(fragmentManager);

        this.Titles = mTitles;
        this.intent = intent;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            ResultsTab tab1 = ResultsTab.newInstance(intent.getBundleExtra("ScoreModelBundle"));
            return tab1;
        }
        else
        {
            LeaderboardTab tab2 = LeaderboardTab.newInstance(false, intent.getBundleExtra("ScoreModelBundle"));
            return tab2;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}