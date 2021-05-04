package com.example.marsrealestate.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentDetailBinding
import com.example.marsrealestate.login.LoginViewModel
import com.example.marsrealestate.login.LoginViewModelFactory
import com.example.marsrealestate.util.SharedElementTransition
import com.example.marsrealestate.util.doOnEnd
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialContainerTransform
import kotlin.math.abs

class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()


    private val viewModel : DetailViewModel by viewModels {
        //Choose whether we should retrieve the factory with a MarsProperty or propertyID depending on the arguments
        val prop = args.marsProperty
        if (prop != null )
            DetailViewModelFactory(prop ,ServiceLocator.getMarsRepository(requireContext()))
        else
            DetailViewModelFactory(args.propertyId ,ServiceLocator.getMarsRepository(requireContext()))

    }

    private val loginViewModel : LoginViewModel by activityViewModels {
        LoginViewModelFactory(ServiceLocator.getMarsRepository(requireContext()),this,null)
    }

    private lateinit var viewDataBinding : FragmentDetailBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentDetailBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        handleSharedElementTransition()
        lifecycleScope.launchWhenResumed { animOnResume() }

        setupViewPagerListener()

        //Functional
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()

        //Databinding does not work for this view
//        viewDataBinding.extendedFab.text = getString(if (args.marsProperty?.isRental == true) R.string.rent else R.string.buy)

        viewModel.property.observe(viewLifecycleOwner, { p ->
                viewDataBinding.extendedFab.setText(if (p.isRental) R.string.rent else R.string.buy)
//                viewDataBinding.executePendingBindings()
            }
        )

        return viewDataBinding.root
    }



    private fun handleSharedElementTransition() {
        //if no property was given in args, then no shared element transition is possible
        val property = args.marsProperty ?: return

        val transitionName = SharedElementTransition.getTransitionName(property)
        setupSharedElementTransition(transitionName)
        loadSharedImageBeforeEnterTransition(property.imgSrcUrl,viewDataBinding.imageToolbar)
    }





    private fun loadSharedImageBeforeEnterTransition(sourceUrl : String,destination : ImageView) {
        postponeEnterTransition()

        val id = sourceUrl.toIntOrNull()
        if (id != null) {
            destination.setImageDrawable(ResourcesCompat.getDrawable(resources, id, destination.context.theme))
            startPostponedEnterTransition()
        }
        else {
            val imgUri = sourceUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(destination)
                .load(imgUri)
                .dontTransform()
                .doOnEnd { startPostponedEnterTransition() }
                .into(destination)
        }
    }

    private fun setupSharedElementTransition(transitionName: String) {
        ViewCompat.setTransitionName(viewDataBinding.root,transitionName)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            //            scrimColor = Color.TRANSPARENT
//            duration = 5000
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
//        sharedElementReturnTransition = sharedElementEnterTransition
    }


    private fun setupNavigation() {
        viewModel.navigateToPayment.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { property ->

                val action = if (loginViewModel.isLoggedIn.value != true)
                    DetailFragmentDirections.actionDestDetailToDestLogin().apply {
                        redirection = R.id.dest_choose_payment
                        redirectionArgs = property.id
                    }
                else
                    DetailFragmentDirections.actionDestDetailToDestChoosePayment(property.id)

                findNavController().navigate(action)
            }
        })
    }


    private fun animOnResume() {
        val fab = viewDataBinding.fab
        val toolbarScrim = viewDataBinding.toolbarScrim

        fab.animate().scaleX(1f).scaleY(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(200)
            .setStartDelay(500)
            .start()
        toolbarScrim.animate().alpha(1f)
            .setStartDelay(300)
            .setDuration(900)
            .start()
    }


    /**
     * Initialize the viewpager when a property is available from the viewmodel
     */
    private fun setupViewPagerListener() {

        viewModel.property.observe(viewLifecycleOwner, Observer { property ->
            viewDataBinding.viewpager.apply {
                adapter = DetailViewPagerAdapter(property)
                setPageTransformer(DetailViewPagerPageTransformer())
                offscreenPageLimit = 3

                registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        //only used for visual purpose of the caption
                        onViewpagerPageSelected(position)
                    }
                })
            }
        })
    }

    /**
     * Fade in and out the caption below the viewpager when a new page is shown
     */
    private fun onViewpagerPageSelected(position : Int){
        viewDataBinding.viewpagerCaption.animate().alpha(0f).withEndAction {
            val orientation = when (position) {
                0 -> "South"
                1 -> "East"
                2 -> "North"
                else -> "West"
            }
            viewDataBinding.viewpagerCaption.text = "View from $orientation"
            viewDataBinding.viewpagerCaption.animate().alpha(1f).start()
        }.start()
    }



    fun setupSharePropertyListener() {

        viewModel.shareProperty.observe(viewLifecycleOwner, Observer {
//                it.getContentIfNotHandled()?.let { prop ->
//                    val deeplink = findNavController().createDeepLink()
//                        .setArguments(bundleOf("MarsProperty" to property))
//                        .setGraph(R.navigation.nav_graph_main)
//                        .setDestination(R.id.dest_detail)
//                        .createPendingIntent()
//                        .
//                }
        })
    }

}

object MarsCoordsToStringConverter {

    private fun formatLatitudeToString(value: Float): String {
        val lat = String.format("%.1f", abs(value))
        return if (value > 0) "$lat° N" else "$lat° S"

    }

    private fun formatLongitudeToString(value: Float): String  = String.format("%.1f° E",value)

    @JvmStatic
    fun formatCoordsToString(prop: MarsProperty): String  = "${formatLatitudeToString(prop.latitude)}\n${formatLongitudeToString(prop.longitude)}"

}

