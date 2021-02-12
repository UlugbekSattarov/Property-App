package com.example.marsrealestate.payment

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentPaymentRecapBinding
import com.example.marsrealestate.databinding.FragmentPaymentVisaBinding
import com.example.marsrealestate.payment.options.PaymentOption
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import java.lang.StringBuilder

class RecapPaymentFragment : Fragment() {

    private val args by navArgs<RecapPaymentFragmentArgs>()


    private val viewModel : RecapPaymentViewModel by viewModels {
        RecapPaymentViewModelFactory( args.propertyToBuyId,
            args.paymentOption,
            ServiceLocator.getMarsRepository(requireContext()))
    }


    private lateinit var viewDataBinding: FragmentPaymentRecapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentPaymentRecapBinding.inflate(inflater)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()

        return viewDataBinding.root
    }


    private fun setupNavigation() {
        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { c ->

                val opt = NavOptions.Builder()
                    .setExitAnim(R.anim.fragment_fade_exit)
                    .setPopExitAnim(R.anim.fragment_fade_exit)
                    .setEnterAnim(R.anim.fragment_fade_enter)
                    .setPopEnterAnim(R.anim.fragment_fade_enter)
                    .setPopUpTo(R.id.dest_blank,
                        false)
                    .build()

                findNavController().navigate(R.id.dest_overview,null,opt)
            }
        }
    }

}

