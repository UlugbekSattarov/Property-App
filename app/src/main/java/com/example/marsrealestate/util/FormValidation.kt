package com.example.marsrealestate.util

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.marsrealestate.util.FormValidation.NO_ERROR
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object FormValidation {
    const val NO_ERROR = -1
}

fun LiveData<Int>.noError() = value == NO_ERROR

fun LiveData<Int>.isError() = value != NO_ERROR


@BindingAdapter("errorMessage" )
fun TextInputLayout.errorMessage(@StringRes errorStringId: Int?) {
    isErrorEnabled = errorStringId != NO_ERROR && errorStringId != null
    if (isErrorEnabled)
        this.error = resources.getString(errorStringId!!)
}



