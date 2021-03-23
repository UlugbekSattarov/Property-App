package com.example.marsrealestate.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentNavigationBinding
import com.example.marsrealestate.login.LoginViewModel
import com.example.marsrealestate.login.LoginViewModelFactory
import com.example.marsrealestate.util.closeDrawerIfPresent


class NavigationFragment : Fragment() {


    private lateinit var viewDataBinding : FragmentNavigationBinding


    private val viewModel : NavigationViewModel by viewModels {
        NavigationViewModelFactory(R.id.dest_overview,(requireActivity() as MainActivity).marsRepository)
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentNavigationBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.loginViewModel = loginViewModel
        viewDataBinding.fragment = this
        viewDataBinding.lifecycleOwner = viewLifecycleOwner




        // Inflate the layout for this fragment
        return viewDataBinding.root
    }




     fun changeDestination(@IdRes navDestinationId : Int) {

        val navController = findNavController()


        requireActivity().closeDrawerIfPresent{

            val opt = NavOptions.Builder()
//                .setExitAnim(android.R.anim.fade_out)
//                .setPopExitAnim(android.R.anim.fade_out)
//                .setEnterAnim(android.R.anim.fade_in)
//                .setPopEnterAnim(android.R.anim.fade_in)
//                .setPopUpTo(navDestinationId, false)
                .build()

            navController.popBackStack(R.id.nav_graph_main,false)
            navController.navigate(navDestinationId,null,opt)
            viewModel.setCurrentDestination(navDestinationId)
        }

    }


}