
@file:JvmName("Utils")

package com.example.marsrealestate.util

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marsrealestate.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar


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
fun ImageView.bindImageUrl(imgUrl: String?) {
    imgUrl?.let {
        val id = it.toIntOrNull()
        if (id != null) {
            setImageDrawable(ResourcesCompat.getDrawable(resources,id,context.theme))
        }
        else {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(this)
                .load(imgUri)


//            .placeholder(R.drawable.ic_launcher_foreground)
//            .error(R.drawable.ic_broken_image_black_24dp))
                .into(this)
        }
    }


}

@BindingAdapter("startAnim")
fun ImageView.startAnim(startAnim: Boolean?) {
    when (startAnim) {
        true -> (drawable as? AnimatedVectorDrawable)?.start()
        false -> (drawable as? AnimatedVectorDrawable)?.stop()
    }
}

@BindingAdapter("fadeInIf")
fun View.fadeInIf(condition: Boolean?) {
    when (condition) {
        true -> {
            isEnabled = true
            visibility = View.VISIBLE
            animate().alpha(1f)
                .withEndAction {  }
                .start()
            ((this as? ImageView)?.drawable as? AnimatedVectorDrawable)?.start()
        }
        false -> {
            isEnabled = false
            animate().alpha(0f)
                .withEndAction {
                    ((this as? ImageView)?.drawable as? AnimatedVectorDrawable)?.stop()
                    visibility = View.INVISIBLE
                }.start()
        }
    }
}

@BindingAdapter("fadeOutIf")
fun View.fadeOutIf(condition: Boolean?) {
    fadeInIf(condition?.not())
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
        addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
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
fun View.setMarginsPlusInsets(marginLeft : Float?,
                              marginTop : Float?,
                              marginRight : Float?,
                              marginBottom : Float?,
                              forceDispatchInsetsToChildren : Boolean?) {

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


@BindingAdapter("showSnackbarOn")
fun CoordinatorLayout.showSnackbarOn( setShape : Boolean) {

    Snackbar.make(this,"",Snackbar.LENGTH_SHORT).apply {

    }
}


/**
 * Set the background bli of a [NavigationView] with the value [R.style.MyStyle_ShapeAppearance_NavigationView]
 * We cannot pass a style to a binding adapter, so this is the only way to do it with DataBinding
 */
@BindingAdapter("shapeAppearanceOverlayNavigationViewUseDefault")
fun NavigationView.setShapeAppearanceOverlayNavigationViewUseDefault( setShape : Boolean) {
    if (setShape) {
        val shape = ShapeAppearanceModel.builder(
            context,
            0,
            R.style.MyStyle_ShapeAppearance_NavigationView
        ).build()


        (background as? MaterialShapeDrawable)?.apply {
            shapeAppearanceModel = shape
        }
    }
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
    val v = this
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
