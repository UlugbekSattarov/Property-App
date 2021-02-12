package com.example.marsrealestate.navigation

import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.marsrealestate.R
import com.example.marsrealestate.custom.NavigationItemView
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.*
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class NavigationViewModel(@IdRes currentDestinationId : Int, repo : MarsRepository) : ViewModel() {

    private val _currentDestinationId : MutableLiveData<Int> = MutableLiveData(currentDestinationId)
    val currentDestinationId : LiveData<Int> = _currentDestinationId

    val favoritesCount : LiveData<Int> = repo.observeFavorites().map { it.count() }




    fun setCurrentDestination(destinationId : Int) {
        _currentDestinationId.value = destinationId
    }



}



class NavigationViewModelFactory(@IdRes private val currentDestinationId : Int,private val repo : MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NavigationViewModel(currentDestinationId,repo ) as T
    }
}
