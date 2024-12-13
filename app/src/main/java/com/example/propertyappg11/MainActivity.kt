package com.example.propertyappg11

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.propertyappg11.databinding.ActivityMainBinding
import com.example.propertyappg11.util.helpers.PreferencesHelper

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding : ActivityMainBinding

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        navController = Navigation.findNavController(viewBinding.root.findViewById(R.id.nav_host_fragment))
        viewBinding.navigationView.setupWithNavController(navController)

        setupPurchaseProgressBarVisibility(navController)
    }

    override fun onBackPressed() {
        if (viewBinding.drawerlayout.isDrawerOpen(GravityCompat.START))
            viewBinding.drawerlayout.close()
        else
            super.onBackPressed()
    }

    private fun setupPurchaseProgressBarVisibility(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewBinding.purchaseProgression.apply {

            }
        }
    }
}
