package com.example.propertyappg11.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.example.propertyappg11.R
import com.example.propertyappg11.ServiceLocator
import com.example.propertyappg11.databinding.FragmentFavoritesBinding
import com.example.propertyappg11.util.helpers.PreferencesHelper
import com.example.propertyappg11.util.helpers.SharedElementTransitionHelper
import com.example.propertyappg11.util.setupFadeThroughTransition
import com.example.propertyappg11.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.snackbar.Snackbar


class FavoritesFragment : Fragment() {

    val viewModel : FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding : FragmentFavoritesBinding

    private var enableListAppearingAnimation = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = null

        // Inflate the layout for this fragment
        viewDataBinding = FragmentFavoritesBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.fragment = this

        setupFadeThroughTransition(viewDataBinding.root)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupHint()
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
        viewModel.favorites.observe(viewLifecycleOwner,  {
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
        viewModel.propertyRemoved.observe(viewLifecycleOwner,  {
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
        viewModel.navigateToProperty.observe(viewLifecycleOwner,  {
            it.getContentIfNotHandled()?.let navigation@{ fav ->
                val action = FavoritesFragmentDirections.actionFavoritesToDetail().apply { marsProperty = fav.property }

                SharedElementTransitionHelper.navigate(this,fav.property,action)
            }
        })

        viewModel.navigateToOverview.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                navigateToOverview()
            }
        }
    }

    private fun navigateToOverview() =
        findNavController().run {
            popBackStack(R.id.nav_graph_main, false)
            navigate(R.id.dest_overview)
        }

    fun setupHint() =
        viewModel.favorites.observe(viewLifecycleOwner) {

            viewDataBinding.motionlayoutHintSwipe.visibility =
                if (it.isNotEmpty() && PreferencesHelper.Tuto.getShowFavoritesSwipe(requireContext()))
                    View.VISIBLE
                else
                    View.GONE
        }



    fun hideSwipeFavoritesHint() {
        PreferencesHelper.Tuto.setShowFavoritesSwipe(requireContext(),false)
        TransitionManager.beginDelayedTransition(viewDataBinding.root as ViewGroup)
        viewDataBinding.motionlayoutHintSwipe.visibility = View.GONE
    }

}
