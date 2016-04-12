package com.triviabilities.utils;

import android.animation.Animator;

import com.triviabilities.enums.AnimationPriority;
import com.triviabilities.interfaces.AnimationListener;
import com.triviabilities.interfaces.AnimatorBundleListener;

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
