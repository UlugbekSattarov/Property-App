package com.example.marsrealestate.sell

import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
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