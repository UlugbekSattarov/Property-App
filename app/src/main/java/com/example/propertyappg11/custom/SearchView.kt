package com.example.propertyappg11.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.TextViewBindingAdapter
import com.example.propertyappg11.R
import com.example.propertyappg11.databinding.LayoutSearchViewBinding
import com.example.propertyappg11.util.hideSoftInput
import com.example.propertyappg11.util.resolveColor
import com.example.propertyappg11.util.showSoftInput


class SearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.defaultSearchViewStyle
) : FrameLayout(context, attrs, defStyleAttr) {

    fun interface OnSearchListener {
        fun onSearch(str : String)
    }

    fun interface OnSearchInputVisibilityListener {
        fun onSearchInputVisibility(visible : Boolean)
    }


    private val viewBinding: LayoutSearchViewBinding by lazy { LayoutSearchViewBinding.inflate(LayoutInflater.from(context),this,true) }


    private var isSearchInputVisible = false


    var onSearchListener : OnSearchListener? = null
    var onSearchInputVisibilityListener : OnSearchInputVisibilityListener? = null
    var onTextChangeListener : TextViewBindingAdapter.AfterTextChanged? = null



    var title : String
        get() = viewBinding.searchviewTitle.text.toString()
        set(value) {
            viewBinding.searchviewTitle.text = value
        }

    var hint : String
        get() = viewBinding.searchviewTextInput.hint.toString()
        set(value) {
            viewBinding.searchviewTextInput.hint = value
        }

    var inputText : String
        get() = viewBinding.searchviewTextInput.text.toString()
        set(value) =  viewBinding.searchviewTextInput.setText(value)

    var transitionDuration : Int
        get() = viewBinding.searchviewMlayout.getTransition(R.id.transition_searchview_input_toVisible).duration
        set(value) {
            viewBinding.searchviewMlayout.setTransitionDuration(value)
        }



    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchView,defStyleAttr,R.style.DefaultSearchViewStyle)
        loadFromAttributes(attributes)
        attributes.recycle()

        setInteralListeners()

        viewBinding.searchView = this
        transitionDuration  = resources.getInteger(R.integer.anim_search_close_total_duration)
    }

    private fun loadFromAttributes(attrs : TypedArray) {

        val backgroundColor = attrs.getColor(R.styleable.SearchView_backgroundColor,context.resolveColor(R.attr.colorBackground))

        title = attrs.getString(R.styleable.SearchView_titleText) ?: ""
        hint = attrs.getString(R.styleable.SearchView_hintText) ?: ""
        val transitionDuration = attrs.getInteger(R.styleable.SearchView_animationDuration,-1)
        if (transitionDuration > 0) viewBinding.searchviewMlayout.setTransitionDuration(transitionDuration)


        viewBinding.searchviewMlayout.setBackgroundColor(backgroundColor)
        viewBinding.searchviewBackgroundAboveTitle.setBackgroundColor(backgroundColor)

    }

    private fun stateChanged() {

        val stateSet = arrayOf(android.R.attr.state_checked * if (isSearchInputVisible) 1 else -1)
        viewBinding.searchviewIconSearch.setImageState(stateSet.toIntArray(), true)

        viewBinding.searchviewMlayout.run {
            if (isSearchInputVisible)
                transitionToEnd()
            else
                transitionToStart()
        }
    }

    private fun setInteralListeners() {
        setOnClickListenerInternal()
        setOnTextChangedListenerInternal()
        setOnSearchListenerInternal()
        setOnSearchInputVisibilityListenerInternal()
    }

    private fun setOnClickListenerInternal() =
        viewBinding.searchviewIconSearchContainer.setOnClickListener {
            isSearchInputVisible = !isSearchInputVisible
            stateChanged()
        }


    fun clearInputText() {
        inputText = ""
    }


    private fun setOnSearchListenerInternal () =
        viewBinding.searchviewTextInput.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchListener?.onSearch(textView.text.toString())
                hideSoftInput()
                true
            }
            else
                false
        }


    private fun setOnTextChangedListenerInternal() =
        viewBinding.searchviewTextInput.addTextChangedListener {
            onTextChangeListener?.afterTextChanged(it)
        }

    private fun setOnSearchInputVisibilityListenerInternal() =
        viewBinding.searchviewMlayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
               if (! isSearchInputVisible) {
                   viewBinding.searchviewTextInput.isEnabled = false
                    hideSoftInput()
               }
                else {
                   viewBinding.searchviewTextInput.isEnabled = true
                   viewBinding.searchviewTextInput.showSoftInput()
               }
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, constraintSet: Int) {
                if (constraintSet == R.id.constraint_set_searchview_searchinput_visible) {
                    onSearchInputVisibilityListener?.onSearchInputVisibility(true)
                }
                else if (constraintSet == R.id.constraint_set_searchview_searchinput_invisible) {
                    onSearchInputVisibilityListener?.onSearchInputVisibility(false)
                }
            }
        })



}


@BindingAdapter("inputText")
fun SearchView.setInputTextBinding( oldValue: String?, newValue: String?) {
    if (newValue!= null && newValue != oldValue && inputText != newValue) {
        inputText = newValue
    }
}

@InverseBindingAdapter(attribute = "inputText")
fun SearchView.getInputTextBinding(): String = inputText


@BindingAdapter("app:inputTextAttrChanged","onTextChange",requireAll = false)
fun SearchView.setInputTextChangedListenerBinding( attrChange: InverseBindingListener,
                                                   listener : TextViewBindingAdapter.AfterTextChanged?) {
    onTextChangeListener = TextViewBindingAdapter.AfterTextChanged{
        attrChange.onChange()
        listener?.afterTextChanged(it)
    }
}

@BindingAdapter("onSearch")
fun SearchView.setOnSearchListenerBinding( listener : SearchView.OnSearchListener?) =
    listener?.let { onSearchListener = it }


@BindingAdapter("onSearchInputVisibility")
fun SearchView.setOnSearchInputVisibilityListenerBinding( listener : SearchView.OnSearchInputVisibilityListener?) =
    listener?.let { onSearchInputVisibilityListener = it }


