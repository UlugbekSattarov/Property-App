package com.example.marsrealestate.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.databinding.FragmentFavoritesBinding
import com.example.marsrealestate.util.helpers.SharedElementTransitionHelper
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough


class FavoritesFragment : Fragment() {

    val viewModel : FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding : FragmentFavoritesBinding

    private var enableListAppearingAnimation = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = null

        // Inflate the layout for this fragment
        viewDataBinding = FragmentFavoritesBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner


        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupRecyclerView()
        setupNavigation()
        setupOnPropertyRemoved()

        //For shared element transition
        postponeEnterTransition()
        viewDataBinding.root.doOnPreDraw { startPostponedEnterTransition() }
        return viewDataBinding.root
    }



    private fun setupRecyclerView() {

        viewDataBinding.favoritesList.adapter = FavoritesAdapter(FavoritesAdapter.OnClickListener { favorite ->
            viewModel.displayPropertyDetails(favorite)
        })


        //For the animation on the first appearance of the items
        viewModel.favorites.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                playListAnimationOnlyOnce()
            }
        })
    }

    private fun playListAnimationOnlyOnce() {
        if (enableListAppearingAnimation) {
            enableListAppearingAnimation = false
            viewDataBinding.favoritesList.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_appearing)
            viewDataBinding.favoritesList.scheduleLayoutAnimation()
        }
    }


    private fun setupOnPropertyRemoved() {
        viewModel.propertyRemoved.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess()) {
                Snackbar.make(viewDataBinding.root,"Property removed",Snackbar.LENGTH_LONG).apply {
                    setAction(android.R.string.cancel) {
                        viewModel.recoverLastDeletedProperty()
                    }
                }.show()
            }
        })
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let navigation@{ fav ->
                val action = FavoritesFragmentDirections.actionFavoritesToDetail().apply { marsProperty = fav.property }

                SharedElementTransitionHelper.navigate(this,fav.property,action)
            }
        })
    }

}
