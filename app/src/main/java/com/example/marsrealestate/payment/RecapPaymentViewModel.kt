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
    propertyId : String,
    paymentOption : PaymentOption,
    repository: MarsRepository
) : ViewModel() {

    val propertyToBuy : LiveData<MarsProperty?> = repository.observeProperty(propertyId)

    val paymentOption : LiveData<PaymentOption> = MutableLiveData(paymentOption)

    private val _operationConfirmTransaction : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val operationConfirmTransaction : LiveData<Result<Nothing>> = _operationConfirmTransaction

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome


    fun confirmTransaction() {
        _operationConfirmTransaction.value = Result.Loading()

        if (propertyToBuy.value == null || paymentOption.value == null)
            _operationConfirmTransaction.postValue(Result.Error())


        viewModelScope.launch {
            delay(2000)
            Log.i("aaa","aaaaaaaaaa")
            _operationConfirmTransaction.postValue(Result.Success())
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

