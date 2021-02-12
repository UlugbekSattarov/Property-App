package com.example.marsrealestate.util

import android.app.Activity
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Transition
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.marsrealestate.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

fun Activity.setupToolbarIfDrawerLayoutPresent(fragment : Fragment, toolbar: Toolbar ) {
    try {
        findViewById<DrawerLayout>(R.id.drawerlayout)?.let {
            val config = AppBarConfiguration(setOf(
                R.id.dest_overview,
                R.id.dest_favorites,
                R.id.dest_login,
                R.id.dest_settings), it)

//            This is not customizable enough, notably for the navigation icon
//            toolbar.setupWithNavController(fragment.findNavController(),config)


            val navController = fragment.findNavController()
            val id = navController.currentDestination?.id ?: return

            if (config.topLevelDestinations.contains(id)) {
                toolbar.setNavigationIcon(R.drawable.ic_drawer_menu)
            }
            else
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back)

            toolbar.setNavigationOnClickListener { navigateUp(navController,config) }

        }
    }
    catch (e : Exception) {
        Log.e("Extensions",e.stackTrace.toString())
    }
}


fun Activity.closeDrawerIfPresent(toDoAfter : () -> Unit) {
    try {
        val drawer = findViewById<DrawerLayout>(R.id.drawerlayout)

        if (drawer != null && drawer.isOpen) {
            drawer.run {
                addDrawerListener(object : DrawerLayout.DrawerListener {
                    override fun onDrawerStateChanged(newState: Int) {}

                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

                    override fun onDrawerClosed(drawerView: View) {
                        removeDrawerListener(this)
                        toDoAfter()
                    }

                    override fun onDrawerOpened(drawerView: View) {}

                })
                close()
            }
        }
        else {
            toDoAfter()
        }
    }
    catch (e : Exception) {
        Log.e("Extensions",e.stackTrace.toString())
    }
}

fun Snackbar.withColoredText() : Snackbar {
    val color = TypedValue()
    context.theme.resolveAttribute(R.attr.colorOnPrimary,color,true)
    setTextColor(color.data)
    return this
}

fun Transition.doOnEnd(block : () -> Unit) : Transition {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            removeListener(this)
            block()
        }

        override fun onTransitionResume(transition: Transition) {
        }

        override fun onTransitionPause(transition: Transition) {
        }

        override fun onTransitionCancel(transition: Transition) {
            removeListener(this)
            block()
        }

        override fun onTransitionStart(transition: Transition) {
        }
    })

    return this
}

//For the glide library

/**
 * Executes the specified [body] when the request is complete. It is invoked no matter whether the
 * request succeeds or fails.
 */
fun <T> RequestBuilder<T>.doOnEnd(body: () -> Unit): RequestBuilder<T> {
    return addListener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }

        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }
    })
}