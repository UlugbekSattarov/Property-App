package com.example.propertyappg11.detail

import android.view.View
import androidx.annotation.FloatRange
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class DetailViewPagerPageTransformer : ViewPager2.PageTransformer {

    var minScale = 0.9f
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = true, toInclusive = true)
    var coeffSpaceBetweenElements = 0.6f


    override fun transformPage(view: View, position: Float) {
        view.apply {

            val scaleFactor = max(minScale, 1 - abs(position))
            scaleX = scaleFactor
            scaleY = scaleFactor

            val lostSizeByScale = (width - width * scaleFactor) / 2f

            val offset = position * -( coeffSpaceBetweenElements * view.marginEnd + view.marginStart + lostSizeByScale )
            translationX = offset
        }
    }
}
