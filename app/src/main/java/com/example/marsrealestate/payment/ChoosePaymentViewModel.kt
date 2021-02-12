package com.example.marsrealestate.payment

import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.util.*
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.util.*


class ChoosePaymentViewModel : ViewModel() {

    private val _navigateToVisaPayment = MutableLiveData<Event<Boolean>>()
    val navigateToVisaPayment: LiveData<Event<Boolean>> = _navigateToVisaPayment


    fun navigateToVisa() {
        _navigateToVisaPayment.value = Event(true)
    }

}

class ChoosePaymentViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChoosePaymentViewModel() as T
    }
}
