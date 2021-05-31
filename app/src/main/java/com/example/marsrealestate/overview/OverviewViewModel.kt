package com.example.marsrealestate.overview

import android.util.Log
import androidx.lifecycle.*
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.query.MarsApiFilter
import com.example.marsrealestate.data.query.MarsApiQuery
import com.example.marsrealestate.data.query.MarsApiSorting
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.getValueNotNull
import kotlinx.coroutines.*
import java.lang.Exception


class OverviewViewModel(private val repository : MarsRepository) : ViewModel() {

    private val _status = MutableLiveData<Result<Nothing>>()
    val status: LiveData<Result<Nothing>> = _status


    val type = MutableLiveData(MarsApiFilter.MarsPropertyType.ALL)
    val queryString = MutableLiveData("")
    val sortedBy = MutableLiveData(MarsApiSorting.Default)


    private val _properties = MediatorLiveData<MutableList<MarsProperty>>().apply {
        value = mutableListOf()

        //Ugly but necessary because otherwise the loadNextPage function is triggered too often, notably three times
        // on startup because there are 3 default properties
        var ignoreFirstFilterChange = true
        var ignoreFirstQueryStringChange = true
        var ignoreFirstSortedByFilterChange = true

        addSource(type) { if (ignoreFirstFilterChange) ignoreFirstFilterChange = false else makeNewSearch() }
        addSource(queryString) { if (ignoreFirstQueryStringChange) ignoreFirstQueryStringChange = false else makeNewSearch() }
        addSource(sortedBy) { if (ignoreFirstSortedByFilterChange) ignoreFirstSortedByFilterChange = false else makeNewSearch() }
    }

    val properties : LiveData<List<MarsProperty>> = _properties.map { it }


    private val _endOfData = MutableLiveData(false)
    val endOfData = _endOfData.distinctUntilChanged()

    private val _navigateToProperty = MutableLiveData<Event<MarsProperty>>()
    val navigateToProperty: LiveData<Event<MarsProperty>> = _navigateToProperty

    val itemsPerPage = 7
    private var pageCount = 0


    init {
        makeNewSearch()
    }


    fun updateQueryString(str : String?) {
        if (queryString.value != str) {
            queryString.value = str ?: ""
        }
    }

    fun clearQueryString() = updateQueryString("")


    fun makeNewSearch() {
        pageCount = 0
        loadNextPage()
    }


    fun loadNextPage() {
        _status.postValue(Result.Loading())
        Log.d("############","loadNextPage()")

        try {
            viewModelScope.launch {
                val nextPageToLoad = pageCount + 1

                val newProps = repository.getProperties(MarsApiQuery(
                    pageNumber = nextPageToLoad,
                    itemsPerPage = itemsPerPage,
                    filter = MarsApiFilter(type.getValueNotNull(),queryString.getValueNotNull()),
                    sortedBy = sortedBy.getValueNotNull()
                ))

                //Replacing the previous list in case of new search
                if (nextPageToLoad == 1)
                    _properties.getValueNotNull().clear()

                _properties.getValueNotNull().addAll(newProps)
                _properties.postValue(_properties.value)

                //If we got new properties, we successfully loaded the page, so the page number increases
                if (newProps.isNotEmpty())
                    pageCount = nextPageToLoad

                //To notify the UI that there is no more properties matching the filters
                _endOfData.postValue(newProps.isEmpty())

                _status.postValue(Result.Success())
            }
        }

        catch (e : Exception) {
//                _properties.postValue(mutableListOf())
            _status.postValue(Result.Error())
            Log.e(this@OverviewViewModel::class.simpleName,Log.getStackTraceString(e))
        }

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