package com.example.marsrealestate.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentPaymentChooseBinding
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialSharedAxis


class ChoosePaymentFragment : Fragment() {

    private val args by navArgs<ChoosePaymentFragmentArgs>()

//    private val cartViewModel: CartViewModel by navGraphViewModels(R.id.nav_graph_payment) {
//        CartViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
//    }
    /**
     * It can't be instantiated here directly because it would cause errors with the test environment.
     * It is created in [onViewCreated]
     */
    private lateinit var cartViewModel : CartViewModel


    private val viewModel: ChoosePaymentViewModel by viewModels {
        ChoosePaymentViewModelFactory()
    }

    private lateinit var viewDataBinding: FragmentPaymentChooseBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cartViewModel = navGraphViewModels<CartViewModel>(R.id.nav_graph_payment) {
            CartViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
        }.value

        args.propertyToBuyId?.let { id ->  cartViewModel.addPropertyToBuy(id) }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentPaymentChooseBinding.inflate(inflater)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)

        setupNavigation()


        return viewDataBinding.root
    }

    private fun setupNavigation() {
        viewModel.navigateToVisaPayment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                val action =
                    ChoosePaymentFragmentDirections.actionDestChoosePaymentToDestPaymentVisa()
                findNavController().navigate(action)
            }
        })
    }
}
