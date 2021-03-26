package com.example.marsrealestate.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.marsrealestate.testshared.MainCoroutineRule
import com.example.marsrealestate.testshared.data.FakeTestRepository
import com.example.marsrealestate.testshared.getOrAwaitValue
import com.example.marsrealestate.util.FormValidation
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.isError
import com.example.marsrealestate.util.noError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class LoginViewModelTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    // Useful to tests cases using viewModelScope.launch {}
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule()


    private lateinit var viewModel : LoginViewModel


    @ExperimentalCoroutinesApi
    @Test
    fun testLogin() = mainCoroutineRule.runBlockingTest {
        viewModel = LoginViewModel(FakeTestRepository())

        assertTrue(Result.notYetDone(viewModel.operationLogging.value))
        assertNull(viewModel.isLoggedIn.value)
        assertNull(viewModel.loggedInEvent.value)

        viewModel.login()
        assertTrue(viewModel.operationLogging.getOrAwaitValue().isError())

        viewModel.email.value = "45!!"
        assertTrue(FormValidation.isError(viewModel.emailErrorStringId.getOrAwaitValue()))
        viewModel.email.value = "tes*t@bli.com"
        assertTrue(FormValidation.isError(viewModel.emailErrorStringId.getOrAwaitValue()))
        viewModel.email.value = "test@bli.com"
        assertFalse(FormValidation.isError(viewModel.emailErrorStringId.getOrAwaitValue()))


        viewModel.password.value = "0"
        assertTrue(FormValidation.isError(viewModel.passwordErrorStringID.getOrAwaitValue()))
        viewModel.password.value = "0000"
        assertFalse(FormValidation.isError(viewModel.passwordErrorStringID.getOrAwaitValue()))

        viewModel.login()

        assertTrue(viewModel.operationLogging.getOrAwaitValue().isSuccess())
        assertNotNull(viewModel.loggedInEvent.getOrAwaitValue())
        assertTrue(viewModel.isLoggedIn.getOrAwaitValue() )

    }



}