package com.example.marsrealestate.payment

import android.content.Context
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ScrollView
import androidx.appcompat.widget.MenuPopupWindow
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.MainApplication
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.detail.MarsCoordsToStringConverter
import com.example.marsrealestate.overview.OverviewAdapter
import com.example.marsrealestate.overview.OverviewFragment
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.AdditionalMatchers.not

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
    lateinit var navController : TestNavHostController


    @Before
    fun setup() {
        ServiceLocator.marsRepository = FakeTestRepository().apply { setPropertiesDataset(props) }
        context  = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context = context)
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.dest_payment_recap)
        navController.navigate(R.id.dest_payment_recap)
    }



    @ExperimentalCoroutinesApi
    @Test
    fun recapSuccessPropertyInfosDisplayed()  = mainCoroutineRule.runBlockingTest{

        val paymentOption = VisaCard("","","1234567891234567",0,0,"")
        val prop = props[0]

        val validArgs = bundleOf("paymentOption" to paymentOption, "propertyToBuyId" to prop.id)


        val scenario = launchFragmentInContainer(validArgs, R.style.AppTheme) {
            RecapPaymentFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(frag.requireView(), navController)
                    }
                }
            }
        }

        //Checking that the id, the price and the payment option displayed are correct
        onView(withId(R.id.property_id_detail)).check(matches(withText(prop.id)))
        onView(withId(R.id.price_value)).check(matches(withText(context.getString(R.string.display_price_money,prop.price))))
        onView(withId(R.id.card_number_detail)).check(matches(withText(paymentOption.getLabelHidden())))

        onView(withId(R.id.button_confirm)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
//        onView(ViewMatchers.isAssignableFrom(ScrollView::class.java)).perform(scrollTo())

        onView(withId(R.id.button_confirm)).perform(scrollTo()).check(matches(isDisplayed())).check(matches(ViewMatchers.isClickable())).perform(click())

        //Confirm becomes invisible after successful confirm, error is not displayed
        onView(withId(R.id.button_confirm)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))


        scenario.moveToState(Lifecycle.State.DESTROYED)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun recapErrorPropertyInfosDisplayed()  = mainCoroutineRule.runBlockingTest{

        val paymentOption = VisaCard("","","1234567891234567",0,0,"")
        val prop = props[0]

        val invalidArgs = bundleOf("paymentOption" to paymentOption, "propertyToBuyId" to "-1")


        val scenario = launchFragmentInContainer(invalidArgs, R.style.AppTheme) {
            RecapPaymentFragment().also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(frag.requireView(), navController)
                    }
                }
            }
        }

        //Checking that the id, the price and the payment option displayed are correct
//        onView(withId(R.id.property_id_detail)).check(matches(withText(prop.id)))
//        onView(withId(R.id.price_value)).check(matches(withText(context.getString(R.string.display_price_money,prop.price))))
//        onView(withId(R.id.card_number_detail)).check(matches(withText(paymentOption.getLabelHidden())))


        //The property is invalid so the confirm button is disabled and the error is displayed
        onView(withId(R.id.button_confirm)).check(matches((org.hamcrest.Matchers.not(ViewMatchers.isEnabled()))))
        onView(withId(R.id.error_message)).check(matches(isDisplayed()))


        scenario.moveToState(Lifecycle.State.DESTROYED)
    }


    @ExperimentalCoroutinesApi
    @Test
    @LargeTest
    fun test() = mainCoroutineRule.runBlockingTest{
        val scenario = ActivityScenario.launch(MainActivity::class.java)

//        val idling = CountingIdlingResource("test")
//        IdlingRegistry.getInstance().register(idling)
//        idling.increment()


        onView(withContentDescription(R.string.navigate_up)).perform(click())
        onView(withId(R.id.settings)).perform(click())


        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment)
            assertEquals(R.id.dest_settings,navController.currentDestination?.id)
        }


        onView(withContentDescription(R.string.navigate_up)).perform(click())
        onView(withId(R.id.overview)).perform(click())


        val positionToClick = 0
        var property = props[1] //It will be changed just after so initialization is not important


        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment)
            assertEquals(R.id.dest_overview,navController.currentDestination?.id)

            property =
                (activity.findViewById<RecyclerView>(R.id.photos_grid)
                    ?.adapter as OverviewAdapter)
                    .currentList[positionToClick]
        }

//        onData(allOf()).inAdapterView(withId(R.id.photos_grid)).atPosition(0).perform(click())

        onView(withId(R.id.photos_grid)).perform(RecyclerViewActions.actionOnItemAtPosition<OverviewAdapter.MarsPropertyViewHolder>(positionToClick, click()))
        onView(withId(R.id.coords)).check(matches(withText(MarsCoordsToStringConverter.formatCoordsToString(property))))

        onView(withId(R.id.extended_fab)).perform( click())
        onView(withId(R.id.email_value)).perform(typeText("a@a.a"))
        onView(withId(R.id.pasword_value)).perform(typeText("aaaa"))

        onView(allOf(
            withClassName(endsWith("MaterialButton")),
            withId(R.id.button_login))
        ).perform(click())

        onView(withId(R.id.payment_option_visa)).perform(click())

        onView(withId(R.id.card_number_value)).perform(typeText("2222222222222222"))
        onView(withId(R.id.card_secret_value)).perform(typeText("568"))

        //First click display the dropDown Menu
        onView(withId(R.id.card_expiration_month_value)).perform(click())
        //Second click selects an element from the dropDown
        onData(allOf())
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0).perform(click())

        onView(withId(R.id.card_expiration_year_value)).perform(click())
        onData(allOf())
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0).perform(click())


        onView(withId(R.id.button_next)).perform(click())

//        onView(withId(R.id.button_confirm)).perform(click())


    }

}