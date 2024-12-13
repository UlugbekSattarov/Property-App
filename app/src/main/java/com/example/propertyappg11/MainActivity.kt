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

        PreferencesHelper.setFontSize(this)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        navController = Navigation.findNavController(viewBinding.root.findViewById(R.id.nav_host_fragment))
        viewBinding.navigationView.setupWithNavController(navController)

        setupPurchaseProgressBarVisibility(navController)
    }

    override fun onBackPressed() {
        if (viewBinding.drawerlayout.isDrawerOpen(GravityCompat.START))
            viewBinding.drawerlayout.close()
//        else if (navController.previousBackStackEntry?.destination?.id == R.id.dest_blank)
//            moveTaskToBack(true)
        else
            super.onBackPressed()
    }

    private fun setupPurchaseProgressBarVisibility(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewBinding.purchaseProgression.apply {

                runOnUiThread {
                    var newAlpha = 1f
                    var startDelay = 50L
                    when (destination.id) {
                        R.id.dest_choose_payment -> { currentStep = 0 }
                        R.id.dest_payment_visa -> { currentStep = 1  }
                        R.id.dest_payment_recap -> { currentStep = 2  }
                        //In this last case we want to hide the PurchaseProgression view
                        else -> { newAlpha = 0f; startDelay = 0; currentStep = 0  }
                    }
                    if (newAlpha == 1f)
                        visibility = View.VISIBLE

                    animate().alpha(newAlpha).setStartDelay(startDelay)
                        .withEndAction { if (newAlpha == 0f) visibility = View.INVISIBLE }
                        .setDuration(200).start()
                }

            }
        }
    }
}
