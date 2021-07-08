package com.example.marsrealestate.sell.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentSellCompletedBinding
import com.example.marsrealestate.detail.DetailFragmentArgs
import com.example.marsrealestate.util.setupFadeThroughTransition

class SellCompletedFragment : Fragment() {


    private val args by navArgs<SellCompletedFragmentArgs>()

    private val viewModel : SellCompletedViewModel by viewModels {
        SellCompletedViewModelFactory(args.propertyId)
    }

    private lateinit var viewDataBinding : FragmentSellCompletedBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =  FragmentSellCompletedBinding.inflate(inflater)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel

        setupFadeThroughTransition(viewDataBinding.root)
        setupNavigation()


        return viewDataBinding.root
    }





    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { propId ->
                navigateToDetail(propId)
            }
        }

        viewModel.navigateToOverview.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                navigateToOverview()
            }
        }
    }


    private fun navigateToDetail(propertyId : String) =
        findNavController().run {
            popBackStack(R.id.nav_graph_main, false)
            navigate(R.id.dest_detail, DetailFragmentArgs.Builder().setPropertyId(propertyId).build().toBundle())
        }

    private fun navigateToOverview() =
        findNavController().run {
            popBackStack(R.id.nav_graph_main, false)
            navigate(R.id.dest_overview)
        }

}

