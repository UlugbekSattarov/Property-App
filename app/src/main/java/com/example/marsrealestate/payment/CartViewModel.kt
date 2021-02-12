package com.example.marsrealestate.payment

import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.*
import kotlinx.coroutines.launch


class CartViewModel(private val repository: MarsRepository) : ViewModel() {

    private val _propertyToBuyId = MutableLiveData<String>()
    val propertyToBuyId: LiveData<String> = _propertyToBuyId

    val property = propertyToBuyId.switchMap {
        repository.observeProperty(it) }

    val propertyaddedToCart = property.map { if (it == null) Result.Error() else Result.Success(it.id) }



    //Cart can only have one item
    fun addPropertyToBuy(propertyToBuyId : String) {
        _propertyToBuyId.postValue(propertyToBuyId)
    }

}

class CartViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(repository) as T
    }
}
