package com.example.marsrealestate.overview

import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.network.MarsApiFilter
import com.example.marsrealestate.data.network.MarsApiPropertySorting
import com.example.marsrealestate.data.network.MarsApiQuery
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.Collections.addAll


class OverviewViewModel(private val repository : MarsRepository) : ViewModel() {

    private val _status = MutableLiveData<Result<Nothing>>()
    val status: LiveData<Result<Nothing>> = _status

    private val _endOfData = MutableLiveData<Boolean>(false)
    val endOfData = _endOfData

    private val _navigateToProperty = MutableLiveData<Event<MarsProperty>>()
    val navigateToProperty: LiveData<Event<MarsProperty>> = _navigateToProperty


    val filter = MutableLiveData<MarsApiFilter.MarsPropertyType>()
    val queryString = MutableLiveData<String>()
    val sortedBy = MutableLiveData<MarsApiPropertySorting>()


    private val _properties = MediatorLiveData<MutableList<MarsProperty>>().apply {
        value = mutableListOf()

        addSource(filter) { loadNextPage(true) }
        addSource(queryString) { loadNextPage(true) }
        addSource(sortedBy) { loadNextPage(true) }
    }

    val properties : LiveData<List<MarsProperty>> = _properties.map { it }


    private val itemsPerPage = 7
    private var pageLoadedCount = 0


    init {
        //This will trigger loadNextPage()
        filter.value = MarsApiFilter.MarsPropertyType.ALL
    }



    /**
     * To be used from xml, will trigger [loadNextPage] with reset = true
     *
     */
    fun updateQueryString(str : String?) {
        if (queryString.value != str)
            queryString.value = str
    }

    fun clearQueryStringAndUpdate() = updateQueryString(null)


    fun loadNextPage(reset : Boolean = false) {
        _status.postValue(Result.Loading())
        viewModelScope.launch {

            try {
                val pageToLoad = if (reset) 1 else pageLoadedCount + 1

                val newProps = requestNewPropertiesFromRepo(pageToLoad,itemsPerPage,sortedBy.value ?: MarsApiPropertySorting.PriceAscending)

                if (reset) {
                    //Replacing the previous list in case of reset
                    _properties.postValue(newProps.toMutableList())
                }
                else if (newProps.isNotEmpty()) {
                    //Adding new elements if no reset
                    val a = _properties.value?.apply { addAll(newProps) }
                    _properties.postValue(a)
                }


                if (_endOfData.value != newProps.isEmpty())
                    _endOfData.postValue(newProps.isEmpty())

                //If we got new properties, we successfully loaded the page, so the page number increases
                if (newProps.isNotEmpty())
                    pageLoadedCount = pageToLoad

                _status.postValue(Result.Success())
            }

            catch (e : Exception) {
                _status.postValue(Result.Error())
//                _properties.postValue(mutableListOf())
                Log.e(this@OverviewViewModel::class.simpleName,Log.getStackTraceString(e))
            }
        }
    }

    private suspend fun requestNewPropertiesFromRepo(pageToLoad: Int, itemsPerPage: Int, sorting: MarsApiPropertySorting = MarsApiPropertySorting.PriceAscending) : List<MarsProperty> {
        val query = MarsApiQuery(
            pageToLoad, itemsPerPage,
            MarsApiFilter(
                filter.value ?: MarsApiFilter.MarsPropertyType.ALL,
                queryString.value ?: ""))

        return repository.getProperties(query,sorting )
    }

    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToProperty.value = Event(marsProperty)
    }

}





class OverviewViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OverviewViewModel(repository) as T
    }
}


//
//object SortingConverter {
//    @InverseMethod("expirationYearToString")
//    @JvmStatic
//    fun stringToSorting(value: String): Int {
//        return try {
//            val a = if (value == MarsApiPropertySorting.PriceAscending) R.string.priceAscending else R.string.priceDescending
//            return context.getString(a)
//        } catch (e: NumberFormatException) {
//            -1
//        }
//    }
//
//    @JvmStatic
//    fun sortingToString(value: MarsApiPropertySorting): String =
//        if (value > 0) value.toString() else ""
//
//}