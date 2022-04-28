package com.example.drill2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    companion object {

    }

    val address = MutableLiveData<String>()
    val addressPublic : LiveData<String> get() =  address
    fun setAddress(value:String) {
        address.value = value
    }
}