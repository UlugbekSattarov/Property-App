package com.example.marsrealestate.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.get
import androidx.viewpager2.widget.ViewPager2
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentDetailBinding
import com.example.marsrealestate.login.LoginViewModel
import com.example.marsrealestate.login.LoginViewModelFactory
import com.example.marsrealestate.util.SharedElementTransition
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import kotlin.math.abs

class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()


    private val viewModel : DetailViewModel by viewModels {
        //Choose whether we should retrieve the factory with a MarsProperty or propertyID depending on the arguments
        val prop = args.marsProperty
        val id = args.propertyId
        if (prop != null )
            DetailViewModelFactory(prop ,ServiceLocator.getMarsRepository(requireContext()))
        else
            DetailViewModelFactory(id!! ,ServiceLocator.getMarsRepository(requireContext()))

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


        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupNavigation()
        setupViewPagerListener()
        setupSharePropertyListener()


        loadToolbarImage()
//        setupSharedElementTransition()
        SharedElementTransition.setupReceiverFragment(this,args.marsProperty,viewDataBinding.root)
        lifecycleScope.launchWhenResumed { animateFab(); animateToolbarScrim() }

        return viewDataBinding.root
    }


    /**
     * Load the image of the toolbar : if a property is given by the [args], set the image directly. If not,
     * wait for the [viewModel] to emit a property and then set the image
     */
    private fun loadToolbarImage() {

        val drawableId = args.marsProperty?.imgSrcUrl?.toIntOrNull()
        val toolbar = viewDataBinding.imageToolbar

        if (drawableId != null) {
            toolbar.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, toolbar.context.theme))
        }
        else {
            viewModel.property.observe(viewLifecycleOwner, {
                toolbar.setImageDrawable(ResourcesCompat.getDrawable(resources, it.imgSrcUrl.toIntOrNull() ?: R.drawable.mars_landscape_1, toolbar.context.theme))
            })
        }
    }


    @Deprecated("Postponing the enter transition to wait for the image to be loaded is " +
            "not optimal for the user",
        replaceWith = ReplaceWith("loadToolbarImage(int)"))
    private fun loadSharedImageBeforeEnterTransition(sourceUrl : String,destination : ImageView) {
        /* postponeEnterTransition()

         val id = sourceUrl.toIntOrNull()
         if (id != null) {
             loadToolbarImage()
             startPostponedEnterTransition()
         }
         else {
             val imgUri = sourceUrl.toUri().buildUpon().scheme("https").build()
             Glide.with(destination)
                 .load(imgUri)
                 .dontTransform()
                 .doOnEnd { startPostponedEnterTransition() }
                 .into(destination)
         }*/
    }


    private fun setupNavigation() {
        viewModel.navigateToPayment.observe(viewLifecycleOwner,  {
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


    private fun animateFab() =
        viewDataBinding.fab.animate().scaleX(1f).scaleY(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(200)
            .setStartDelay(900)
            .start()


    private fun animateToolbarScrim() =
        viewDataBinding.toolbarScrim.animate().alpha(1f)
            .setStartDelay(200)
            .setDuration(600)
            .start()



    /**
     * Initialize the viewpager when a property is available from the viewmodel
     */
    private fun setupViewPagerListener() =
        viewModel.property.observe(viewLifecycleOwner,  { property ->
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


    /**
     * Fade in and out the caption below the viewpager when a new page is shown
     */
    private fun onViewpagerPageSelected(position : Int) {
        val orientation = when (position) {
            0 -> "South"
            1 -> "East"
            2 -> "North"
            else -> "West"
        }
        val captionText = "View from $orientation"

        viewDataBinding.viewpagerCaption.apply {


            //If the text is null, we do not want to animate the transition
            val duration = if (text.isNullOrEmpty()) 0L else 300L

            //Fade out old text and fade in new text
            animate().alpha(0f).setDuration(duration).withEndAction {
                text = captionText
                animate().alpha(1f).setDuration(duration).start()
            }.start()

        }
    }



    private fun setupSharePropertyListener() =
        viewModel.shareProperty.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { prop ->
                val uri = Uri.parse("https://com.example.marsrealestate/detail/${prop.id}")
                if (findNavController().graph[R.id.dest_detail].hasDeepLink(uri)) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "I just bought a property, check it out! $uri")
                        // (Optional) Here we're setting the title of the content
                        putExtra(Intent.EXTRA_TITLE, "Mars property ${prop.id}")
//                        data = Uri.parse("android.resource://com.example.marsrealestate/drawable/astronaut.jpg")
//                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(sendIntent,"Share property"))
                }
            }
        })


}

object MarsCoordsToStringConverter {

    private fun formatLatitudeToString(value: Float): String {
        val lat = String.format("%.1f", abs(value))
        return if (value > 0) "$lat° N" else "$lat° S"

    }

    private fun formatLongitudeToString(value: Float): String  = String.format("%.1f° E",value)

    @JvmStatic
    fun formatCoordsToString(prop: MarsProperty?): String  = "${formatLatitudeToString(prop?.latitude ?: 0f)}\n${formatLongitudeToString(prop?.longitude ?: 0f)}"

}

