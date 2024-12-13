package com.example.propertyappg11.overview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.propertyappg11.R
import com.example.propertyappg11.testshared.MainCoroutineRule
import com.example.propertyappg11.testshared.data.FakeTestRepository
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.data.query.PropApiFilter
import com.example.propertyappg11.testshared.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*

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

    private val props = listOf(
        PropProperty("14506", "${R.drawable.img1}", "rent", 2000.toDouble(),45.2f,56f,250f),
        PropProperty("14507", "${R.drawable.img2}", "buy", 5000.toDouble(),45.2f,56f,250f),
        PropProperty("14508", "${R.drawable.img3}", "rent", 200000.toDouble(),45.2f,56f,250f),
        PropProperty("14509", "${R.drawable.img4}", "rent", 25000.toDouble(),45.2f,56f,250f),
        PropProperty("14510", "${R.drawable.img5}", "rent", 25000.toDouble(),45.2f,56f,250f),
        PropProperty("14511", "${R.drawable.img1}", "rent", 25000.toDouble(),45.2f,56f,250f),
        PropProperty("14512", "${R.drawable.img2}", "rent", 25000.toDouble(),45.2f,56f,250f),
        PropProperty("14513", "${R.drawable.img3}", "rent", 25000.toDouble(),45.2f,56f,250f)
    )


    @Before
    fun setUpViewModel() {
        repository = FakeTestRepository().apply { setPropertiesDataset(props) }
        repository.willThrowExceptionForTesting = false
        viewModel = OverviewViewModel(repository)
    }


    @Test
    fun repositoryBroken() {
        repository.willThrowExceptionForTesting = true
        viewModel = OverviewViewModel(repository)

        Assert.assertTrue(viewModel.properties.getOrAwaitValue().isEmpty())
        Assert.assertTrue(viewModel.status.getOrAwaitValue().isError())

        //Repository will function properly now
        repository.willThrowExceptionForTesting = false

        viewModel.loadNextPage()

        Assert.assertFalse(viewModel.properties.getOrAwaitValue().isEmpty())
        Assert.assertTrue(viewModel.status.getOrAwaitValue().isSuccess())

        //Error again
        repository.willThrowExceptionForTesting = true

        viewModel.loadNextPage()

        Assert.assertTrue(viewModel.status.getOrAwaitValue().isError())


    }

    @Test
            /**
             * Tests that the properties are properly retrieved from the repository when
             * filters are changed
             */
    fun `getMarsRealEstateProperties() `() = mainCoroutineRule.runBlockingTest{

        viewModel.properties.getOrAwaitValue()

        //Only one property matches the search criteria
        viewModel.type.value = PropApiFilter.PropPropertyType.RENT
        viewModel.queryString.value = "14506"
        Assert.assertEquals(1, viewModel.properties.getOrAwaitValue().size)
        //Since we are in synchronous environment, we must check the status AFTER the properties are awaited
        Assert.assertTrue(viewModel.status.getOrAwaitValue().isSuccess())


        //No property matches the search criteria
        viewModel.queryString.value = "NOT_EXISTING"
        Assert.assertEquals(0, viewModel.properties.getOrAwaitValue().size)


        //Every filter is reset, list should be one page length maximum
        viewModel.clearQueryString()
        viewModel.type.value = PropApiFilter.PropPropertyType.ALL

        if (props.size <= viewModel.itemsPerPage)
            Assert.assertEquals(props.size,viewModel.properties.getOrAwaitValue().size)
        else
            Assert.assertEquals(viewModel.itemsPerPage,viewModel.properties.getOrAwaitValue().size)

    }


    @Test
    fun displayPropertyDetails()  = mainCoroutineRule.runBlockingTest {

        viewModel = OverviewViewModel(FakeTestRepository())
        Assert.assertNull(viewModel.navigateToProperty.value)

        val prop = PropProperty("20","","rent",20.toDouble(),50f,45f,-20f)
        viewModel.displayPropertyDetails(prop)

        viewModel.navigateToProperty.getOrAwaitValue {
            Assert.assertNotNull(viewModel.navigateToProperty.value)
            Assert.assertEquals(prop.id,viewModel.navigateToProperty.value?.peekContent()?.id)
        }

    }
}
