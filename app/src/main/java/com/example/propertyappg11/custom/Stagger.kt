package com.example.propertyappg11.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.transition.Fade
import androidx.transition.SidePropagation
import androidx.transition.TransitionValues

@Deprecated(message = "This is too complex and not customizable enough to use for a recyclerview",
    replaceWith = ReplaceWith("A layoutAnimation for the recyclerview")
)
class Stagger : Fade(IN) {

    init {
        duration = 3000
        interpolator = LinearOutSlowInInterpolator()
        propagation = SidePropagation().apply {
            setSide(Gravity.BOTTOM)
            setPropagationSpeed(2f)
        }


    }


    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        val view = startValues?.view ?: endValues?.view ?: return null
        val fadeAnimator = super.createAnimator(sceneRoot, startValues, endValues) ?: return null
        return AnimatorSet().apply {
            playTogether(
                fadeAnimator,
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.height * 0.5f, 0f)
            )
        }
    }
}



