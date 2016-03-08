package com.simpsonstrial2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simpsonstrial2.R;
import com.simpsonstrial2.interfaces.QuestionCard;
import com.simpsonstrial2.models.Question;

public class QuestionView extends Fragment implements QuestionCard.View {

    public static QuestionView newInstance(Bundle questionBundle) {
        QuestionView myFragment = new QuestionView();
        Bundle args = new Bundle();
        args.putBundle("QuestionModelBundle", questionBundle);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_card, container, false);
        Question question = new Question(getArguments().getBundle("QuestionModelBundle"));
        return view;
    }


    @Override
    public void showQuestion() {

    }

    @Override
    public void hideQuestion() {

    }
}
