package com.example.marsrealestate.payment.options

import androidx.annotation.StringRes
import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.data.isValidPropertyType
import com.example.marsrealestate.payment.options.VisaCard.Companion.VISA_CARD_NUMBER_LENGTH
import com.example.marsrealestate.payment.options.VisaCard.Companion.VISA_SECRET_CODE_LENGTH
import com.example.marsrealestate.util.*
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.util.*


class PaymentVisaViewModel : ViewModel() {

    val cardNumber : MutableLiveData<String> = MutableLiveData()
    val expirationMonth : MutableLiveData<Int> = MutableLiveData()
    val expirationYear : MutableLiveData<Int> = MutableLiveData()
    val secretCode : MutableLiveData<String> = MutableLiveData()

    val cardNumberErrorStringId = cardNumber.map { cardNumberValidator(it) }
    val expirationMonthErrorStringID = expirationMonth.map {month -> expirationMonthValidator(month)}
    val expirationYearErrorStringID = expirationYear.map {year -> expirationYearValidator(year) }
    val secretCodeErrorStringID = secretCode.map { code -> secretCodeValidator(code) }


    private val _operationValidateCard : MutableLiveData<Result<VisaCard>> = MutableLiveData()
    val operationValidateCard : LiveData<Result<VisaCard>> = _operationValidateCard


    private val _onCardValidated : MutableLiveData<Event<VisaCard>> = MutableLiveData()
    val onCardValidated : LiveData<Event<VisaCard>> = _onCardValidated


    @StringRes
    private fun cardNumberValidator(cardNumber : String) : Int =
        if (!cardNumber.all { c -> c.isDigit() })
            R.string.card_number_only_digits
        else if (cardNumber.length != VISA_CARD_NUMBER_LENGTH)
            R.string.card_number_length
        else
            NO_ERROR

    @StringRes
    private fun expirationMonthValidator(month : Int) : Int =
        if (month !in 1..12)
            R.string.enter_valid_month
        else
            NO_ERROR

    @StringRes
    private fun expirationYearValidator(year : Int) : Int =
        Calendar.getInstance().get(Calendar.YEAR).let { currentYear ->
            if (year < currentYear || year > currentYear + 15)
                R.string.enter_valid_year
            else
                NO_ERROR
        }

    @StringRes
    private fun secretCodeValidator(secretCode : String) : Int =
        if(!secretCode.all { c -> c.isDigit() })
            R.string.secret_code_only_digits
        else if (secretCode.length != VISA_SECRET_CODE_LENGTH)
            R.string.secret_code_length
        else
            NO_ERROR





    fun validateCard() {
        //Will cause the errors to be updated on the UI, useful for empty inputs
        notifyEmptyFields()

        _operationValidateCard.value = Result.Loading()

        try {
            val card =  VisaCard("",
                "",
                cardNumber = cardNumber.getValueNotNull(::cardNumberValidator),
                expirationMonth = expirationMonth.getValueNotNull(::expirationMonthValidator),
                expirationYear = expirationYear.getValueNotNull(::expirationYearValidator),
                secretCode = secretCode.getValueNotNull(::secretCodeValidator))

            viewModelScope.launch {
                _operationValidateCard.postValue(Result.Success(card))
                _onCardValidated.postValue(Event(card))
            }
        }
        catch (e: Exception) {
            _operationValidateCard.postValue(Result.Error(e))
        }
    }


    private fun notifyEmptyFields() {
        cardNumber.postValue(cardNumber.value ?: "")
        expirationYear.postValue(expirationYear.value ?: 0)
        expirationMonth.postValue(expirationMonth.value ?: 0)
        secretCode.postValue(secretCode.value ?: "")
    }
}



class PaymentVisaViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentVisaViewModel() as T
    }
}

//TODO move elsewhere
object Converter {
    @InverseMethod("expirationYearToString")
    @JvmStatic
    fun stringToExpirationYear(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    @JvmStatic
    fun expirationYearToString(value: Int): String  =
        if (value > 0 ) value.toString() else ""



    @InverseMethod("expirationMonthToString")
    @JvmStatic
    fun stringToExpirationMonth(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    @JvmStatic
    fun expirationMonthToString(value: Int): String  =
        if (value > 0 ) String.format("%02d",value) else ""
}