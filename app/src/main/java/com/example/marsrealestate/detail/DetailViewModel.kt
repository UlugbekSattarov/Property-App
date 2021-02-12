package com.example.marsrealestate.detail

import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.FavoriteProperty
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.launch

class DetailViewModel(private val prop : MarsProperty, private val repository: MarsRepository) : ViewModel() {

    enum class Operation { ADD, REMOVE }

    private val _property = MutableLiveData<MarsProperty>(prop)
    val property : LiveData<MarsProperty> = _property


    private val _propertyAddedRemovedToFavorites = MutableLiveData<Result<MarsProperty>>()
    val propertyAddedRemovedToFavorites : LiveData<Result<MarsProperty>> = _propertyAddedRemovedToFavorites

    val isPropertyFavorite : LiveData<Boolean> = repository.observeIsFavorite(prop.id)

    private val _navigateToPayment = MutableLiveData<Event<MarsProperty>>()
    val navigateToPayment: LiveData<Event<MarsProperty>> = _navigateToPayment




    fun addRemovePropertyToFavorites() {
        viewModelScope.launch {
            if (repository.isFavorite(prop.id))
                doOperationOnProperty(Operation.REMOVE)
            else
                doOperationOnProperty(Operation.ADD)
        }
    }


    private suspend fun doOperationOnProperty(op : Operation){
        try {
            val msgId = if (op == Operation.ADD) {
                repository.saveToFavorite(prop)
                R.string.property_added_favorites
            }
            else {
                repository.removeFromFavorite(prop.id)
                R.string.property_removed_favorites
            }

            _propertyAddedRemovedToFavorites.value =  Result.Success(prop,msgId)
        }
        catch (e : Exception) {
            val msgId = if (op == Operation.ADD) R.string.property_not_added_favorites_error
                        else R.string.property_not_removed_favorites_error
            _propertyAddedRemovedToFavorites.value =  Result.Error(e,msgId)
        }
    }

    fun navigateToPayment() {
        _navigateToPayment.value = Event(prop)
    }

}


class DetailViewModelFactory(private val property : MarsProperty, private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(property,repository) as T
    }
}