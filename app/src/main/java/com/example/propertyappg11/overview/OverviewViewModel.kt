package com.example.propertyappg11.overview

import android.util.Log
import androidx.lifecycle.*
import com.example.propertyappg11.data.PropRepository
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.data.query.PropApiFilter
import com.example.propertyappg11.data.query.PropApiQuery
import com.example.propertyappg11.data.query.PropApiSorting
import com.example.propertyappg11.util.Event
import com.example.propertyappg11.util.Result
import com.example.propertyappg11.util.getValueNotNull
import kotlinx.coroutines.*
import java.lang.Exception


class OverviewViewModel(private val repository : PropRepository) : ViewModel() {

    private val _status = MutableLiveData<Result<Nothing>>()
    val status: LiveData<Result<Nothing>> = _status


    val type = MutableLiveData(PropApiFilter.PropPropertyType.ALL)
    val queryString = MutableLiveData("")
    val sortedBy = MutableLiveData(PropApiSorting.Default)


    private val _properties = MediatorLiveData<MutableList<PropProperty>>().apply {
        value = mutableListOf()

        var ignoreFirstFilterChange = true
        var ignoreFirstQueryStringChange = true
        var ignoreFirstSortedByFilterChange = true

        addSource(type) { if (ignoreFirstFilterChange) ignoreFirstFilterChange = false else makeNewSearch() }
        addSource(queryString) { if (ignoreFirstQueryStringChange) ignoreFirstQueryStringChange = false else makeNewSearch() }
        addSource(sortedBy) { if (ignoreFirstSortedByFilterChange) ignoreFirstSortedByFilterChange = false else makeNewSearch() }
    }

    val properties : LiveData<List<PropProperty>> = _properties.map { it }


    private val _endOfData = MutableLiveData(false)
    val endOfData = _endOfData.distinctUntilChanged()

    private val _navigateToProperty = MutableLiveData<Event<PropProperty>>()
    val navigateToProperty: LiveData<Event<PropProperty>> = _navigateToProperty


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
        _status.value = Result.Loading()
        Log.d("############","loadNextPage()")

        viewModelScope.launch {
            try {
                val nextPageToLoad = pageCount + 1

                val newProps = repository.getProperties(
                    PropApiQuery(
                        pageNumber = nextPageToLoad,
                        itemsPerPage = itemsPerPage,
                        filter = PropApiFilter(
                            type.getValueNotNull(),
                            queryString.getValueNotNull()
                        ),
                        sortedBy = sortedBy.getValueNotNull()
                    )
                )

                if (nextPageToLoad == 1)
                    _properties.getValueNotNull().clear()

                _properties.getValueNotNull().addAll(newProps)
                _properties.postValue(_properties.value)

                if (newProps.isNotEmpty())
                    pageCount = nextPageToLoad

                _endOfData.postValue(newProps.isEmpty())

                _status.postValue(Result.Success())
            }

            catch(e : Exception) {
                _status.postValue(Result.Error())
                Log.e(this@OverviewViewModel::class.simpleName, Log.getStackTraceString(e))
            }
        }
    }


    fun displayPropertyDetails(propProperty: PropProperty) {
        _navigateToProperty.value = Event(propProperty)
    }

}





class OverviewViewModelFactory(private val repository: PropRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OverviewViewModel(repository) as T
    }
}
