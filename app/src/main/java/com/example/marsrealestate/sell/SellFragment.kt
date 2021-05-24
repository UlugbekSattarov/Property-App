package com.example.marsrealestate.sell

import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.query.MarsApiSorting
import com.example.marsrealestate.databinding.FragmentSellBinding
import com.example.marsrealestate.util.resolveColor
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SellFragment : Fragment() {



    private val viewModel : SellViewModel by viewModels {
        SellViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding : FragmentSellBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =  FragmentSellBinding.inflate(inflater)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)

        val rentalOrPurchase = listOf(resources.getString(R.string.rent),resources.getString(R.string.buy))
        val adapterMonths = ArrayAdapter(requireContext(),R.layout.view_sorting_option_item,rentalOrPurchase)
        viewDataBinding.sellOrRentInputValue.setAdapter(adapterMonths)


        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        viewDataBinding.areaInputValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                scrollToPutOnSaleButton(600)
            false
        }

        return viewDataBinding.root
    }

    private fun scrollToPutOnSaleButton(delay : Long) {
        lifecycleScope.launch {
            delay(delay)
            viewDataBinding
                .fragmentSellScrollview
                .smoothScrollTo(0, viewDataBinding.buttonPutOnSale.y.toInt(), 1000)
        }
    }


    private fun makeNavigationBarColored() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            requireActivity().window.navigationBarColor = requireContext().resolveColor(R.attr.backgroundColor)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    requireActivity().window.navigationBarDividerColor = requireContext().resolveColor(android.R.attr.listDivider)
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

    //We have to reset the adapter each time because only the selected value is kept otherwise,not the whole list (bug)
//    val sortingOptions = listOf(context.getString(R.string.defaultSorting),context.getString(R.string.priceAscending),context.getString(
//        R.string.priceDescending))
//    val adapterSorting = ArrayAdapter(context, R.layout.view_sorting_option_item, sortingOptions)
//    setAdapter(adapterSorting)
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