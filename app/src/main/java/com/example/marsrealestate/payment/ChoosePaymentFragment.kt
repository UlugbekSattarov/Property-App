package com.example.marsrealestate.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentPaymentChooseBinding
import com.example.marsrealestate.detail.DetailFragmentArgs
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent


class ChoosePaymentFragment : Fragment() {

    private val args by navArgs<ChoosePaymentFragmentArgs>()

    private val cartViewModel: CartViewModel by navGraphViewModels(R.id.graph_payment) {
        CartViewModelFactory((activity as MainActivity).marsRepository)
    }

    private val viewModel: ChoosePaymentViewModel by viewModels {
        ChoosePaymentViewModelFactory()
    }

    private lateinit var viewDataBinding: FragmentPaymentChooseBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.propertyToBuyId?.let { cartViewModel.addPropertyToBuy(it)}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = FragmentPaymentChooseBinding.inflate(inflater)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)

        setupNavigation()


        return viewDataBinding.root
    }

    private fun setupNavigation() {
        viewModel.navigateToVisaPayment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { c ->
                val action =
                    ChoosePaymentFragmentDirections.actionDestChoosePaymentToDestPaymentVisa()
                findNavController().navigate(action)
            }
        }
    }
}
