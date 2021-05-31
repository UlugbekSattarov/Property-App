package com.example.marsrealestate.payment

import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.*
import kotlinx.coroutines.launch


class CartViewModel(private val repository: MarsRepository) : ViewModel() {

    private val _propertyToBuyId = MutableLiveData<String>()
    val propertyToBuyId: LiveData<String> = _propertyToBuyId

    private val _property = MutableLiveData<MarsProperty>()
    val property = _property


    //Cart can only have one item
    fun addPropertyToBuy(propertyToBuyId : String) {
        if (this.propertyToBuyId.value == propertyToBuyId) return

        try {
            viewModelScope.launch {
                _propertyToBuyId.postValue(propertyToBuyId)
                _property.postValue(repository.getProperty(propertyToBuyId))
            }
        } catch (e: Exception) {
            _propertyToBuyId.value = ""
        }
    }

}

class CartViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(repository) as T
    }
}
