package com.example.propertyappg11.overview

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.propertyappg11.R
import com.example.propertyappg11.ServiceLocator
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.databinding.FragmentOverviewBinding
import com.example.propertyappg11.util.helpers.NotificationHelper
import com.example.propertyappg11.util.helpers.ResourceUrlHelper
import com.example.propertyappg11.util.helpers.SharedElementTransitionHelper
import com.example.propertyappg11.util.setupFadeThroughTransition
import com.example.propertyappg11.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

class OverviewFragment : Fragment() {

    private val viewModel : OverviewViewModel by viewModels {
        OverviewViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var  viewDataBinding : FragmentOverviewBinding

    private var enableListAppearingAnimation = true

    private var appBarLayoutIsShown = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.fragment_overview,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_refresh) {
                    viewModel.makeNewSearch()
                }
                else if (menuItem.itemId == R.id.menu_test) {
                    testNotification()
                }
                return true
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewDataBinding = FragmentOverviewBinding.inflate(LayoutInflater.from(requireContext()))
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner


        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(viewDataBinding.toolbar)
        setupFadeThroughTransition(viewDataBinding.root)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this, viewDataBinding.toolbar)

        setupRecyclerView()
        setupNavigation()
        setupNoMoreProperties()
        setupAppBarLayoutVisualState()



        postponeEnterTransition()
        viewDataBinding.root.doOnPreDraw { startPostponedEnterTransition() }
        return viewDataBinding.root
    }








    private fun testNotification() {
        val property = PropProperty("140158",
            ResourceUrlHelper.getResourceAsUrl(requireContext(),R.drawable.img2),
            "rent",
            120_000.toDouble(),
            22f,
            58f,
            -50f)
        NotificationHelper.notifyPropertyBought(requireActivity(),property)
    }

    private fun setupAppBarLayoutVisualState() {
        viewDataBinding.appBarLayout.setExpanded(appBarLayoutIsShown)
        viewDataBinding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            appBarLayoutIsShown = verticalOffset == 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupRecyclerView() {
        viewModel.properties.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                playListAnimationOnlyOnce()
            }

            (viewDataBinding.photosGrid.adapter as? androidx.recyclerview.widget.ListAdapter<Any, RecyclerView.ViewHolder>)
                ?.submitList(it.toList())
        }


        viewDataBinding.photosGrid.adapter = OverviewAdapter(
            OverviewAdapter.OnClickListener { property ->
                viewModel.displayPropertyDetails(property)
            }
        ) {
            Log.d(this@OverviewFragment::class.java.name, "onLastItemDisplayed")
            viewModel.loadNextPage()
        }
    }

    private fun playListAnimationOnlyOnce() {
        if (enableListAppearingAnimation) {
            enableListAppearingAnimation = false
            viewDataBinding.photosGrid.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_appearing)
            viewDataBinding.photosGrid.scheduleLayoutAnimation()
        }
    }

    private fun setupNoMoreProperties() {
        viewModel.endOfData.observe(viewLifecycleOwner) {
            if (it) {
                Snackbar.make(
                    viewDataBinding.root,
                    R.string.no_more_properties,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let navigation@{ property ->
                val action = OverviewFragmentDirections.actionOverviewToDetail()
                    .apply { marsProperty = property }

                SharedElementTransitionHelper.navigate(this, property, action)
            }
        }
    }

}


