package com.example.marsrealestate.overview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.payment.RecapPaymentFragment
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@MediumTest
@RunWith(AndroidJUnit4::class)
class OverviewFragmentTest {

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

    @Test
    fun check_filter() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer (null,R.style.AppTheme) {
            OverviewFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(frag.requireView(), navController)
                    }
                }
            }
        }

        //Check that "buy" properties are removed after the click on chip_rent
        onView(withId(R.id.chip_rent)).perform(scrollTo(),click())
        onView(withId(R.id.photos_grid)).check { view, noViewFoundException ->
            noViewFoundException?.let { throw it }
            val list = view as RecyclerView
//            assertEquals(props.count { it.isRental },list.adapter?.itemCount)
            (list.adapter as OverviewAdapter).currentList.forEach {
                assertTrue(it.isRental)
            }
        }

        //Check that "rent" properties are removed after the click on chip_buy
        onView(withId(R.id.chip_buy)).perform(scrollTo(),click())
        onView(withId(R.id.photos_grid)).check { view, noViewFoundException ->
            noViewFoundException?.let { throw it }
            val list = view as RecyclerView
//            assertEquals(props.count { !it.isRental },list.adapter?.itemCount)
            (list.adapter as OverviewAdapter).currentList.forEach {
                assertFalse(it.isRental)
            }
        }


    }


    @Test
    fun test_click_navigation() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            setGraph(R.navigation.nav_graph_main)
        }

        val scenario = launchFragmentInContainer<OverviewFragment> (null,R.style.AppTheme)

        val action = object : FragmentScenario.FragmentAction<OverviewFragment> {
            override fun perform(fragment: OverviewFragment) {
                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }

        scenario.onFragment (action)

        onView(withId(R.id.photos_grid))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,click()))

        assertEquals(R.id.dest_detail,navController.currentDestination?.id)
    }
}