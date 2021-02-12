package com.example.marsrealestate.payment

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.overview.OverviewFragment
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class RecapPaymentFragmentTest {


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    // Useful to tests cases using viewModelScope.launch {}
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule()


    val props = listOf(
        MarsProperty("14506", "url1", "rent", 2000.toDouble()),
        MarsProperty("14507", "url2", "buy", 5000.toDouble()),
        MarsProperty("14508", "url3", "rent", 200000.toDouble()),
        MarsProperty("14509", "url4", "rent", 25000.toDouble())
    )

    @Before
    fun setup() {
        ServiceLocator.marsRepository = FakeTestRepository().apply {
            setPropertiesDataset(props)
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun testConfirmValidArgs()  = mainCoroutineRule.runBlockingTest{

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.dest_payment_recap)
        navController.navigate(R.id.dest_payment_recap)


        val paymentOption = VisaCard("","","1234567891234567",0,0,"")
        val propId = "14509"
        val args = bundleOf("paymentOption" to paymentOption, "propertyToBuyId" to propId)


        val scenario = launchFragmentInContainer(args, R.style.AppTheme) {
            RecapPaymentFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null)
                        Navigation.setViewNavController(frag.requireView(),navController)
                }
            }
        }

        onView(withId(R.id.confirm_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.confirm_button)).perform(click())

        //Button becomes invisible after successful confirm
        onView(withId(R.id.confirm_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))


    }



    @ExperimentalCoroutinesApi
    @Test
    fun testConfirmIncorrectArgs()  = mainCoroutineRule.runBlockingTest{


        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.dest_payment_recap)
        navController.navigate(R.id.dest_payment_recap)


        val paymentOption = VisaCard("","","1234567891234567",0,0,"")
        val propId = "bla"
        val args = bundleOf("paymentOption" to paymentOption, "propertyToBuyId" to propId)


        val scenario = launchFragmentInContainer(args, R.style.AppTheme) {
            RecapPaymentFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null)
                        Navigation.setViewNavController(frag.requireView(),navController)
                }
            }
        }

        onView(withId(R.id.error_message)).check(matches(withAlpha(0f)))
        onView(withId(R.id.confirm_button)).perform(click())
        onView(withId(R.id.error_message)).check(matches(withAlpha(1f)))
        onView(withId(R.id.confirm_button)).check(matches(withAlpha(1f)))


    }

}