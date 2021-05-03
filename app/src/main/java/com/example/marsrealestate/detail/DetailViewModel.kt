package com.example.marsrealestate.detail

import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.FavoriteProperty
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.launch

class DetailViewModel private constructor(private val repository : MarsRepository) : ViewModel() {


    constructor(property : MarsProperty,repository: MarsRepository) : this(repository) {
        _property.value = property
    }

    constructor(propertyId : String?,repository: MarsRepository) : this(repository) {
        if (propertyId != null) {
            viewModelScope.launch {
                repository.getProperty(propertyId)?.let {
                    _property.postValue(it)
                }
            }
        }
    }


    enum class Operation { ADD, REMOVE }

    private val _property = MutableLiveData<MarsProperty>(MarsProperty.DEFAULT)
    val property : LiveData<MarsProperty> = _property

    private val _propertyViewCount = MutableLiveData<Int>((0..3).random())
    val propertyViewCount : LiveData<Int> = _propertyViewCount

    private val _propertyAddedRemovedToFavorites = MutableLiveData<Result<MarsProperty>>()
    val propertyAddedRemovedToFavorites : LiveData<Result<MarsProperty>> = _propertyAddedRemovedToFavorites

    val isPropertyFavorite : LiveData<Boolean> = property
        .switchMap{ prop->
            if (prop == MarsProperty.DEFAULT) MutableLiveData(false)
            else repository.observeIsFavorite(prop.id)
        }

    private val _navigateToPayment = MutableLiveData<Event<MarsProperty?>>()
    val navigateToPayment: LiveData<Event<MarsProperty?>> = _navigateToPayment

    private val _shareProperty = MutableLiveData<Event<MarsProperty?>>()
    val shareProperty: LiveData<Event<MarsProperty?>> = _shareProperty




    fun addRemovePropertyToFavorites(prop : MarsProperty) {
        if (property.value == null || property.value == MarsProperty.DEFAULT)
            return

        viewModelScope.launch {
            if (repository.isFavorite(prop.id))
                doOperationOnProperty(prop,Operation.REMOVE)
            else
                doOperationOnProperty(prop,Operation.ADD)
        }
    }


    private suspend fun doOperationOnProperty(prop:MarsProperty, op : Operation){
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
        if (property.value != null && property.value != MarsProperty.DEFAULT)
            _navigateToPayment.value = Event(property.value)
    }

    fun shareProperty() {
        if (property.value != null && property.value != MarsProperty.DEFAULT)
            _shareProperty.value = Event(property.value)
    }

}


class DetailViewModelFactory private constructor(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    private var property : MarsProperty? = null
    private var propertyId : String? = null

    constructor(property : MarsProperty,repository: MarsRepository) : this(repository) {
        this.property = property
    }

    constructor(propertyId : String?,repository: MarsRepository) : this(repository) {
        this.propertyId = propertyId
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val p = property

        return if (p != null)
            DetailViewModel(p,repository) as T
        else
            DetailViewModel(propertyId,repository) as T
    }
}