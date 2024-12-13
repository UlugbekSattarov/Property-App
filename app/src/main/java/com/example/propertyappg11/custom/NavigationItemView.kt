package com.example.propertyappg11.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.propertyappg11.R
import com.example.propertyappg11.databinding.LayoutNavigationItemViewBinding
import com.example.propertyappg11.util.resolveColor
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel


class NavigationItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var viewBinding: LayoutNavigationItemViewBinding =
        LayoutNavigationItemViewBinding.inflate(LayoutInflater.from(context),this,true)

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

    var startIcon : Drawable? = null
        get() = viewBinding.startIcon.drawable
        set(value) {
            if (field != value) {
                setImageDrawable(viewBinding.startIcon,value)
                stateChanged()
            }
        }


    var isActive:  Boolean = false
        set(value) {
            if (field != value) {
                field = value
                stateChanged()
            }
        }

    private val defaultTextColor = context.resolveColor(android.R.attr.textColorPrimary)
    private val defaultColorInactive = context.resolveColor(R.attr.colorControlInactive)
    private val defaultColorActive = context.resolveColor(R.attr.colorControlNormal)

    private val defaultBackgroundHighlightColor = context.resolveColor(R.attr.colorControlHighlightAlt)

    private var _defaultTextColorList : ColorStateList =  ResourcesCompat.getColorStateList(resources,
        R.color.control_activable,context.theme) ?: ColorStateList.valueOf(defaultTextColor)




    private val _textColor : Int
        @ColorInt
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val state = if (isActive) android.R.attr.state_checked else 0
                _defaultTextColorList.getColorForState(intArrayOf(state), defaultTextColor)

            }
            else if (isActive)
                defaultColorActive
            else
                defaultColorInactive

    private val _iconColor : Int
        @ColorInt
        get() = _textColor


    private val _backgroundTint : Int
        @ColorInt
        get() = if (isActive) defaultBackgroundHighlightColor else Color.TRANSPARENT


    private val _fontSize : Int
        get() = if (isActive) Typeface.BOLD else Typeface.NORMAL




    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NavigationItemView)
        loadFromAttributes(attributes)
        attributes.recycle()

        setShape()

        stateChanged()
    }


    private fun loadFromAttributes(attrs : TypedArray) {
        title = attrs.getString(R.styleable.NavigationItemView_title) ?: ""
        endText = attrs.getString(R.styleable.NavigationItemView_endText) ?: ""
        startIcon = attrs.getDrawable(R.styleable.NavigationItemView_startIcon)
        attrs.getColorStateList(R.styleable.NavigationItemView_textColor)?.let { _defaultTextColorList = it }
    }

    private fun setShape() =
        (viewBinding.root as MaterialCardView).apply {
            shapeAppearanceModel = ShapeAppearanceModel.builder(
                context,
                R.style.MyStyle_ShapeAppearance_NavigationItemView,
                R.style.MyStyle_ShapeAppearance_NavigationItemView)
                .build()
        }




    private fun setImageDrawable(imageView : ImageView, drawable : Drawable?) {
        val toDraw =
            if (drawable is BitmapDrawable) {
                RoundedBitmapDrawableFactory.create(resources,drawable.bitmap).apply {
                    cornerRadius = bitmap?.height?.times(2.5f) ?: 0f
                    setAntiAlias(true)
                }
            }
            else
                drawable


        imageView.setImageDrawable(toDraw)
    }

    override fun setOnClickListener( l: OnClickListener?) {
        viewBinding.root.setOnClickListener(l)
    }


    private fun stateChanged() {

        viewBinding.title.setTextColor(_textColor)
        viewBinding.endText.setTextColor(_textColor)

        viewBinding.title.setTypeface(Typeface.create(viewBinding.title.typeface,_fontSize),_fontSize)
        viewBinding.endText.setTypeface(Typeface.create(viewBinding.endText.typeface,_fontSize),_fontSize)

//        (viewBinding.root as MaterialCardView).isChecked = isActive

        //The color state list has a strange behavior so we apply the color manually
        (viewBinding.root as MaterialCardView).setCardBackgroundColor(_backgroundTint)


        if (startIcon is VectorDrawable || startIcon is VectorDrawableCompat)
            viewBinding.startIcon.drawable.setTint(_iconColor)

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

