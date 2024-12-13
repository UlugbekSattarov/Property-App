package com.example.propertyappg11.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.propertyappg11.R
import com.example.propertyappg11.ServiceLocator
import com.example.propertyappg11.databinding.FragmentNavigationBinding
import com.example.propertyappg11.login.CredentialsManagerImpl
import com.example.propertyappg11.login.LoginViewModel
import com.example.propertyappg11.login.LoginViewModelFactory
import com.example.propertyappg11.util.closeDrawerIfPresent


class NavigationFragment : Fragment() {


    private lateinit var viewDataBinding : FragmentNavigationBinding


    private val viewModel : NavigationViewModel by viewModels {
        NavigationViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(ServiceLocator.getMarsRepository(requireContext()),
            CredentialsManagerImpl(requireContext()),
            this,null)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentNavigationBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.loginViewModel = loginViewModel
        viewDataBinding.fragment = this
        viewDataBinding.lifecycleOwner = viewLifecycleOwner


        requireActivity().findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener { _, destination, _ ->
                viewModel.setCurrentDestination(destination.id)
            }



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
        }

    }


}
