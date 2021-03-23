package com.example.marsrealestate.login

import android.graphics.Rect
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentLoginBinding
import com.example.marsrealestate.overview.OverviewFragmentDirections
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private val navArgs by navArgs<LoginFragmentArgs>()


    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = FragmentLoginBinding.inflate(inflater)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()

        return viewDataBinding.root
    }



    private fun setupNavigation() {
        viewModel.loggedInEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {

                if (navArgs.redirection == R.id.dest_choose_payment ) {
                    val opt = NavOptions.Builder().setPopUpTo(R.id.dest_login,true).build()
                    navArgs.redirectionArgs?.let { args ->
                        val direction = LoginFragmentDirections.actionDestLoginToDestChoosePayment(args)
                        findNavController().navigate(direction,opt)
                    }
                }
            }
        })
    }


}



