package com.example.marsrealestate.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.LayoutPurchaseProgressionBinding


class PurchaseProgressionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var viewBinding: LayoutPurchaseProgressionBinding =
        LayoutPurchaseProgressionBinding.inflate(LayoutInflater.from(context),this,true)

    val MAX_STEP = 2

    private val circles = arrayOf(
        viewBinding.purchaseProgressionCircle1,
        viewBinding.purchaseProgressionCircle2,
        viewBinding.purchaseProgressionCircle3)

    private val progressBarBackgrounds = arrayOf(
        viewBinding.purchaseProgressionBar1Background,
        viewBinding.purchaseProgressionBar2Background
    )

    private val progressBars = arrayOf(
        viewBinding.purchaseProgressionBar1,
        viewBinding.purchaseProgressionBar2
    )

    private val captions = arrayOf(
        viewBinding.purchaseProgressionCircle1Caption,
        viewBinding.purchaseProgressionCircle2Caption,
        viewBinding.purchaseProgressionCircle3Caption
    )

    private val colorActive = kotlin.run {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorControlNormal, typedValue, true)
        typedValue.data
    }

    private val colorInActive = kotlin.run {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorControlInactiveAlt, typedValue, true)
        typedValue.data
    }



    private val circleDiameter = resources.getDimension(R.dimen.purchase_progression_circle_diameter)
    private val circleScaleInactive = 0.7f
    private val progressBarWidth = resources.getDimension(R.dimen.purchase_progression_bar_width)


    var currentStep : Int = 0
        set(value) {
            if (value < 0 || value > MAX_STEP)
                throw Exception("Invalid state specified : $value , must be between 0 and $MAX_STEP inclusive")
            if (field != value ) {
                stateChanged(value)
                field = value
            }
        }


    init {
        initComponent()
        stateChanged(currentStep)
        viewBinding.root.setOnClickListener {
            stateChanged(2)
        }
    }

    /**
     * Used to set the size of a few UI elements dynamically
     */
    private fun initComponent() {

        circles.forEach {
            it.updateLayoutParams<ViewGroup.LayoutParams> {
                width = circleDiameter.toInt()
                height = circleDiameter.toInt()
            }
            it.radius = circleDiameter
        }

        progressBars.forEach { it.updateLayoutParams<ViewGroup.LayoutParams> { height = progressBarWidth.toInt() } }
        progressBarBackgrounds.forEach { it.updateLayoutParams<ViewGroup.LayoutParams> { height = progressBarWidth.toInt() } }
    }



    /**
     * Sets the current step and animates the transitions in between.
     * Valid steps are Int between 0 and [MAX_STEP]
     */
    private fun stateChanged(newStep : Int) {


        growCircle(circles[newStep])
        circles.filterIndexed { index, _ -> index != newStep }.forEach { shrinkCircle(it) }

        emphasizeText(captions[newStep])
        captions.filterIndexed { index, _ -> index != newStep }.forEach { minimizeText(it) }

        circles.filterIndexed { index, _ -> index <= newStep }.forEach { switchColors(it,true) }
        circles.filterIndexed { index, _ -> index > newStep }.forEach { switchColors(it,false) }


        progressBars.filterIndexed { index, _ -> index < newStep }.forEach { growProgressBar(it) }
        progressBars.filterIndexed { index, _ -> index >= newStep }.forEach { shrinkProgressBar(it) }

    }



    private fun emphasizeText(textView: TextView) {
        textView.alpha = 1f
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,captionFontSizeEmphasized)
        textView.setTypeface(Typeface.create(textView.typeface,Typeface.BOLD),Typeface.BOLD)

    }

    private fun minimizeText(textView: TextView) {
        textView.alpha = 0.7f
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,captionFontSizeMinimized)
        textView.setTypeface(Typeface.create(textView.typeface,Typeface.NORMAL),Typeface.NORMAL)
    }

    private fun shrinkCircle(circle : View) = shrinkOrGrowCircle(circle, true)
    private fun growCircle(circle : View) = shrinkOrGrowCircle(circle, false)

    private fun shrinkOrGrowCircle(circle : View, shrink : Boolean) {
        val newCircleScale = if (shrink) circleScaleInactive else 1f
        if (circle.scaleX == newCircleScale) return

        circle.animate()
            .scaleX(newCircleScale)
            .scaleY(newCircleScale)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(300)
            .start()
    }

    private fun shrinkProgressBar(progressBar : View) = shrinkOrGrowProgressBar(progressBar, true)
    private fun growProgressBar(progressBar : View) = shrinkOrGrowProgressBar(progressBar, false)

    private fun shrinkOrGrowProgressBar(progressBar : View, shrink : Boolean) {
        val newScaleX = if (shrink) 0f else 1f
        progressBar.animate().setInterpolator(FastOutSlowInInterpolator()).scaleX(newScaleX).start()
    }


    private fun switchColors(circle : CardView,active : Boolean) {
        val colorFrom = circle.cardBackgroundColor.defaultColor
        val colorTo = if (active)  colorActive else colorInActive
        ObjectAnimator.ofArgb(circle, "cardBackgroundColor",colorFrom,colorTo).apply {
            startDelay = 100
        }.start()

    }
}


@BindingAdapter("currentStep")
fun PurchaseProgressionView.setCurrentStepBinding(oldValue: Int?, newValue: Int?) {
    if (newValue!= null && newValue != oldValue && currentStep != newValue) {
        currentStep = newValue
    }
}
