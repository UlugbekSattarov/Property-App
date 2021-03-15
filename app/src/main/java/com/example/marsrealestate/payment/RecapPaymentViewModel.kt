package com.example.marsrealestate.payment

import android.util.Log
import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.payment.options.PaymentOption
import com.example.marsrealestate.payment.options.PaymentVisaViewModel
import com.example.marsrealestate.payment.options.VisaCard
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class RecapPaymentViewModel(
    private val propertyId : String,
    paymentOption : PaymentOption,
    private val repository: MarsRepository
) : ViewModel() {


    private val _propertyToBuy : MutableLiveData<MarsProperty?> = MutableLiveData()
    val propertyToBuy : LiveData<MarsProperty?> = _propertyToBuy

    val isPropertyValid = propertyToBuy.map { prop -> prop != null }

    val paymentOption : LiveData<PaymentOption> = MutableLiveData(paymentOption)

    private val _transactionState : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val transactionState : LiveData<Result<Nothing>> = _transactionState

    private val _transactionCompleted = MutableLiveData<Event<MarsProperty>>()
    val transactionCompleted: LiveData<Event<MarsProperty>> = _transactionCompleted

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome

    init {
        viewModelScope.launch {
            _propertyToBuy.postValue(repository.getProperty(propertyId))
        }
    }

    fun confirmTransaction() {
        _transactionState.value = Result.Loading()

        val property = propertyToBuy.value

        if (property == null || paymentOption.value == null) {
            _transactionState.postValue(Result.Error())
            return
        }

        viewModelScope.launch {
            delay(2000)
            Log.i(RecapPaymentViewModel::class.toString(),"Property bought : $property")
            _transactionCompleted.postValue(Event(property))
            _transactionState.postValue(Result.Success())
            _navigateToHome.postValue(Event(true))
        }
    }

}


class RecapPaymentViewModelFactory(private val propertyId: String,
                                   private val paymentOption: PaymentOption,
                                   private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecapPaymentViewModel(propertyId,paymentOption,repository) as T
    }
}

