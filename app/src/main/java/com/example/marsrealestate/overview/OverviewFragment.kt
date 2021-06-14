package com.example.marsrealestate.overview

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentOverviewBinding
import com.example.marsrealestate.util.helpers.NotificationHelper
import com.example.marsrealestate.util.helpers.SharedElementTransitionHelper
import com.example.marsrealestate.util.setupMaterialFadeThrough
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        exitTransition = null

        viewDataBinding = FragmentOverviewBinding.inflate(LayoutInflater.from(requireContext()))
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner


        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(viewDataBinding.toolbar)
        setupMaterialFadeThrough(viewDataBinding.root)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this, viewDataBinding.toolbar)

        setupRecyclerView()
        setupNavigation()
        setupNoMoreProperties()
        setupAppBarLayoutVisualState()



        postponeEnterTransition()
        viewDataBinding.root.doOnPreDraw { startPostponedEnterTransition() }
        return viewDataBinding.root
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_overview,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            viewModel.makeNewSearch()
        }
        else if (item.itemId == R.id.menu_test) {
            testNotification()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun testNotification() {
        val property = MarsProperty("140158", "${R.drawable.mars_landscape_2}", "", 0.toDouble(),22f,58f,-50f)
        NotificationHelper.notifyPropertyBought(requireContext(),property)
    }

    /**
     * Useful to keep the visual state of the [AppBarLayout]
     * when navigating from the detail view to this [Fragment].
     *
     * For example if it was hidden, then it remains hidden when the user moves from the
     * detail view to this [Fragment].
     */
    private fun setupAppBarLayoutVisualState() {
        viewDataBinding.appBarLayout.setExpanded(appBarLayoutIsShown)
        viewDataBinding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                appBarLayoutIsShown = verticalOffset == 0
            })
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupRecyclerView() {
        viewModel.properties.observe(viewLifecycleOwner, {
            if ( it.isNotEmpty()) {
                playListAnimationOnlyOnce()
            }

            (viewDataBinding.photosGrid.adapter as? androidx.recyclerview.widget.ListAdapter<Any, RecyclerView.ViewHolder>)
                ?.submitList(it.toList())
        })


        viewDataBinding.photosGrid.adapter = OverviewAdapter(
            //When an item on the list is clicked
            OverviewAdapter.OnClickListener { property ->
                viewModel.displayPropertyDetails(property)
            }
        ) {
            //When the last item of the list has been displayed
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
        viewModel.endOfData.observe(viewLifecycleOwner,  {
            if (it) {
                Snackbar.make(viewDataBinding.root,R.string.no_more_properties, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let navigation@{ property ->
                val action = OverviewFragmentDirections.actionOverviewToDetail().apply { marsProperty = property }

                SharedElementTransitionHelper.navigate(this,property,action)
            }
        })
    }

}


