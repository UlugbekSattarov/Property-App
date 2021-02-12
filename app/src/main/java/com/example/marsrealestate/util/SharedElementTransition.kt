package com.example.marsrealestate.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty


object SharedElementTransition {

    fun getTransitionName(marsProperty: MarsProperty) = marsProperty.id

    fun createSharedElementExtra(
        itemView: View?,
        property: MarsProperty
    ): FragmentNavigator.Extras? {
        if (itemView == null)
            return null

        val transitionName = getTransitionName(property)

        return FragmentNavigatorExtras(itemView to transitionName)
    }
}