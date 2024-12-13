package com.example.propertyappg11.util.helpers

import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.util.helpers.SharedElementTransitionHelper.getTransitionName
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform

/**
 * Handle the SharedElement transition from a [Fragment] A to B.
 * A android:tag must be set on the view on fragment A that will be shared.
 * For the tag content, use [getTransitionName]
 *
 */
object SharedElementTransitionHelper {

    /**
     * Create a unique transition name for the given property
     */
    @JvmStatic
    fun getTransitionName(marsProperty: MarsProperty) = marsProperty.id

    /**
     * Navigate to the given direction. Must be called from [Fragment] A.
     * Take care of the SharedElement transition
     *  with a [MarsProperty] for this [Fragment].
     * If the transition is not possible, it falls back on the standard transition.
     */
    fun navigate(
        fragment: Fragment,
        property: MarsProperty,
        direction : NavDirections
    ) {
        val transitionName = getTransitionName(property)
        val sharedView = fragment.view?.findViewWithTag<View>(getTransitionName(property))


        if (sharedView != null) {
            fragment.exitTransition = Hold()
            val extras = FragmentNavigatorExtras(sharedView to transitionName)
            fragment.findNavController().navigate(direction,extras)
        }
        else {
            fragment.findNavController().navigate(direction)
        }
    }


    /**
     * Setup the [Fragment] B for a SharedElement transition.
     * @param view An optional parameter telling into which view the transition will morph, if none
     * is provided, the [Fragment]'s root view is used
     */
    fun setupReceiverFragment(fragment: Fragment,property: MarsProperty,view: View? = null) {

        ViewCompat.setTransitionName(view ?: fragment.requireView(),getTransitionName(property))

        fragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            isElevationShadowEnabled = false // Must be set to false to prevent HUGE performance drops
        }
//        sharedElementReturnTransition = sharedElementEnterTransition
    }


}
