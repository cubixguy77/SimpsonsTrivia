package com.triviabilities.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

import com.triviabilities.enums.AnimationPriority;
import com.triviabilities.interfaces.AnimatorBundleListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatorBundle
{
    private Map<AnimationPriority, List<Animator>> animations;
    private AnimatorSet globalAnimatorSet;

    public AnimatorBundle()
    {
        this.animations = new HashMap<>();
        loadAnimationsMap();
    }

    public void loadAnimationsMap()
    {
        for (AnimationPriority animationPriority : AnimationPriority.values())
        {
            this.animations.put(animationPriority, new ArrayList<Animator>());
        }
    }

    public void registerAnimation(Animator animator, AnimationPriority animationPriority)
    {
        this.animations.get(animationPriority).add(animator);
    }

    public void startAnimations(final AnimatorBundleListener listener)
    {
        if (this.globalAnimatorSet == null)
        {
            this.globalAnimatorSet = this.GetGlobalAnimatorSet();
        }

        this.globalAnimatorSet.removeAllListeners();
        this.globalAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                listener.AnimationsComplete();
            }
        });

        this.globalAnimatorSet.start();
    }

    private AnimatorSet GetGlobalAnimatorSet()
    {
        AnimatorSet first = new AnimatorSet();
        first.playTogether(animations.get(AnimationPriority.FIRST));

        AnimatorSet second = new AnimatorSet();
        second.playTogether(animations.get(AnimationPriority.SECOND));

        AnimatorSet third = new AnimatorSet();
        third.playTogether(animations.get(AnimationPriority.THIRD));

        AnimatorSet fourth = new AnimatorSet();
        fourth.playTogether(animations.get(AnimationPriority.FOURTH));

        AnimatorSet globalAnimatorSet = new AnimatorSet();
        globalAnimatorSet.playSequentially(first, second, third, fourth);
        return globalAnimatorSet;
    }
}
