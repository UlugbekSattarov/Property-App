package com.example.marsrealestate.login

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.isValidationError
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class LoginViewModel(private val savedStateHandle: SavedStateHandle,
                     private val repository: MarsRepository) : ViewModel() {

    companion object {
        private const val KEY_PASSWORD = "password"
        private const val KEY_USERNAME = "username"
        private const val KEY_LOGGED_IN = "login"
    }

    val email : MutableLiveData<String> = MutableLiveData()
    val password : MutableLiveData<String> = MutableLiveData()

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    val userLogged: LiveData<String?> = isLoggedIn.map { if (it) "User" else null }

    private val _operationLogging : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val operationLogging : LiveData<Result<Nothing>> = _operationLogging

    private val _loggedInEvent : MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loggedInEvent : LiveData<Event<Boolean>> = _loggedInEvent

    private val _navigateToOverviewEvent : MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigateToOverviewEvent : LiveData<Event<Boolean>> = _navigateToOverviewEvent



    init {
        restoreState()
    }



    val emailErrorStringId = email.map {
        val pattern = Pattern.compile("^[a-zA-Z0-9_.+-]+@\\w+\\.\\w+\$")

        if (pattern.matcher(it).matches())
            NO_ERROR
        else
            R.string.incorrect_email
    }


    val passwordErrorStringID = password.map {
        if (it.isNullOrEmpty())
            R.string.password_empty
        else if (it.length < 4)
            R.string.password_too_short
        else NO_ERROR
    }





    fun login() {
        //Will cause the errors to be updated on the UI, if any
        email.postValue(email.value ?: "")
        password.postValue(password.value ?: "")

        if (emailErrorStringId.isValidationError() || passwordErrorStringID.isValidationError()) {
            _operationLogging.value = Result.Error()
            return
        }

        _operationLogging.value = Result.Loading()
        viewModelScope.launch {
            val result = repository.login(email.value ?: "", password.value ?: "")

            if (result.isSuccess()) {
                _operationLogging.postValue(Result.Success())
                _isLoggedIn.postValue(true)
                _loggedInEvent.postValue(Event(true))

                saveState(true,email.value, password.value)

            }
            else {
                _operationLogging.postValue(Result.Error())
            }
        }
    }



    fun logout() {
        _isLoggedIn.value = false
        saveState(false)
    }

    fun navigateToOverview() {
        _navigateToOverviewEvent.value = Event(true)
    }


    private fun saveState(isLoggedIn : Boolean, username : String? = null, password : String? = null) {
        username?.let { savedStateHandle[KEY_USERNAME] = it }
        password?.let { savedStateHandle[KEY_PASSWORD] = it }
        savedStateHandle[KEY_LOGGED_IN] = isLoggedIn
    }

    private fun restoreState() {
        savedStateHandle.get<Boolean>(KEY_LOGGED_IN)?.let { _isLoggedIn.value = it }
        savedStateHandle.get<String>(KEY_USERNAME)?.let { email.value = it }
        savedStateHandle.get<String>(KEY_PASSWORD)?.let { password.value = it }
    }




}



class LoginViewModelFactory(private val repository: MarsRepository,
                            owner: SavedStateRegistryOwner,
                            defaultArgs: Bundle?
)
    : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return LoginViewModel(repository) as T
//    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return LoginViewModel(handle,repository) as T
    }
}
