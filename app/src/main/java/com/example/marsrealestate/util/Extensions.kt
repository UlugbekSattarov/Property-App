package com.example.marsrealestate.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.transition.Transition
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.marsrealestate.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough

@Deprecated("Use setupToolbarIfDrawerLayoutPresent instead")
fun Activity.setupToolbarIfDrawerLayoutPresentOld(fragment : Fragment, toolbar: Toolbar ) {
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

fun Activity.setupToolbarIfDrawerLayoutPresent(fragment : Fragment, toolbar: Toolbar ) {
    try {

        val toplevelDestinations = setOf(
            R.id.dest_overview,
            R.id.dest_favorites,
            R.id.dest_login,
            R.id.dest_sell,
            R.id.dest_settings)


        val navController = fragment.findNavController()
        val id = navController.currentDestination?.id ?: return

        val icon = if (id in toplevelDestinations)
            R.drawable.ic_drawer_menu
        else
            R.drawable.ic_arrow_back

        toolbar.setNavigationIcon(icon)

        toolbar.setNavigationOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawerlayout)
            val config = AppBarConfiguration(toplevelDestinations, drawer)
            navigateUp(navController,config)
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

@ColorInt
fun Context.resolveColor(attr : Int) :  Int{
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@Suppress("unused")
fun Snackbar.withColoredText() : Snackbar {
    setTextColor(context.resolveColor(R.attr.colorOnPrimary))
    return this
}

@Suppress("unused")
fun Activity.makeNavigationBarColored() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.navigationBarColor = resolveColor(R.attr.colorSurface)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//            window.navigationBarDividerColor = resolveColor(android.R.attr.listDivider)
    }
}

@Suppress("unused")
fun Activity.makeNavigationBarTranslucent() {
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

}

/**
 * Fetch this [LiveData] its value or throw an exception if it is null
 */
inline fun <reified T> LiveData<T>.getValueNotNull() : T = this.value ?: throw Exception("Value of type ${T::class.simpleName} was null")


/**
 * Fetch this [LiveData] its value or throw an exception if it is null.
 * @param validator should return a [@StringRes] without throwing exceptions. If the data is valid,
 * it should then return [FormValidation.NO_ERROR]
 */
inline fun <reified T> LiveData<T>.getValueNotNull(validator :  (data : T) -> Int) : T {
    val value = this.getValueNotNull()

    val validation = validator(value)

    if (validation.isValidationError())
        throw Exception("Validator throwed error : $validation")

    return value
}

fun View.hideSoftInput() {
    clearFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, InputMethodManager.RESULT_UNCHANGED_HIDDEN)
}

fun Fragment.hideSoftInput() {
    (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun View.showSoftInput() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.setupFadeThroughTransition(root : View) {
    enterTransition = MaterialFadeThrough().addTarget(root)
    exitTransition = MaterialFadeThrough().addTarget(root)
    returnTransition = null
    reenterTransition = null
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