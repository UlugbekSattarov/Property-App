package com.example.propertyappg11.navigation

import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.example.propertyappg11.data.PropRepository
import com.example.propertyappg11.util.*


class NavigationViewModel( repo : PropRepository) : ViewModel() {

    private val _currentDestinationId : MutableLiveData<Int> = MutableLiveData()
    val currentDestinationId : LiveData<Int> = _currentDestinationId

    val favoritesCount : LiveData<Int> = repo.observeFavorites().map { it.count() }




    fun setCurrentDestination(@IdRes destinationId : Int) {
        _currentDestinationId.value = destinationId
    }



}



class NavigationViewModelFactory(private val repo : PropRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NavigationViewModel(repo) as T
    }
}
