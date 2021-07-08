package com.example.marsrealestate.sell.completed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsrealestate.util.Event


class SellCompletedViewModel(private val propertyId: String) : ViewModel() {


    private val _navigateToProperty = MutableLiveData<Event<String>>()
    val navigateToProperty: LiveData<Event<String>> = _navigateToProperty


    private val _navigateToOverview = MutableLiveData<Event<Boolean>>()
    val navigateToOverview: LiveData<Event<Boolean>> = _navigateToOverview



    fun navigateToProperty() {
        _navigateToProperty.value = Event(propertyId)
    }

    fun navigateToOverview() {
        _navigateToOverview.value = Event(true)
    }

}


class SellCompletedViewModelFactory(private val propertyId: String) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellCompletedViewModel(propertyId) as T
    }
}

