package com.example.propertyappg11.util

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputLayout

object FormValidation {
    const val NO_ERROR = -1

    fun isSuccess(stringId : Int) = stringId == NO_ERROR

    fun isError(stringId : Int) = ! isSuccess(stringId)

}


fun Int.isValidationError() = FormValidation.isError(this)
fun Int.isValidationSuccess() = FormValidation.isError(this)

fun LiveData<Int>.isValidationSuccess() = FormValidation.isSuccess(value!!)
fun LiveData<Int>.isValidationError() = FormValidation.isError(value!!)




@BindingAdapter("errorMessage" )
fun TextInputLayout.errorMessage(@StringRes errorStringId: Int?) {
    isErrorEnabled =  errorStringId != null && errorStringId.isValidationError()
    if (isErrorEnabled)
        this.error = resources.getString(errorStringId!!)
}



