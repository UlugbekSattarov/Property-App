package com.example.marsrealestate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.*
import kotlinx.android.synthetic.main.activity_main.*

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

    }


    override fun onBackPressed() {
        if (viewBinding.drawerlayout.isDrawerOpen(GravityCompat.START))
            viewBinding.drawerlayout.close()
        else if (navController.previousBackStackEntry?.destination?.id == R.id.dest_blank)
            moveTaskToBack(true)
        else
            super.onBackPressed()
    }

}
