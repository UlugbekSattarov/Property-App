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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentFavoritesBinding
import com.example.marsrealestate.util.SharedElementTransition
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold


class FavoritesFragment : Fragment() {

    val viewModel : FavoritesViewModel by viewModels {
        FavoritesViewModelFactory((activity as MainActivity).marsRepository)
    }

    private lateinit var viewDataBinding : FragmentFavoritesBinding

    private var enableListAppearingAnimation = true

    //Useful for shared element transition
    private var selectedProperty : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        viewDataBinding.favoritesList.adapter = FavoritesAdapter(FavoritesAdapter.OnClickListener { favorite, viewClicked ->
            this.selectedProperty = viewClicked
            viewModel.displayPropertyDetails(favorite.property)
        })


        //For the animation on the first appearance of the items
        viewModel.favorites.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                playListAnimationOnlyOnce()
            }
        }
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
        viewModel.propertyRemoved.observe(viewLifecycleOwner) {
            if (it.isSuccess()) {
                Snackbar.make(viewDataBinding.root,"Property removed",Snackbar.LENGTH_LONG).apply {
                    setAction(android.R.string.cancel) {
                        viewModel.recoverLastDeletedProperty()
                    }.show()
                }
            }
        }
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let navigation@{ property ->
                val action = FavoritesFragmentDirections.actionFavoritesToDetail(property)

                exitTransition = Hold()

                val extras = SharedElementTransition.createSharedElementExtra(selectedProperty,property)
                if (extras != null)
                    findNavController().navigate(action, extras)
                else
                    findNavController().navigate(action)

            }
        })
    }

}
