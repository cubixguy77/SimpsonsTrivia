package com.simpsonstrivia.utils;

import android.animation.Animator;

import com.simpsonstrivia.enums.AnimationPriority;
import com.simpsonstrivia.interfaces.AnimationListener;
import com.simpsonstrivia.interfaces.AnimatorBundleListener;

public class AnimationPresenter
{
    private AnimatorBundle answerResponseAnimations;

    public AnimationPresenter()
    {
        this.answerResponseAnimations = new AnimatorBundle();
    }

    public void removeListeners()
    {
    }

    public void StartAnswerResponseAnimations(final AnimationListener animationListener)
    {
        answerResponseAnimations.startAnimations(new AnimatorBundleListener() {
            @Override
            public void AnimationsComplete() {
                animationListener.onAnimationsComplete();
            }
        });
    }

    public void registerAnimation(Animator animator, AnimationPriority animationPriority)
    {
        answerResponseAnimations.registerAnimation(animator, animationPriority);
    }
}
