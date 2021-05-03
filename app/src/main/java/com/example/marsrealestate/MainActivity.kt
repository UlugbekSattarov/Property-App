package com.example.marsrealestate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding : ActivityMainBinding

    val marsRepository : MarsRepository
        get() = ServiceLocator.getMarsRepository(this)

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        navController = Navigation.findNavController(viewBinding.root.findViewById<View>(R.id.nav_host_fragment))
        viewBinding.navigationView.setupWithNavController(navController)

        hideOrRevealPurchaseProgressBar(navController)
    }

    override fun onBackPressed() {
        if (viewBinding.drawerlayout.isDrawerOpen(GravityCompat.START))
            viewBinding.drawerlayout.close()
        else if (navController.previousBackStackEntry?.destination?.id == R.id.dest_blank)
            moveTaskToBack(true)
        else
            super.onBackPressed()
    }

    private fun hideOrRevealPurchaseProgressBar(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewBinding.purchaseProgression.apply {

                runOnUiThread {

                    val newAlpha : Float
                    val startDelay : Long
                    when (destination.id) {
                        R.id.dest_choose_payment -> { newAlpha = 1f; startDelay = 50; currentStep = 0 }
                        R.id.dest_payment_visa -> { newAlpha = 1f; startDelay = 50; currentStep = 1  }
                        R.id.dest_payment_recap -> { newAlpha = 1f; startDelay = 50; currentStep = 2  }
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
