package com.example.marsrealestate.payment.options

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.getOrAwaitValue
import com.example.marsrealestate.util.FormValidation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.Before

class PaymentVisaViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    // Useful to tests cases using viewModelScope.launch {}
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule()


    private lateinit var viewModel : PaymentVisaViewModel

    @Before
    fun setup() {
        viewModel = PaymentVisaViewModel()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun errorExpirationMonth() = mainCoroutineRule.runBlockingTest {

        viewModel.expirationMonth.value = -1
        assertTrue(FormValidation.isError(viewModel.expirationMonthErrorStringID.getOrAwaitValue()))

        viewModel.expirationMonth.value = 2
        assertFalse(FormValidation.isError(viewModel.expirationMonthErrorStringID.getOrAwaitValue()))

        viewModel.expirationMonth.value = 0
        assertTrue(FormValidation.isError(viewModel.expirationMonthErrorStringID.getOrAwaitValue()))

        viewModel.expirationMonth.value = 253
        assertTrue(FormValidation.isError(viewModel.expirationMonthErrorStringID.getOrAwaitValue()))

        viewModel.expirationMonth.value = 12
        assertFalse(FormValidation.isError(viewModel.expirationMonthErrorStringID.getOrAwaitValue()))




    }


    @ExperimentalCoroutinesApi
    @Test
    fun errorCardNumber() = mainCoroutineRule.runBlockingTest {

        viewModel.cardNumber.value = ""
        assertTrue(FormValidation.isError(viewModel.cardNumberErrorStringId.getOrAwaitValue()))

        viewModel.cardNumber.value = "1234123412341234"
        assertFalse(FormValidation.isError(viewModel.cardNumberErrorStringId.getOrAwaitValue()))

        viewModel.cardNumber.value = "12*4123412341234"
        assertTrue(FormValidation.isError(viewModel.cardNumberErrorStringId.getOrAwaitValue()))

        viewModel.cardNumber.value = "123"
        assertTrue(FormValidation.isError(viewModel.cardNumberErrorStringId.getOrAwaitValue()))

        viewModel.cardNumber.value = "1234567891234567"
        assertFalse(FormValidation.isError(viewModel.cardNumberErrorStringId.getOrAwaitValue()))




    }



}