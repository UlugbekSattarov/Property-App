package com.example.marsrealestate.login

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.getValueNotNull
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class LoginViewModel(private val savedStateHandle: SavedStateHandle,
                     private val repository: MarsRepository,
                     private val credentialsManager : CredentialsManager) : ViewModel() {

    companion object {
        private const val KEY_LOGIN = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_LOGGED_IN = "logged_in"
    }

    val email : MutableLiveData<String> = MutableLiveData()
    val password : MutableLiveData<String> = MutableLiveData()
    val rememberMe : MutableLiveData<Boolean> = MutableLiveData()

    val emailErrorStringId = email.map { emailValidator(it) }
    val passwordErrorStringID = password.map { passwordValidator(it) }


    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    val userLogged: LiveData<String?> = isLoggedIn.map { if (it) "User" else null }

    private val _operationLogging : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val operationLogging : LiveData<Result<Nothing>> = _operationLogging

    private val _loggedInEvent : MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loggedInEvent : LiveData<Event<Boolean>> = _loggedInEvent


    private val _navigateToOverviewEvent : MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigateToOverviewEvent : LiveData<Event<Boolean>> = _navigateToOverviewEvent



    init {
        restoreCredentials()
        restoreState()
    }




    @StringRes
    private fun emailValidator(email : String) : Int {
        val pattern = Pattern.compile("^[a-zA-Z0-9_.+-]+@\\w+\\.\\w+\$")

        return if (! pattern.matcher(email).matches())
            R.string.incorrect_email
        else
            NO_ERROR
    }


    @StringRes
    private fun passwordValidator(password : String) : Int =
        when {
            password.isBlank() -> R.string.password_empty
            password.length < 4 -> R.string.password_too_short
            else -> NO_ERROR
        }




    fun login() {
        notifyEmptyFields()

        _operationLogging.value = Result.Loading()

        viewModelScope.launch {
            try {
                val email = email.getValueNotNull(::emailValidator)
                val password = password.getValueNotNull(::passwordValidator)
                val rememberMe = rememberMe.value ?: false

                repository.login(email, password)

                _operationLogging.postValue(Result.Success())
                _isLoggedIn.postValue(true)
                _loggedInEvent.postValue(Event(true))

                saveState(true, email, password)
                saveOrDeleteCredentials(rememberMe,email,password)

            } catch (e: Exception) {
                _operationLogging.postValue(Result.Error())
            }
        }
    }

    private fun notifyEmptyFields() {
        //Will cause the errors to be updated on the UI, if any
        email.postValue(email.value ?: "")
        password.postValue(password.value ?: "")
    }



    fun logout() {
        _isLoggedIn.value = false
        saveState(false)
    }

    fun navigateToOverview() {
        _navigateToOverviewEvent.value = Event(true)
    }


    private fun saveState(isLoggedIn : Boolean, login : String? = null, password : String? = null) {
        login?.let { savedStateHandle[KEY_LOGIN] = it }
        password?.let { savedStateHandle[KEY_PASSWORD] = it }
        savedStateHandle[KEY_LOGGED_IN] = isLoggedIn
    }

    private fun restoreState() {
        savedStateHandle.get<Boolean>(KEY_LOGGED_IN)?.let { _isLoggedIn.value = it }
        savedStateHandle.get<String>(KEY_LOGIN)?.let { email.value = it }
        savedStateHandle.get<String>(KEY_PASSWORD)?.let { password.value = it }
    }


    
    private fun saveOrDeleteCredentials(save : Boolean,login : String,password : String) {
        if (save)
            credentialsManager.saveCredentials(Credentials(login,password))
        else
            credentialsManager.deleteCredentials()
    }

    private fun restoreCredentials() {
        credentialsManager.getSavedCredentials()?.let {
            email.value = it.login
            password.value = it.password
            rememberMe.value = true
        }
    }

}



class LoginViewModelFactory(private val repository: MarsRepository,
                            private val credentialsManager : CredentialsManager,
                            owner: SavedStateRegistryOwner,
                            defaultArgs: Bundle?
)
    : AbstractSavedStateViewModelFactory(owner, defaultArgs) {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return LoginViewModel(handle,repository,credentialsManager) as T
    }
}
