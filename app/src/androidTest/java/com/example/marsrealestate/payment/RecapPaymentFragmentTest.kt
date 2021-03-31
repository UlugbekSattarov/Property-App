package com.example.marsrealestate.payment

import android.content.Context
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
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Ignore
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
        navController = TestNavHostController(context)
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.dest_payment_recap)
//        navController.navigate(R.id.dest_payment_recap)
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

        onView(withId(R.id.button_confirm)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//        onView(ViewMatchers.isAssignableFrom(ScrollView::class.java)).perform(scrollTo())

        onView(withId(R.id.button_confirm)).check(matches(isDisplayed())).check(matches(isClickable())).perform(click())

        //Confirm becomes invisible after successful confirm, error is not displayed
        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))

    }

    @ExperimentalCoroutinesApi
    @Test
    fun recapErrorPropertyInfosDisplayed()  = mainCoroutineRule.runBlockingTest{

        val paymentOption = VisaCard("","","1234567891234567",0,0,"")

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

        //The property is invalid so the confirm button is disabled and the error is displayed
        onView(withId(R.id.button_confirm)).check(matches((org.hamcrest.Matchers.not(isEnabled()))))
        onView(withId(R.id.error_message)).check(matches(isDisplayed()))

    }




}