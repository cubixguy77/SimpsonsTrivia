package com.simpsonstrial2.utils;

import android.animation.Animator;

import com.simpsonstrial2.enums.AnimationPriority;
import com.simpsonstrial2.interfaces.AnimationListener;
import com.simpsonstrial2.interfaces.AnimatorBundleListener;
import com.simpsonstrial2.utils.AnimatorBundle;

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
