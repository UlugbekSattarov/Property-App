package com.example.marsrealestate.login

import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.IntegerRes
import androidx.annotation.NavigationRes
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.*
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class LoginViewModel(private val repository: MarsRepository) : ViewModel() {


    val email : MutableLiveData<String> = MutableLiveData()
    val password : MutableLiveData<String> = MutableLiveData()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _userLogged = MutableLiveData<String>()
    val userLogged: LiveData<String> = _userLogged

    private val _operationLogging : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val operationLogging : LiveData<Result<Nothing>> = _operationLogging

    private val _loggedInEvent : MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loggedInEvent : LiveData<Event<Boolean>> = _loggedInEvent






    val emailErrorStringId = email.map {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
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

        if (emailErrorStringId.isError() || passwordErrorStringID.isError())
            return

        _operationLogging.value = Result.Loading()
        viewModelScope.launch {
            val result = repository.login(email.value ?: "", password.value ?: "")

            if (result.isSuccess()) {
                _operationLogging.postValue(Result.Success())
                _isLoggedIn.postValue(true)
                _userLogged.postValue("user")
                _loggedInEvent.postValue(Event(true))
            }
            else {
                _operationLogging.postValue(Result.Error())
            }
        }
    }

    fun isLoggedIn() : Boolean = isLoggedIn.value == true


}



class LoginViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}
