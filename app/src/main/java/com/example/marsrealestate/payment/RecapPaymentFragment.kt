package com.example.marsrealestate.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentPaymentRecapBinding
import com.example.marsrealestate.util.helpers.NotificationHelper
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent

class RecapPaymentFragment : Fragment() {

    private val args by navArgs<RecapPaymentFragmentArgs>()

    private val viewModel : RecapPaymentViewModel by viewModels {
        RecapPaymentViewModelFactory( args.propertyToBuyId,
            args.paymentOption,
            ServiceLocator.getMarsRepository(requireContext()))
    }


    private val viewDataBinding: FragmentPaymentRecapBinding by lazy { FragmentPaymentRecapBinding.inflate(layoutInflater) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()


        return viewDataBinding.root
    }


    private fun setupNavigation() {

        viewModel.transactionCompleted.observe(viewLifecycleOwner,  {
            it.getContentIfNotHandled()?.let { property ->
                NotificationHelper.notifyPropertyBought(requireContext(),property)
            }
        })


        viewModel.navigateToHome.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {

                findNavController().apply {
                    popBackStack(R.id.nav_graph_main, false)
                    navigate(R.id.dest_overview)
                }
            }
        })
    }


}

