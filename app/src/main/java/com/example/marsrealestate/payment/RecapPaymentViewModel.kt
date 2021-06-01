package com.example.marsrealestate.payment

import android.util.Log
import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.payment.options.PaymentOption
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.getValueNotNull
import kotlinx.coroutines.launch

class RecapPaymentViewModel(
    private val propertyId : String,
    paymentOption : PaymentOption,
    private val repository: MarsRepository
) : ViewModel() {


    private val _propertyToBuy : MutableLiveData<MarsProperty> = MutableLiveData()
    val propertyToBuy : LiveData<MarsProperty> = _propertyToBuy

    private val _statePropertyValid : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val statePropertyValid : LiveData<Result<Nothing>> = _statePropertyValid


    val paymentOption : LiveData<PaymentOption> = MutableLiveData(paymentOption)


    private val _transactionState : MutableLiveData<Result<Nothing>> = MutableLiveData()
    val transactionState : LiveData<Result<Nothing>> = _transactionState

    private val _transactionCompleted = MutableLiveData<Event<MarsProperty>>()
    val transactionCompleted: LiveData<Event<MarsProperty>> = _transactionCompleted


    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome



    init {
        fetchProperty()
    }


    private fun fetchProperty() {
        _statePropertyValid.value = Result.Loading()

        viewModelScope.launch {
            try {
                _propertyToBuy.postValue(repository.getProperty(propertyId))
                _statePropertyValid.postValue(Result.Success())

            } catch (e: Exception) {
                _statePropertyValid.postValue(Result.Error())
            }
        }
    }

    fun confirmTransaction() {
        _transactionState.value = Result.Loading()

        viewModelScope.launch {
            try {
                val property = propertyToBuy.getValueNotNull()
//            val paymentOption = paymentOption.getValueNotNull()

                Log.d(RecapPaymentViewModel::class.toString(), "Property bought : ${property.id}")
                _transactionState.postValue(Result.Success())
                _transactionCompleted.postValue(Event(property))
                _navigateToHome.postValue(Event(true))
            }
            catch(e: Exception) {
                _transactionState.postValue(Result.Error(e))
            }
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

