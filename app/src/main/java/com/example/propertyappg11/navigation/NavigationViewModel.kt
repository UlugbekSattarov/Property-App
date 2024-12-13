package com.example.propertyappg11.navigation

import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.propertyappg11.R
import com.example.propertyappg11.custom.NavigationItemView
import com.example.propertyappg11.data.MarsRepository
import com.example.propertyappg11.util.*
import com.example.propertyappg11.util.FormValidation.NO_ERROR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class NavigationViewModel( repo : MarsRepository) : ViewModel() {

    private val _currentDestinationId : MutableLiveData<Int> = MutableLiveData()
    val currentDestinationId : LiveData<Int> = _currentDestinationId

    val favoritesCount : LiveData<Int> = repo.observeFavorites().map { it.count() }




    fun setCurrentDestination(@IdRes destinationId : Int) {
        _currentDestinationId.value = destinationId
    }



}



class NavigationViewModelFactory(private val repo : MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NavigationViewModel(repo) as T
    }
}
