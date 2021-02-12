package com.example.marsrealestate.detail

import android.view.View
import androidx.annotation.FloatRange
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max



/**
 * Will scale down items other than the active one, and make the previous and next one visible.
 * The item [View.marginEnd] has to be set to a value > 0 in order to display a peek of the next
 * and previous items.
 */
class DetailViewPagerPageTransformer : ViewPager2.PageTransformer {

    var minScale = 0.9f

    /**
     * At 0.0, the previous and next items won't be visible because out of visible bounds.
     * At 1.0, they will appear stuck to the middle item sides.
     * In between, they will appear more or less close to the middle element.
     * */
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = true, toInclusive = true)
    var coeffSpaceBetweenElements = 0.6f


    override fun transformPage(view: View, position: Float) {
        view.apply {

            //Will increase the size of the object in position 0
            val scaleFactor = max(minScale, 1 - abs(position))
            scaleX = scaleFactor
            scaleY = scaleFactor

            val lostSizeByScale = (width - width * scaleFactor) / 2f

            //Will make previous and next items visible
            val offset = position * -( coeffSpaceBetweenElements * view.marginEnd + view.marginStart + lostSizeByScale )
            translationX = offset
        }
    }
}