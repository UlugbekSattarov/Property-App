package com.example.marsrealestate.sell

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SellViewModel(private val repository: MarsRepository) : ViewModel() {

    private val _status = MutableLiveData<Result<Nothing>>()
    val status: LiveData<Result<Nothing>> = _status


    fun putPropertyToSale() {
        val prop = MarsProperty.DEFAULT
        viewModelScope.launch {
            repository.addProperty(prop)
        }
    }


}


class SellViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellViewModel(repository) as T
    }
}