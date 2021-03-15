package com.example.marsrealestate.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.provider.CalendarContract
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.LayoutNavigationMenuBinding


class NavigationItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var viewBinding: LayoutNavigationMenuBinding =
        LayoutNavigationMenuBinding.inflate(LayoutInflater.from(context),this,true)

    var title : String
        get() = viewBinding.title.text.toString()
        set(value) {
            viewBinding.title.text = value
        }

    var endText : String
        get() = viewBinding.endText.toString()
        set(value) {
            viewBinding.endText.text = value
        }

    var startIcon : Drawable?
        get() = viewBinding.startIcon.drawable
        set(value) { setImageDrawable(value)}


    var isActive:  Boolean = false
        set(value) {
            if (field != value) {
                field = value
                controlStateChanged()
            }
        }

    private val defaultTextColor = getDefaultTextColor()

    private var defaultTextColorList : ColorStateList = ColorStateList.valueOf(
        ResourcesCompat.getColor(resources,R.color.control_activable,context.theme))

    private val currentControlControl : Int
        get() {
            val state = if (isActive) android.R.attr.state_checked else 0
            return defaultTextColorList.getColorForState(intArrayOf(state), defaultTextColor)
        }


    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NavigationItemView)
        loadFromAttributes(attributes)
        attributes.recycle()

        controlStateChanged()
    }


    private fun loadFromAttributes(attrs : TypedArray) {
        title = attrs.getString(R.styleable.NavigationItemView_title) ?: ""
        endText = attrs.getString(R.styleable.NavigationItemView_endText) ?: ""
        startIcon = attrs.getDrawable(R.styleable.NavigationItemView_startIcon)
        attrs.getColorStateList(R.styleable.NavigationItemView_textColor)?.let { defaultTextColorList = it }
    }


    private fun getDefaultTextColor() : Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        return typedValue.data
    }

    private fun setImageDrawable(drawable : Drawable?) {
        val draw = if (drawable is BitmapDrawable) {
            val f = RoundedBitmapDrawableFactory.create(resources,drawable.bitmap)
            f.cornerRadius = f.bitmap?.height?.times(2.5f) ?: 0f
            f.setAntiAlias(true)
            f
        }
        else {
            drawable?.apply { setTint(currentControlControl) }
        }

        viewBinding.startIcon.setImageDrawable(draw)
    }

    override fun setOnClickListener( l: OnClickListener?) {
        viewBinding.root.setOnClickListener(l)
    }


    private fun controlStateChanged() {
        val textColor = currentControlControl

        viewBinding.title.setTextColor(textColor)
        viewBinding.endText.setTextColor(textColor)

        val fontSize =  if (isActive) Typeface.BOLD else Typeface.NORMAL
        viewBinding.title.setTypeface(Typeface.create(viewBinding.title.typeface,fontSize),fontSize)
        viewBinding.endText.setTypeface(Typeface.create(viewBinding.endText.typeface,fontSize),fontSize)

        viewBinding.navigationMenuItem.isChecked = isActive

        if (viewBinding.startIcon.drawable is VectorDrawable)
            viewBinding.startIcon.drawable.setTint(textColor)

    }

}


@BindingAdapter("title")
fun NavigationItemView.setTitleBinding(oldValue: String?, newValue: String?) {
    if (newValue!= null && newValue != oldValue && title != newValue) {
        title = newValue
    }
}

@BindingAdapter("endText")
fun NavigationItemView.setEndTextBinding(oldValue: String?, newValue: String?) {
    if (newValue!= null && newValue != oldValue && endText != newValue) {
        endText = newValue
    }
}

@BindingAdapter("startIcon")
fun NavigationItemView.setStartIconBinding(oldValue: Drawable?, newValue: Drawable?) {
    if (newValue!= null && newValue != oldValue && startIcon != newValue) {
        startIcon = newValue
    }
}


@BindingAdapter("isActive")
fun NavigationItemView.setIsActiveBinding(oldValue: Boolean?, newValue: Boolean?) {
    if (newValue!= null && newValue != oldValue && isActive != newValue) {
        isActive = newValue
    }
}

