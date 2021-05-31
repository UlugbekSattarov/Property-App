package com.example.marsrealestate.favorites

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.marsrealestate.R
import com.example.marsrealestate.data.FavoriteProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.launch
import java.lang.Exception


class FavoritesViewModel(private val repository : MarsRepository) : ViewModel() {

    val favorites: LiveData<List<FavoriteProperty>> = repository.observeFavorites()

    private val _navigateToProperty = MutableLiveData<Event<FavoriteProperty>>()
    val navigateToProperty: LiveData<Event<FavoriteProperty>> = _navigateToProperty

    private var lastRemovedProperty : FavoriteProperty? = null

    private val _propertyRecovered = MutableLiveData<Result<FavoriteProperty>>()
    val propertyRecovered: LiveData<Result<FavoriteProperty>> = _propertyRecovered

    private val _propertyRemoved = MutableLiveData<Result<FavoriteProperty>>()
    val propertyRemoved: LiveData<Result<FavoriteProperty>> = _propertyRemoved



    fun displayPropertyDetails(favorite: FavoriteProperty) {
        _navigateToProperty.value = Event(favorite)
    }


    fun removePropertyFromFavorites(favorite: FavoriteProperty){
        try {
            viewModelScope.launch {
                repository.removeFromFavorite(favorite.property.id)
                lastRemovedProperty = favorite
                _propertyRemoved.postValue(Result.Success(data = favorite))
            }
        }
        catch (e: Exception) {
            _propertyRemoved.postValue(Result.Error(exception = e))
        }
    }


    fun recoverLastDeletedProperty() {
        try {
            val toRecover = lastRemovedProperty ?: throw Exception()

            viewModelScope.launch {
                repository.saveToFavorite(toRecover.property,toRecover.favorite.dateFavorited)
                lastRemovedProperty = null
                _propertyRecovered.postValue(Result.Success(data = toRecover))
            }
        }
        catch (e: Exception) {
            _propertyRecovered.postValue(Result.Error(exception = e))
        }
    }
}


class FavoritesViewModelFactory(private val repository: MarsRepository) :  ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}