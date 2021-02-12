package com.example.marsrealestate.payment.options

import androidx.databinding.InverseMethod
import androidx.lifecycle.*
import com.example.marsrealestate.R
import com.example.marsrealestate.payment.options.VisaCard.Companion.VISA_CARD_NUMBER_LENGTH
import com.example.marsrealestate.payment.options.VisaCard.Companion.VISA_SECRET_CODE_LENGTH
import com.example.marsrealestate.util.*
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.util.*


class PaymentVisaViewModel : ViewModel() {

    val cardNumber : MutableLiveData<String> = MutableLiveData()
    val expirationMonth : MutableLiveData<Int> = MutableLiveData()
    val expirationYear : MutableLiveData<Int> = MutableLiveData()
    val secretCode : MutableLiveData<String> = MutableLiveData()


    val cardNumberErrorStringId = cardNumber.map {
        if(!it.all { c -> c.isDigit() })
            R.string.card_number_only_digits
        else if (it.length != VISA_CARD_NUMBER_LENGTH)
            R.string.card_number_length
        else
            NO_ERROR
    }


    val expirationMonthErrorStringID = expirationMonth.map {month ->
        if (month !in 1..12)
            R.string.enter_valid_month
        else
            NO_ERROR
    }

    val expirationYearErrorStringID = expirationYear.map {year ->
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year < currentYear || year > currentYear + 15)
            R.string.enter_valid_year
        else
            NO_ERROR
    }

    val secretCodeErrorStringID = secretCode.map {
        if(!it.all { c -> c.isDigit() })
            R.string.secret_code_only_digits
        else if (it.length != VISA_SECRET_CODE_LENGTH)
            R.string.secret_code_length
        else
            NO_ERROR
    }


    private val _operationValidateCard : MutableLiveData<Result<VisaCard>> = MutableLiveData()
    val operationValidateCard : LiveData<Result<VisaCard>> = _operationValidateCard


    private val _onCardValidated : MutableLiveData<Event<VisaCard>> = MutableLiveData()
    val onCardValidated : LiveData<Event<VisaCard>> = _onCardValidated


    fun validateCard() {
        //Will cause the errors to be updated on the UI, if any
        cardNumber.postValue(cardNumber.value ?: "")
        expirationYear.postValue(expirationYear.value ?: 0)
        expirationMonth.postValue(expirationMonth.value ?: 0)
        secretCode.postValue(secretCode.value ?: "")

        val card = getVisaCard()

        if (cardNumberErrorStringId.isError()
            || expirationMonthErrorStringID.isError()
            || expirationYearErrorStringID.isError()
            || secretCodeErrorStringID.isError()
            || card == null
        )
            return

            _operationValidateCard.value = Result.Loading()
            viewModelScope.launch {
                delay(1000)
                _operationValidateCard.postValue(Result.Success(card))
                _onCardValidated.postValue(Event(card))
            }

    }


    private fun getVisaCard() : VisaCard? {
        val cardNumber = cardNumber.value ?: return null
        val expirationMonth = expirationMonth.value ?: return null
        val expirationYear = expirationYear.value ?: return null
        val secretCode = secretCode.value ?: return null

        return VisaCard("","",cardNumber,expirationMonth,expirationYear,secretCode)
    }
}



class PaymentVisaViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentVisaViewModel() as T
    }
}


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