package com.example.marsrealestate.sell

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentSellBinding
import com.example.marsrealestate.util.resolveColor
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SellFragment : Fragment() {



    private val viewModel : SellViewModel by viewModels {
        SellViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding : FragmentSellBinding


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enterTransition = MaterialFadeThrough()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =  FragmentSellBinding.inflate(inflater)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setupScrollAfterAreaInput()
        setupPropertyTypeAdapter()

        return viewDataBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupPropertyTypeAdapter() {
        val types = listOf(resources.getString(R.string.rent),resources.getString(R.string.buy))
        val adapterType = ArrayAdapter(requireContext(),R.layout.view_sorting_option_item,types)
        viewDataBinding.sellOrRentInputValue.setAdapter(adapterType)
    }

    /**
     * Scroll to the Button putOnSale after the surface are has been given by the user.
     * This is useful to make sure the button is seen.
     */
    private fun setupScrollAfterAreaInput() =
        viewDataBinding.areaInputValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                scrollTo(viewDataBinding.buttonPutOnSale,600)
            false
        }


    @Suppress("SameParameterValue")
    private fun scrollTo(destination : View,delay : Long) {
        lifecycleScope.launch {
            delay(delay)
            viewDataBinding
                .fragmentSellScrollview
                .smoothScrollTo(0, destination.y.toInt(), 1000)
        }
    }




}


@BindingAdapter("propertyType")
fun AutoCompleteTextView.setPropertyType(oldValue: String?, newValue: String?) {
    if (newValue != null && newValue != oldValue) {
        val toDisplay = when (newValue) {
            MarsProperty.TYPE_BUY -> R.string.buy
            MarsProperty.TYPE_RENT -> R.string.rent
            else -> R.string.error
        }
        this.setText(context.getString(toDisplay),false)
    }
}


@InverseBindingAdapter(attribute = "propertyType")
fun AutoCompleteTextView.getPropertyType(): String {
    return when (text.toString()) {
        context.getString(R.string.rent) -> MarsProperty.TYPE_RENT
        context.getString(R.string.buy) -> MarsProperty.TYPE_BUY
        else -> ""
    }
}

@BindingAdapter("app:propertyTypeAttrChanged")
fun AutoCompleteTextView.setPropertyTypeListeners(
    attrChange: InverseBindingListener
) {
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, _, _ ->
            attrChange.onChange()
        }
}