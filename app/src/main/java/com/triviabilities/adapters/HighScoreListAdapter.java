package com.triviabilities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.triviabilities.MyApplication;
import com.triviabilities.R;
import com.triviabilities.models.HighScoreItem;
import com.triviabilities.models.User;

import java.util.List;

public class HighScoreListAdapter extends ArrayAdapter<HighScoreItem>
{

    public HighScoreListAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public HighScoreListAdapter(Context context, int resource, List<HighScoreItem> items)
    {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if (v == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.high_score_list_item, parent, false);
        }

        HighScoreItem p = getItem(position);

        if (p != null) {
            TextView rank = (TextView) v.findViewById(R.id.rank);
            TextView score = (TextView) v.findViewById(R.id.score);
            TextView name = (TextView) v.findViewById(R.id.name);

            if (rank != null) {
                rank.setText(p.getRank());
            }

            if (score != null) {
                score.setText(p.getScore());
            }

            if (name != null) {

                name.setText(p.getName());
            }

            LinearLayout layout = (LinearLayout) v.findViewById(R.id.list_item_layout);
            final ViewGroup.LayoutParams params = layout.getLayoutParams();

            if (p.getName().equals(User.getInstance().getUserName()))
            {
                layout.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.leaderboard_row_background_selected));
                layout.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, MyApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.leaderboard_list_row_height_large)));
            }
            else
            {
                layout.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.leaderboard_row_background_standard));
                layout.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, MyApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.leaderboard_list_row_height)));
            }
        }

        return v;
    }
}
