package com.example.marsrealestate.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.*
import androidx.databinding.adapters.TextViewBindingAdapter
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.LayoutSearchviewBinding
import com.google.android.material.card.MaterialCardView


class SearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.defaultSearchViewStyle
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OnSearchListener {
        fun onSearch(str : String)
    }

    interface OnViewCollapseExpand {
        fun onViewState(collapsed : Boolean)
    }


    private val viewBinding: LayoutSearchviewBinding by lazy { LayoutSearchviewBinding.inflate(LayoutInflater.from(context),this,true) }

    private var isViewExpanded = false


    var onSearchListener : OnSearchListener? = null
    var onViewCollapseListener : OnViewCollapseExpand? = null
    var onTextChangeListener : TextViewBindingAdapter.AfterTextChanged? = null

    /** Must only be used with [SearchView.setInputTextChangedListenerBinding]
     * This is a limitation of databinding requiring prublic properties
     * **/
    var onTextChangeListener_BINDING_ONLY : TextViewBindingAdapter.AfterTextChanged? = null



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



    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchView,defStyleAttr,R.style.DefaultSearchViewStyle)
        loadFromAttributes(attributes)
        attributes.recycle()
        setInteralListeners()
        viewBinding.searchviewMlayout.setTransitionDuration(400)
    }


    private fun loadFromAttributes(attrs : TypedArray) {

        val bckgrdclr = attrs.getColor(R.styleable.SearchView_backgroundColor,ResourcesCompat.getColor(resources,android.R.color.white,null))

        title = attrs.getString(R.styleable.SearchView_titleText) ?: ""
        hint = attrs.getString(R.styleable.SearchView_hintText) ?: ""
        viewBinding.searchviewMlayout.setTransitionDuration(attrs.getInteger(R.styleable.SearchView_animationDuration,1000))

        viewBinding.searchviewMlayout.setBackgroundColor(bckgrdclr)
        viewBinding.searchviewBackgroundAboveTitle.setBackgroundColor(bckgrdclr)
        viewBinding.searchviewEraseTextInputContainer.setBackgroundColor(bckgrdclr)

    }

    private fun setInteralListeners() {
        setOnClickListenerInternal()
        setOnTextChangedListenerInternal()
        setOnSearchListenerInternal()
        setOnViewCollapsedExpandedListenerInternal()
    }

    private fun setOnClickListenerInternal() {
        viewBinding.searchviewIconSearchContainer.setOnClickListener {
            val stateSet = arrayOf(android.R.attr.state_checked * if (!isViewExpanded) 1 else -1)
            viewBinding.searchviewIconSearch.setImageState(stateSet.toIntArray(), true)

            if (isViewExpanded) {
                //Transition to collapsed state
                viewBinding.searchviewMlayout.setTransition(
                    R.id.constraint_set_searchview_expanded,
                    R.id.constraint_set_searchview_collapsed
                )

            }
            else {
                //Transition to expanded state
                viewBinding.searchviewMlayout.setTransition(
                    R.id.constraint_set_searchview_collapsed,
                    R.id.constraint_set_searchview_expanded
                )
                viewBinding.searchviewTextInput.isEnabled = true
            }

            viewBinding.searchviewMlayout.transitionToEnd()
            isViewExpanded = !isViewExpanded
        }

        viewBinding.searchviewEraseTextInputContainer.setOnClickListener {
            inputText = ""
        }
    }

    private fun setOnSearchListenerInternal () {
        viewBinding.searchviewTextInput.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchListener?.onSearch(textView.text.toString())
                hideKeyboard()
                  true
            } else
                false
        }
    }


    private fun showKeyboard() {
        viewBinding.searchviewTextInput.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(viewBinding.searchviewTextInput,InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        this.clearFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken,0)

    }

    private fun setOnTextChangedListenerInternal() {
        viewBinding.searchviewTextInput.addTextChangedListener {
            onTextChangeListener?.afterTextChanged(it)
            onTextChangeListener_BINDING_ONLY?.afterTextChanged(it)
        }
    }



    /**
     * Callback when this [SearchView] finished expanding ([true]) or collapsing ([false])
     */
    private fun setOnViewCollapsedExpandedListenerInternal() {
        viewBinding.searchviewMlayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (p1 == R.id.constraint_set_searchview_expanded) {
                    onViewCollapseListener?.onViewState(false)

                    showKeyboard()
                }
                else if (p1 == R.id.constraint_set_searchview_collapsed) {
                    onViewCollapseListener?.onViewState(true)
                    viewBinding.searchviewTextInput.isEnabled = false
                    hideKeyboard()

                }
            }
        })
    }




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
    onTextChangeListener_BINDING_ONLY = TextViewBindingAdapter.AfterTextChanged{
        attrChange.onChange()
        listener?.afterTextChanged(it)
    }
}

@BindingAdapter("onSearch")
fun SearchView.setOnSearchListenerBinding( listener : SearchView.OnSearchListener?) =
    listener?.let { onSearchListener = it }


@BindingAdapter("onViewCollapseExpand")
fun SearchView.setOnViewCollapseExpandBinding( listener : SearchView.OnViewCollapseExpand?) =
    listener?.let { onViewCollapseListener = it }

