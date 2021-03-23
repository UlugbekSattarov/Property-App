package com.example.marsrealestate.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChoosePaymentFragmentTest {


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
        MarsProperty("14506", "url1", "rent", 2000.toDouble(),45.2f,56f,250f),
        MarsProperty("14507", "url2", "buy", 5000.toDouble(),45.2f,56f,250f),
        MarsProperty("14508", "url3", "rent", 200000.toDouble(),45.2f,56f,250f),
        MarsProperty("14509", "url4", "rent", 25000.toDouble(),45.2f,56f,250f)
    )

    @Before
    fun setup() {
        ServiceLocator.marsRepository = FakeTestRepository().apply {
            setPropertiesDataset(props)
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun testChoosePaymentVisa()  = mainCoroutineRule.runBlockingTest{

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.dest_choose_payment)

        val args = bundleOf( "propertyToBuyId" to "14506")


        val scenario = launchFragmentInContainer(args, R.style.AppTheme) {
            ChoosePaymentFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null)
                        Navigation.setViewNavController(frag.requireView(),navController)
                }
            }
        }




        Espresso.onView(ViewMatchers.withId(R.id.payment_option_visa)).perform(ViewActions.click())

        assertEquals(R.id.dest_payment_visa,navController.currentDestination?.id)
//        Thread.sleep(50_000)

    }


}