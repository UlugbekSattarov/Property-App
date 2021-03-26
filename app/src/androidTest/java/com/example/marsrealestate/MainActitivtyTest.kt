package com.example.marsrealestate

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.detail.MarsCoordsToStringConverter
import com.example.marsrealestate.overview.OverviewAdapter
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActitivtyTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    // Useful to tests cases using viewModelScope.launch {}
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule()

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)


    val props = listOf(
        MarsProperty("14506", "${R.drawable.mars_landscape_1}", "rent", 2000.toDouble(),45.2f,56f,250f),
        MarsProperty("14507", "${R.drawable.mars_landscape_2}", "buy", 5000.toDouble(),45.2f,56f,250f),
        MarsProperty("14508", "${R.drawable.mars_landscape_3}", "rent", 200000.toDouble(),45.2f,56f,250f),
        MarsProperty("14509", "${R.drawable.mars_landscape_4}", "rent", 25000.toDouble(),45.2f,56f,250f)
    )

    lateinit var context : Context


    @Before
    fun setup() {
        ServiceLocator.marsRepository = FakeTestRepository().apply { setPropertiesDataset(props) }
        context  = ApplicationProvider.getApplicationContext()
    }


    @ExperimentalCoroutinesApi
    @Test
    @LargeTest
    fun test() = mainCoroutineRule.runBlockingTest{
        val scenario = ActivityScenario.launch(MainActivity::class.java)

//        val idling = CountingIdlingResource("test")
//        IdlingRegistry.getInstance().register(idling)
//        idling.increment()


        Espresso.onView(ViewMatchers.withContentDescription(R.string.navigate_up))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(ViewActions.click())


        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment)
            Assert.assertEquals(R.id.dest_settings, navController.currentDestination?.id)
        }


        Espresso.onView(ViewMatchers.withContentDescription(R.string.navigate_up))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.overview)).perform(ViewActions.click())


        val positionToClick = 0
        var property = props[1] //It will be changed just after so initialization is not important


        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment)
            Assert.assertEquals(R.id.dest_overview, navController.currentDestination?.id)

            property =
                (activity.findViewById<RecyclerView>(R.id.photos_grid)
                    ?.adapter as OverviewAdapter)
                    .currentList[positionToClick]
        }

//        onData(allOf()).inAdapterView(withId(R.id.photos_grid)).atPosition(0).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.photos_grid)).perform(
            RecyclerViewActions.actionOnItemAtPosition<OverviewAdapter.MarsPropertyViewHolder>(positionToClick,
                ViewActions.click()
            ))
        Espresso.onView(ViewMatchers.withId(R.id.coords)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    MarsCoordsToStringConverter.formatCoordsToString(property)
                )
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.extended_fab)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.email_value))
            .perform(ViewActions.typeText("a@a.a"))
        Espresso.onView(ViewMatchers.withId(R.id.pasword_value))
            .perform(ViewActions.typeText("aaaa"))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withClassName(Matchers.endsWith("MaterialButton")),
                ViewMatchers.withId(R.id.button_login)
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.payment_option_visa)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.card_number_value))
            .perform(ViewActions.typeText("2222222222222222"))
        Espresso.onView(ViewMatchers.withId(R.id.card_secret_value))
            .perform(ViewActions.typeText("568"))

        //First click display the dropDown Menu
        Espresso.onView(ViewMatchers.withId(R.id.card_expiration_month_value))
            .perform(ViewActions.click())
        //Second click selects an element from the dropDown
        Espresso.onData(Matchers.allOf())
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.card_expiration_year_value))
            .perform(ViewActions.click())
        Espresso.onData(Matchers.allOf())
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0).perform(ViewActions.click())


        Espresso.onView(ViewMatchers.withId(R.id.button_next)).perform(ViewActions.click())

//        onView(withId(R.id.button_confirm)).perform(click())


    }
}