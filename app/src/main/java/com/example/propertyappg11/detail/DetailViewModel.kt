package com.example.propertyappg11.detail

import android.util.Log
import androidx.lifecycle.*
import com.example.propertyappg11.R
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.data.MarsRepository
import com.example.propertyappg11.util.Event
import com.example.propertyappg11.util.Result
import com.example.propertyappg11.util.getValueNotNull
import kotlinx.coroutines.launch

class DetailViewModel private constructor(private val repository : MarsRepository) : ViewModel() {

    constructor(property : MarsProperty,repository: MarsRepository) : this(repository) {
        _property.value = property
        _statePropertyFetched.value = Result.Success()
    }

    constructor(propertyId : String,repository: MarsRepository) : this(repository) {
        fetchProperty(propertyId)
    }


    private val _property = MutableLiveData<MarsProperty>()
    val property : LiveData<MarsProperty> = _property

    private val _statePropertyFetched = MutableLiveData<Result<Nothing>>()
    val statePropertyFetched : LiveData<Result<Nothing>> = _statePropertyFetched

    private val _propertyViewCount = MutableLiveData((0..3).random())
    val propertyViewCount : LiveData<Int> = _propertyViewCount

    private val _propertyAddedRemovedToFavorites = MutableLiveData<Result<MarsProperty>>()
    val propertyAddedRemovedToFavorites : LiveData<Result<MarsProperty>> = _propertyAddedRemovedToFavorites

    val isPropertyFavorite : LiveData<Boolean> = property
        .switchMap{ prop->
            repository.observeIsFavorite(prop.id)
        }

    private val _navigateToPayment = MutableLiveData<Event<MarsProperty?>>()
    val navigateToPayment: LiveData<Event<MarsProperty?>> = _navigateToPayment

    private val _shareProperty = MutableLiveData<Event<MarsProperty?>>()
    val shareProperty: LiveData<Event<MarsProperty?>> = _shareProperty



    private fun fetchProperty(propertyId : String) {
        _statePropertyFetched.value = Result.Loading()

        viewModelScope.launch {
            try {
                repository.getProperty(propertyId).let { prop ->
                    if (prop != null) {
                        _property.value = prop
                        _statePropertyFetched.value = Result.Success()
                    } else {
                        _statePropertyFetched.value = Result.Error()
                    }
                }
            } catch (e: Exception) {
                _statePropertyFetched.postValue(Result.Error())
            }
        }
    }



    fun addRemovePropertyToFavorites() {
        viewModelScope.launch {
            try {
                val prop = property.getValueNotNull()
                val msg = if (repository.isFavorite(prop.id)) {
                    repository.removeFromFavorite(prop.id)
                    R.string.property_removed_favorites
                } else {
                    repository.saveToFavorite(prop)
                    R.string.property_added_favorites
                }
                _propertyAddedRemovedToFavorites.value = Result.Success(prop, msg)

            } catch (e: Exception) {
                Log.d(DetailViewModel::class.qualifiedName, "addRemovePropertyToFavorites() : $e")
                _propertyAddedRemovedToFavorites.value = Result.Error(e)
            }
        }
    }



    fun navigateToPayment() {
        try {
            _navigateToPayment.value = Event(property.getValueNotNull())

        } catch (e: Exception) {
            Log.d(DetailViewModel::class.qualifiedName,"navigateToPayment() : $e")
        }
    }


    fun shareProperty() {
        try {
            _shareProperty.value = Event(property.getValueNotNull())

        } catch (e: Exception) {
            Log.d(DetailViewModel::class.qualifiedName,"shareProperty() : $e")
        }
    }

}


class DetailViewModelFactory private constructor(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    private var property : MarsProperty? = null
    private var propertyId : String? = null

    constructor(property : MarsProperty,repository: MarsRepository) : this(repository) {
        this.property = property
    }

    constructor(propertyId : String,repository: MarsRepository) : this(repository) {
        this.propertyId = propertyId
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val p = property

        return if (p != null)
            DetailViewModel(p,repository) as T
        else
            DetailViewModel(propertyId!!,repository) as T

        //It is safe to use !! because if property is null, then the other constructor of the factory
        //has been used which means propertyId is not null
    }
}
