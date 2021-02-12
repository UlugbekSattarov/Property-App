package com.example.marsrealestate.overview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.testshared.getOrAwaitValue
import com.example.marsrealestate.data.network.MarsApiFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OverviewViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    // Useful to tests cases using viewModelScope.launch {}
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule()


    private lateinit var viewModel : OverviewViewModel

    private lateinit var repository : FakeTestRepository



    @Before
    fun setUpViewModel() {
        repository = FakeTestRepository()
        viewModel = OverviewViewModel(repository)
    }



    @Test
    fun `getMarsRealEstateProperties() `() = mainCoroutineRule.runBlockingTest{

        repository.willThrowExceptionForTesting = true
        viewModel.getMarsRealEstateProperties()

        var props = viewModel.properties.getOrAwaitValue()
        Assert.assertNotEquals(null,props)
        Assert.assertEquals(0,props.size)


        repository.willThrowExceptionForTesting = false
        viewModel.getMarsRealEstateProperties()

        props = viewModel.properties.getOrAwaitValue()
        Assert.assertNotEquals(null,props)
    }

    @Test
    fun `updateQuery() `() = mainCoroutineRule.runBlockingTest {
        viewModel.getMarsRealEstateProperties()


        viewModel.filter.value =  MarsApiFilter.SHOW_RENT
        viewModel.applyFilters()
        var props = viewModel.properties.getOrAwaitValue()
        props.forEach { assert(it.isRental) }


        val containsString = "6"
        viewModel.filter.value =  MarsApiFilter.SHOW_BUY
        viewModel.queryString.value =  containsString
        viewModel.applyFilters()

        props = viewModel.properties.getOrAwaitValue()
        props.forEach {
            Assert.assertFalse(it.isRental)
            assert(it.id.contains(containsString))
        }

    }


    @Test
    fun `displayPropertyDetails`()  {

        Assert.assertNull(viewModel.navigateToProperty.value)

        val prop = MarsProperty("20","","rent",20.toDouble())
        viewModel.displayPropertyDetails(prop)

        Assert.assertNotNull(viewModel.navigateToProperty.getOrAwaitValue())

    }
}