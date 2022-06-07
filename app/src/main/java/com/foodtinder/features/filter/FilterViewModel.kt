package com.foodtinder.features.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {

    val distance: LiveData<Int> get() = _distance
    private val _distance = MutableLiveData<Int>()

    val priceRange: LiveData<String> get() = _priceRange
    private val _priceRange = MutableLiveData<String>()

    val cuisineCategories: LiveData<List<String>> get() = _cuisineCategories
    private val _cuisineCategories = MutableLiveData<List<String>>()

    fun onClickSearch(
//        priceRange: String,
        distance: Float,
//        cuisines: List<String>
    ) {

    }
}