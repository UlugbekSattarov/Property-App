package com.example.marsrealestate.overview

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentOverviewBinding
import com.example.marsrealestate.util.NotificationHelper
import com.example.marsrealestate.util.SharedElementTransition
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold

class OverviewFragment : Fragment() {

    private val viewModel : OverviewViewModel by viewModels {
        OverviewViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var  viewDataBinding : FragmentOverviewBinding

    private var enableListAppearingAnimation = true

    //Useful for shared element transition
    private var selectedProperty : View? = null

    private var appBarLayoutIsShown = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        exitTransition = null

        viewDataBinding = FragmentOverviewBinding.inflate(LayoutInflater.from(requireContext()))
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner


        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(viewDataBinding.toolbar)
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
        inflater.inflate(R.menu.overview_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            viewModel.loadNextPage(true)
        }
        else if (item.itemId == R.id.menu_test) {
            bli()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bli() {

        val property = MarsProperty("140158", "${R.drawable.mars_landscape_2}", "", 0.toDouble(),22f,58f,-50f)


        val deeplink = findNavController().createDeepLink()
            .setArguments(bundleOf("MarsProperty" to property))
            .setGraph(R.navigation.nav_graph_main)
            .setDestination(R.id.dest_detail)
            .createPendingIntent()


        NotificationHelper.notifyPropertyBought(requireContext(),deeplink,property)
    }

    private fun setupAppBarLayoutVisualState() {
        viewDataBinding.appBarLayout.setExpanded(appBarLayoutIsShown)
        viewDataBinding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                appBarLayoutIsShown = verticalOffset == 0
            })
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupRecyclerView() {
        viewModel.properties.observe(viewLifecycleOwner) {
            if ( it.isNotEmpty()) {
                playListAnimationOnlyOnce()
            }
            //Passing in a copy makes the adapter load visually the new elements in the list,
            //otherwise we have to call notifyDatasetChanged but it can cause some visual trouble
            //with the layout animation
            (viewDataBinding.photosGrid.adapter as? androidx.recyclerview.widget.ListAdapter<Any, RecyclerView.ViewHolder>)?.submitList(it.toList())
        }

        viewDataBinding.photosGrid.adapter = OverviewAdapter(OverviewAdapter.OnClickListener { property, viewClicked ->
            this.selectedProperty = viewClicked
            viewModel.displayPropertyDetails(property)
        }, object : OnLastItemDisplayedListener {
            override fun onLastItemDisplayed() {
                viewModel.loadNextPage()
            }
        })
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
                Snackbar.make(viewDataBinding.root,R.string.no_more_properties, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let navigation@{ property ->
                val action = OverviewFragmentDirections.actionOverviewToDetail(property)

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


