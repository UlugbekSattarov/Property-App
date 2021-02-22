package com.example.marsrealestate.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentDetailBinding
import com.example.marsrealestate.login.LoginViewModel
import com.example.marsrealestate.login.LoginViewModelFactory
import com.example.marsrealestate.util.*
import com.google.android.material.transition.MaterialContainerTransform
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.max

class DetailFragment : Fragment() {

//    private val args  by navArgs<DetailFragmentArgs>()

    //Useful when you want to start the app with this fragment for preview purpose
    private val property by lazy {
        try {
            DetailFragmentArgs.fromBundle(requireArguments()).marsProperty
        }
        catch (e: Exception) {
            Log.i("$this",e.toString())
            MarsProperty("0000", "", "rent", 123456.0)
        }
    }


    private val viewModel : DetailViewModel by viewModels {
        DetailViewModelFactory(property,(activity as MainActivity).marsRepository)
    }

    private val loginViewModel : LoginViewModel by activityViewModels {
        LoginViewModelFactory()
    }

    private lateinit var viewDataBinding : FragmentDetailBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentDetailBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        //Handling the shared element transition
        val transitionName = SharedElementTransition.getTransitionName(property)
        setupSharedElementTransition(transitionName)
        loadSharedImageBeforeEnterTransition(property.imgSrcUrl,viewDataBinding.imageToolbar)
        lifecycleScope.launchWhenResumed { animOnResume() }





        setupViewPager()


        //Functional
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()

        //Databinding does not work for this view
        viewDataBinding.extendedFab.text = getString(if (property.isRental) R.string.rent else R.string.buy)

        return viewDataBinding.root
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
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
//        sharedElementReturnTransition = sharedElementEnterTransition
    }


    private fun setupNavigation() {
        viewModel.navigateToPayment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { property ->

                val action = if (loginViewModel.isLoggedIn().not())
                    DetailFragmentDirections.actionDestDetailToDestLogin().apply {
                        redirection = R.id.dest_choose_payment
                        redirectionArgs = property.id
                    }
                else
                    DetailFragmentDirections.actionDestDetailToDestChoosePayment(property.id)

                findNavController().navigate(action)
            }
        }
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
            .setDuration(1800)
            .start()
    }


    private fun setupViewPager() {
        viewDataBinding.viewpager.apply {
            adapter = DetailViewPagerAdapter(property)
            setPageTransformer(DetailViewPagerPageTransformer())
            offscreenPageLimit = 3

            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
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
            })
        }
    }


}

