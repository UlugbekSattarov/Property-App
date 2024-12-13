package com.example.propertyappg11.util

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.example.propertyappg11.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFade


@BindingAdapter("itemSpacing", "columnNumber","endSpace",requireAll = false)
fun RecyclerView.bindItemSpacing( spacing: Float?, columnNumber : Int?, endSpace : Float?) {
    if (spacing == null && columnNumber == null && endSpace == null)
        return

    val manager = GridLayoutManager(context,
        columnNumber ?: 1,
        RecyclerView.VERTICAL,
        false)

    if ((spacing != null && spacing > 0) || (endSpace != null && endSpace > 0 ) )
        addItemDecoration(MarginItemDecoration(
            spacing?.toInt() ?: 0,
            columnNumber ?: 1,
            endSpace?.toInt() ?: 0))


    layoutManager = manager
}




/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(imgUrl: String?) {
    imgUrl?.let { url ->

        try {
                Glide.with(this).load(url.toUri()).override(1280,720)
                    .into(this)
//                setImageURI(url.toUri())

        } catch (e: Exception) {
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_share_24, context.theme))
        }

    }

}




@BindingAdapter("startAnim")
fun ImageView.startAnim(startAnim: Boolean?) {
    when (startAnim) {
        true -> (drawable as? AnimatedVectorDrawable)?.start()
        false -> (drawable as? AnimatedVectorDrawable)?.stop()
        null -> (drawable as? AnimatedVectorDrawable)?.stop()
    }
}




@BindingAdapter("fadeInIf","fadeInDuration","fadeOutDuration","startDelay",requireAll = false)
fun View.fadeInIf(condition: Boolean?,
                  fadeIntDuration : Long? = null,
                  fadeOutDuration : Long? = null,
                  startDelay: Long? = null
) {
    when (condition) {
        true -> {
            isEnabled = true
            visibility = View.VISIBLE
            animate().alpha(1f)
                .setDuration(fadeIntDuration ?: resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .setStartDelay(startDelay ?: 0)
                .start()
            ((this as? ImageView)?.drawable as? AnimatedVectorDrawable)?.start()
        }
        false -> {
            isEnabled = false
            animate().alpha(0f)
                .setDuration(fadeOutDuration ?: resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .setStartDelay(startDelay ?: 0)
                .withEndAction {
                    ((this as? ImageView)?.drawable as? AnimatedVectorDrawable)?.stop()
                    visibility = View.INVISIBLE
                }.start()
        }
        null -> { }
    }
}

@BindingAdapter("fadeOutIf")
fun View.fadeOutIf(condition: Boolean?) {
    fadeInIf(condition?.not())
}

@BindingAdapter("materialFadeInIf","materialFadeInDelay",requireAll = false)
fun View.materialFadeInIf(condition: Boolean?,materialFadeInDelay : Long? = null) {
    condition?.let {
        val fade = MaterialFade().apply {
            startDelay  = materialFadeInDelay ?: 0
            duration = 300
        }
        TransitionManager.beginDelayedTransition(parent as ViewGroup, fade)
        visibility = if (condition) View.VISIBLE else View.INVISIBLE
    }
}

@BindingAdapter("scaleInIf","scaleInDelay",requireAll = false)
fun View.scaleInIf(condition: Boolean?, scaleInDelay : Long? = null) {
    when (condition) {
        true -> {
            isEnabled = true
            visibility = View.VISIBLE
            animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(200)
                .setStartDelay(scaleInDelay ?: 0)
                .start()
        }
        false -> {
            isEnabled = false
            animate().alpha(0f).scaleX(0f).scaleY(0f)
                .setDuration(200)
                .withEndAction {
                    visibility = View.INVISIBLE
                }.start()
        }
        null -> { }
    }
}


@BindingAdapter("setChecked")
fun ImageView.setChecked(setChecked: Boolean?) {
    setChecked?.let {
        val stateSet =
            intArrayOf(android.R.attr.state_checked * if (setChecked) 1 else -1)
        this.setImageState(stateSet,true)
    }
}



const val PIVOT_TOP_LEFT = 0
const val PIVOT_TOP_RIGHT = 1
const val PIVOT_BOTTOM_RIGHT = 2
const val PIVOT_BOTTOM_LEFT = 3


@BindingAdapter("transformPivot")
fun View.setTransformPivot(pivot : Int?) {
    pivot?.let { p ->
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            when (p) {
                PIVOT_TOP_LEFT -> { pivotX = 0f; pivotY = 0f }
                PIVOT_TOP_RIGHT -> { pivotX = width.toFloat(); pivotY = 0f }
                PIVOT_BOTTOM_RIGHT -> { pivotX = width.toFloat(); pivotY = this.bottom.toFloat() }
                PIVOT_BOTTOM_LEFT -> { pivotX = 0f; pivotY = this.bottom.toFloat() }
            }
        }
    }
}



fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}


@BindingAdapter("marginLeftPlusInsets",
    "marginTopPlusInsets",
    "marginRightPlusInsets",
    "marginBottomPlusInsets",
    "forceDispatchInsetsToChildren",
    requireAll = false)
fun View.setMarginsPlusInsets(marginLeft : Float? = null,
                              marginTop : Float? = null,
                              marginRight : Float? = null,
                              marginBottom : Float? = null,
                              forceDispatchInsetsToChildren : Boolean? = null) {

    setOnApplyWindowInsetsListener { view, insets ->
        val newParams = view.layoutParams as? ViewGroup.MarginLayoutParams

        newParams?.let { np ->






            if (marginLeft != null)
                np.leftMargin = marginLeft.toInt() + insets.systemWindowInsetLeft

            if (marginTop != null)
                np.topMargin = marginTop.toInt() + insets.systemWindowInsetTop

            if (marginRight != null)
                np.rightMargin = marginRight.toInt() + insets.systemWindowInsetRight

            if (marginBottom != null)
                np.bottomMargin = marginBottom.toInt() + insets.systemWindowInsetBottom

            if (view is NavigationView)
                np.leftMargin = 500

            view.layoutParams = np
        }

        if (forceDispatchInsetsToChildren == true && view is ViewGroup) {
            view.children.forEach {
                ViewCompat.dispatchApplyWindowInsets(it,WindowInsetsCompat.toWindowInsetsCompat(insets))
            }
        }
        insets
    }

//    requestApplyInsetsWhenAttached()
}




@BindingAdapter("dataList")
fun RecyclerView.bindDataList(list : List<Any>?) {
    list?.let {newList ->
        @Suppress("UNCHECKED_CAST")
        (adapter as? androidx.recyclerview.widget.ListAdapter<Any, RecyclerView.ViewHolder>)?.let { ada ->
            ada.submitList(newList)
            //useful to add new items on the fly
//            ada.notifyDataSetChanged()
        }
    }
}

interface onSwipeListener { fun onSwipe(swipedItem : Any)}

@BindingAdapter("onSwipeListener")
fun RecyclerView.setOnSwipeListener(onSwipe : (Any) -> Unit) {
    val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        @Suppress("UNCHECKED_CAST")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val items = (adapter
                    as? androidx.recyclerview.widget.ListAdapter<Any, RecyclerView.ViewHolder>)?.currentList

            if (items != null)
                onSwipe(items[viewHolder.absoluteAdapterPosition])
        }
    }
    ItemTouchHelper(callback).attachToRecyclerView(this)
}




@BindingAdapter("popupOnMessage")
fun TextView.popupOnMessage(msgId : Int?) {
    msgId?.let {
        fun hide() : ViewPropertyAnimator {
            return animate().alpha(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setStartDelay(0)
                .setDuration(200)
                .withEndAction {
                    visibility = View.GONE
                }
        }

        fun reveal() : ViewPropertyAnimator {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            return animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setStartDelay(0)
                .withEndAction { hide().setStartDelay(1500).start() }
                .setDuration(200)

        }

        if (visibility == View.VISIBLE) {
            animate().cancel()
            hide().withEndAction { text = resources.getString(msgId); reveal().start() }.start()
        }
        else {
            text = resources.getString(msgId)
            reveal().start()
        }

    }
}

@BindingAdapter("layoutWidth","layoutHeight",requireAll = false)
fun View.setLayoutSize(oldWidth : Float?, oldHeight: Float?,newWidth : Float?, newHeight : Float? ) {

    updateLayoutParams<ViewGroup.LayoutParams> {
        if (newWidth != null && newWidth != oldWidth && newWidth != 0f)
            width = newWidth.toInt()
        if (newHeight != null && newHeight != oldHeight && newHeight != 0f)
            height = newHeight.toInt()


//        androidx.transition.TransitionManager.beginDelayedTransition(v as ViewGroup)
    }

}


@BindingAdapter("extendedFabText")
fun ExtendedFloatingActionButton.setExtendedFabText(oldText : String?, newText : String? ) {
    if (newText != null && newText != oldText) {
        text = newText.toString()
        extend()
    }
}


@BindingAdapter("textFloat")
fun TextInputEditText.setTextFloat(oldFloat : Float?, newFloat : Float?) {
    if (newFloat != null && newFloat != oldFloat) {
        if (text.toString().isEmpty() && newFloat == 0f)
            return
        if (text.toString().toFloatOrNull() != newFloat)
            setText(newFloat.toString())
    }
}


@InverseBindingAdapter(attribute = "textFloat")
fun TextInputEditText.getTextFloat(): Float = text.toString().toFloatOrNull() ?: 0f

@BindingAdapter("textFloatAttrChanged")
fun TextInputEditText.setTextFloatListeners(
    attrChange: InverseBindingListener
) {
    this.addTextChangedListener { _ ->
        attrChange.onChange()
    }

}


@BindingAdapter("onTransitionEnd","endConstraintSetId",requireAll = false)
fun MotionLayout.setOnTransitionEnd(onTransitionEnd : (() -> Unit)?, @IdRes endConstraintSetId : Int?) =
    onTransitionEnd?.let {
        addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, constraintSet: Int) {
                if (constraintSet == endConstraintSetId) {
                    removeTransitionListener(this)
                    onTransitionEnd.invoke()
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }
