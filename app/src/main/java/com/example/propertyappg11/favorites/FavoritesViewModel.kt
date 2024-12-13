package com.example.propertyappg11.favorites

import androidx.lifecycle.*
import com.example.propertyappg11.data.FavoriteProperty
import com.example.propertyappg11.data.PropRepository
import com.example.propertyappg11.util.Event
import com.example.propertyappg11.util.Result
import kotlinx.coroutines.launch


class FavoritesViewModel(private val repository : PropRepository) : ViewModel() {

    val favorites: LiveData<List<FavoriteProperty>> = repository.observeFavorites()


    private val _navigateToProperty = MutableLiveData<Event<FavoriteProperty>>()
    val navigateToProperty: LiveData<Event<FavoriteProperty>> = _navigateToProperty

    private val _navigateToOverview = MutableLiveData<Event<Boolean>>()
    val navigateToOverview: LiveData<Event<Boolean>> = _navigateToOverview


    private var lastRemovedProperty : FavoriteProperty? = null


    private val _propertyRecovered = MutableLiveData<Result<FavoriteProperty>>()
    val propertyRecovered: LiveData<Result<FavoriteProperty>> = _propertyRecovered

    private val _propertyRemoved = MutableLiveData<Result<FavoriteProperty>>()
    val propertyRemoved: LiveData<Result<FavoriteProperty>> = _propertyRemoved



    fun displayPropertyDetails(favorite: FavoriteProperty) {
        _navigateToProperty.value = Event(favorite)
    }

    fun navigateToOverview() {
        _navigateToOverview.value = Event(true)
    }


    fun removePropertyFromFavorites(favorite: FavoriteProperty) {
        viewModelScope.launch {
            try {
                repository.removeFromFavorite(favorite.property.id)
                lastRemovedProperty = favorite
                _propertyRemoved.postValue(Result.Success(data = favorite))
            }
            catch(e: Exception) {
                _propertyRemoved.postValue(Result.Error(exception = e))
            }
        }
    }


    fun recoverLastDeletedProperty() {
        val toRecover = lastRemovedProperty ?: return

        viewModelScope.launch {
            try {
                repository.saveToFavorite(toRecover.property, toRecover.favorite.dateFavorited)
                lastRemovedProperty = null
                _propertyRecovered.postValue(Result.Success(data = toRecover))
            } catch (e: Exception) {
                _propertyRecovered.postValue(Result.Error(exception = e))
            }
        }
    }
}


class FavoritesViewModelFactory(private val repository: PropRepository) :  ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}
