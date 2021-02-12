package com.example.marsrealestate.purchase

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PurchaseViewModel : ViewModel() {

    val email : MutableLiveData<String> = MutableLiveData<String>()
    val password : MutableLiveData<String> = MutableLiveData<String>()


//    val firstName : MutableLiveData<String> = MutableLiveData<String>()
//    val lastName : MutableLiveData<String> = MutableLiveData<String>()
//    val birthday : MutableLiveData<String> = MutableLiveData<String>()
//    val allowAdvertisment : MutableLiveData<String> = MutableLiveData<String>()



    fun confirm() {
        val a = email.value

    }
}
