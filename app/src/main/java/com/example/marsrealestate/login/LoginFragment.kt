package com.example.marsrealestate.login

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentLoginBinding
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private val navArgs by navArgs<LoginFragmentArgs>()


    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(ServiceLocator.getMarsRepository(requireContext()),this,null)
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
    ): View {

        viewDataBinding = FragmentLoginBinding.inflate(inflater)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()
        setupLoginLayoutVisibility()


        return viewDataBinding.root
    }



    private fun setupNavigation() {
        viewModel.loggedInEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {

                //Transition is handled by the nav controller (defined in nav_graph_main.xml)
                exitTransition = null

                if (navArgs.redirection == R.id.dest_choose_payment ) {

                    navArgs.redirectionArgs?.let { args ->
                        val direction = LoginFragmentDirections.actionDestLoginToDestChoosePayment(args)
                        findNavController().navigate(direction)
                    }
                }
            }
        })
    }

    /**
     * Setup a listener on [viewModel.isLoggedIn] to switch the layout between layoutLogin and layoutLogout.
     * Takes care of the transition in between.
     */
    private fun setupLoginLayoutVisibility() {
        val delayFadeIn = 500L
        val delayCheckMarkIsDrawn = 1100L

        //The transition that will be applied when layouts are switched, the check mark transition is done
        //a few lines later
        val transition = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.MODE_IN).apply { startDelay = delayFadeIn; duration = 500 })
            addTransition(Fade(Fade.MODE_OUT))
            interpolator = FastOutSlowInInterpolator()

        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->

            //This will play in parallel of the transition, but the delay will make it happen after
            lifecycleScope.launch {
                delay(delayCheckMarkIsDrawn)

                //This will play the checkmark animation after the delay
                //This is a complicated way but we have to remove entirely the drawable in order to
                //reset the animation on API < 23  (reset() is available only for API 23+)
                viewDataBinding.loggedInCheck.apply {
                    if (loggedIn) {
                        setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.an_check, requireContext().theme))
                        (this.drawable as? AnimatedVectorDrawable)?.start()
                    }
                    else
                        viewDataBinding.loggedInCheck.setImageDrawable(null)
                }
            }

            //Actually starting the transition
            TransitionManager.beginDelayedTransition(viewDataBinding.loginCardview, transition)
             viewDataBinding.layoutLogin.visibility = if (loggedIn) View.GONE else  View.VISIBLE
             viewDataBinding.layoutLogout.visibility = if (loggedIn) View.VISIBLE else  View.GONE

        })
    }


}



