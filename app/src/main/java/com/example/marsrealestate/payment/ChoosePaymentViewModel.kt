package com.example.marsrealestate.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsrealestate.util.Event


class ChoosePaymentViewModel : ViewModel() {

    private val _navigateToVisaPayment = MutableLiveData<Event<Boolean>>()
    val navigateToVisaPayment: LiveData<Event<Boolean>> = _navigateToVisaPayment


    fun navigateToVisa() {
        _navigateToVisaPayment.value = Event(true)
    }

}

class ChoosePaymentViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChoosePaymentViewModel() as T
    }
}
