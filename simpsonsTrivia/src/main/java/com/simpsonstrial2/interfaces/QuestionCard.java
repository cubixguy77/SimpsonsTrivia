package com.simpsonstrial2.interfaces;

public interface QuestionCard
{
    interface View {
        void showQuestion();
        void hideQuestion();
    }

    interface UserActionsListener {
        void onCorrectAnswerSelected();
        void onWrongAnswerSelected();
    }
}
