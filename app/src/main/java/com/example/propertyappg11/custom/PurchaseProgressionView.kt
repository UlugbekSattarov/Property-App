package com.example.propertyappg11.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.propertyappg11.R
import com.example.propertyappg11.databinding.LayoutPurchaseProgressionViewBinding
import com.example.propertyappg11.util.resolveColor


class PurchaseProgressionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_STEP = 2
    }

    private var viewBinding: LayoutPurchaseProgressionViewBinding =
        LayoutPurchaseProgressionViewBinding.inflate(LayoutInflater.from(context),this,true)

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

    private val colorActive = context.resolveColor(android.R.attr.colorControlNormal)
    private val colorInActive = context.resolveColor(R.attr.colorControlInactiveAlt)

    private val circleDiameter = resources.getDimension(R.dimen.purchase_progression_circle_diameter)
    private val circleScaleInactive = 0.7f
    private val progressBarWidth = resources.getDimension(R.dimen.purchase_progression_bar_width)


    var currentStep : Int = 1
        set(value) {
            if (value < 0 || value > MAX_STEP)
                throw Exception("Invalid state specified : $value , must be between 0 and $MAX_STEP inclusive")
            if (field != value ) {
                field = value
                stateChanged()
            }
        }


    init {
        initSizes()
        stateChanged()
    }


    /**
     * Used to set the size of a few UI elements dynamically
     */
    private fun initSizes() {

        circles.forEach {
            it.updateLayoutParams<ViewGroup.LayoutParams> {
                width = circleDiameter.toInt()
                height = circleDiameter.toInt()
            }
            it.radius = circleDiameter / 2f
        }

        progressBars.forEach { it.updateLayoutParams<ViewGroup.LayoutParams> { height = progressBarWidth.toInt() } }
        progressBarBackgrounds.forEach { it.updateLayoutParams<ViewGroup.LayoutParams> { height = progressBarWidth.toInt() } }
    }



    /**
     * Sets the current step and animates the transitions in between.
     * Valid steps are Int between 0 and [MAX_STEP]
     */
    private fun stateChanged() {

        growCircle(circles[currentStep])
        circles.filterIndexed { index, _ -> index != currentStep }.forEach { shrinkCircle(it) }

        emphasizeText(captions[currentStep])
        captions.filterIndexed { index, _ -> index != currentStep }.forEach { minimizeText(it) }

        circles.filterIndexed { index, _ -> index <= currentStep }.forEach { paintActive(it) }
        circles.filterIndexed { index, _ -> index > currentStep }.forEach { paintInactive(it) }


        progressBars.filterIndexed { index, _ -> index < currentStep }.forEach { growProgressBar(it) }
        progressBars.filterIndexed { index, _ -> index >= currentStep }.forEach { shrinkProgressBar(it) }

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


    private fun paintActive(circle: CardView) = switchColors(circle,true)
    private fun paintInactive(circle: CardView) = switchColors(circle,false)

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
