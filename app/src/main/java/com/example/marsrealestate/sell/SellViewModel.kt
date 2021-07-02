package com.example.marsrealestate.sell

import androidx.annotation.StringRes
import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.isValidPropertyType
import com.example.marsrealestate.util.Event
import com.example.marsrealestate.util.FormValidation
import com.example.marsrealestate.util.Result
import com.example.marsrealestate.util.getValueNotNull
import kotlinx.coroutines.launch
import java.lang.Exception


class SellViewModel(private val repository: MarsRepository) : ViewModel() {

    private val _status = MutableLiveData<Result<Nothing>>(Result.Success())
    val status: LiveData<Result<Nothing>> = _status


    val type : MutableLiveData<String> = MutableLiveData(MarsProperty.TYPE_BUY)
    val imgSrcUrl : MutableLiveData<String> = MutableLiveData()
    val price : MutableLiveData<Int> = MutableLiveData(0)
    val latitude : MutableLiveData<Float> = MutableLiveData(0f)
    val longitude : MutableLiveData<Float> = MutableLiveData(0f)
    val area : MutableLiveData<Float> = MutableLiveData(0f)

    val isRental = type.map { it == MarsProperty.TYPE_RENT }

    private val _navigateToProperty = MutableLiveData<Event<MarsProperty>>()
    val navigateToProperty: LiveData<Event<MarsProperty>> = _navigateToProperty


    @StringRes
    private fun typeValidator(type : String) : Int {
        if ( ! type.isValidPropertyType()) return R.string.error
        return FormValidation.NO_ERROR
    }

    @StringRes
    private fun priceValidator(price : Int) : Int {
        if (price <= 0) return R.string.need_positive_value
        if (price > 100_000_000) return R.string.price_too_high
        return FormValidation.NO_ERROR
    }


    @StringRes
    private fun latitudeValidator(latitude : Float) : Int {
        if ( latitude < -90 || latitude > 90) return R.string.error
        return FormValidation.NO_ERROR
    }

    @StringRes
    private fun longitudeValidator(longitude : Float) : Int {
        if ( longitude < 0 || longitude > 360) return R.string.error
        return FormValidation.NO_ERROR
    }

    @StringRes
    private fun areaValidator(area : Float) : Int {
        if ( area < 0 || area > 100_000) return R.string.error
        return FormValidation.NO_ERROR
    }



    fun putPropertyToSale() {
        _status.value = Result.Loading()

        viewModelScope.launch {
            try {

                val newProperty = MarsProperty(
                    "",
                    type = type.getValueNotNull(::typeValidator),
                    imgSrcUrl = imgSrcUrl.getValueNotNull(),
                    price = price.getValueNotNull(::priceValidator).toDouble(),
                    surfaceArea = area.getValueNotNull(::areaValidator),
                    latitude = latitude.getValueNotNull(::latitudeValidator),
                    longitude = longitude.getValueNotNull(::longitudeValidator)
                )

                repository.addProperty(newProperty).also {
                    repository.saveToFavorite(it)
                    _navigateToProperty.value = Event(it)
                }


                _status.postValue(Result.Success())

            } catch (e: Exception) {
                _status.postValue(Result.Error(exception = e))
            }
        }
    }

}


class SellViewModelFactory(private val repository: MarsRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellViewModel(repository) as T
    }
}

//TODO move elsewhere
object SellConverter {


    @InverseMethod("intToString")
    @JvmStatic
    fun stringToInt(value: String): Int = value.toIntOrNull() ?: 0

    @JvmStatic
    fun intToString(value: Int): String  = if (value == 0) "" else value.toString()

}