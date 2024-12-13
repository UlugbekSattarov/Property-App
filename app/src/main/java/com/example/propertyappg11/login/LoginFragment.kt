package com.example.propertyappg11.login

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.propertyappg11.R
import com.example.propertyappg11.ServiceLocator
import com.example.propertyappg11.databinding.FragmentLoginBinding
import com.example.propertyappg11.util.helpers.BiometricHelper
import com.example.propertyappg11.util.helpers.PreferencesHelper
import com.example.propertyappg11.util.hideSoftInput
import com.example.propertyappg11.util.setupFadeThroughTransition
import com.example.propertyappg11.util.setupToolbarIfDrawerLayoutPresent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val navArgs by navArgs<LoginFragmentArgs>()


    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(
            ServiceLocator.getMarsRepository(requireContext()),
            CredentialsManagerImpl(requireContext()),
            this,null)
    }

    private lateinit var viewDataBinding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentLoginBinding.inflate(inflater)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        setupFadeThroughTransition(viewDataBinding.root)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()
        setupSwitchLoginLayoutListener()
        setupCloseKeyboardOnLoading()
        setupBiometricLoginListener()


//        viewModel.email.value = PreferencesHelper.getEmail(requireContext())


        return viewDataBinding.root
    }



    private fun setupNavigation() {

        viewModel.loggedInEvent.observe(viewLifecycleOwner,  {
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

        viewModel.navigateToOverviewEvent.observe(viewLifecycleOwner,  {
            it.getContentIfNotHandled()?.let {
                findNavController().apply {
                    popBackStack(R.id.nav_graph_main,false)
                    navigate(R.id.dest_overview)
                }
            }
        })
    }

    /**
     * Setup a listener on viewModel.isLoggedIn to switch the layout between layoutLogin and layoutLogout.
     * Takes care of the transition in between which is Fade out + Height -> Fade In -> CheckMark animation is played
     */

    private fun setupSwitchLoginLayoutListener() {
        val delayFadeIn = 500L
        val delayCheckMarkAnimation = 1100L

        //The transition that will be applied when layouts are switched, the check mark transition is done
        //a few lines after
        val transition = TransitionSet().apply {
            addTransition(ChangeBounds()) //For the height transition
            addTransition(Fade(Fade.MODE_IN).apply { startDelay = delayFadeIn; duration = 500 })
            addTransition(Fade(Fade.MODE_OUT))
            interpolator = FastOutSlowInInterpolator()
        }

        //Setup the listener
        viewModel.isLoggedIn.observe(viewLifecycleOwner,  { loggedIn ->

            //Actually starting the transition
            TransitionManager.beginDelayedTransition(viewDataBinding.loginCardview, transition)
             viewDataBinding.layoutLogin.visibility = if (loggedIn) View.GONE else  View.VISIBLE
             viewDataBinding.layoutLogout.visibility = if (loggedIn) View.VISIBLE else  View.GONE

            playOrResetCheckMarkAnimation(delayCheckMarkAnimation,loggedIn)

        })
    }

    private fun setupCloseKeyboardOnLoading() {
        viewModel.operationLogging.observe(viewLifecycleOwner, {
            if (it.isLoading())
                hideSoftInput()
        })
    }

    @Suppress("SameParameterValue")
    private fun playOrResetCheckMarkAnimation(delay : Long, play : Boolean) {
        lifecycleScope.launch {
            delay(delay)

            //This will play the checkmark animation after the delay
            //This is a complicated way but we have to remove entirely the drawable in order to
            //reset the animation on API < 23  (reset() is available only for API 23+)
            viewDataBinding.loggedInCheck.apply {
                if (play) {
                    setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.an_check, requireContext().theme))
                    (this.drawable as? AnimatedVectorDrawable)?.start()
                }
                else {
                    //Animation is reset by setting drawable to null
                    viewDataBinding.loggedInCheck.setImageDrawable(null)
                }
            }
        }
    }


    private fun setupBiometricLoginListener() {

        //The callback when the user uses the biometric prompt
        val callback =
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
//                    Snackbar.make(viewDataBinding.root,"Authentication error: $errString",Snackbar.LENGTH_SHORT)
                    Log.d("${LoginFragment::class.simpleName}","Biometric onAuthenticationError $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fillLoginAndAuthenticate()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    Snackbar.make(viewDataBinding.root,"Authentication failed",Snackbar.LENGTH_SHORT)
                    Log.d("${LoginFragment::class.simpleName}","Biometric onAuthenticationFailed")

                }
            }




            viewDataBinding.loginBiometricButton.setOnClickListener {
                BiometricHelper.showBiometricPrompt(this,callback)
        }
    }

    private fun fillLoginAndAuthenticate() {
        viewModel.email.value = "user@marsrealestate.com"
        viewModel.password.value = "password"
        viewModel.login()

    }



}

data class Credentials(val login: String,val password : String)

interface CredentialsManager {

    fun getSavedCredentials() : Credentials?
    fun saveCredentials(credentials: Credentials)
    fun deleteCredentials()
}

class CredentialsManagerImpl(private val context : Context) : CredentialsManager {

    override fun getSavedCredentials(): Credentials? =
        PreferencesHelper.getCredentials(context)

    override fun saveCredentials(credentials: Credentials) =
        PreferencesHelper.setCredentials(context,credentials)

    override fun deleteCredentials() =
        PreferencesHelper.deleteCredentials(context)
}
