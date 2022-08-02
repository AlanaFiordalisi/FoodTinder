package com.foodtinder.features.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodtinder.model.Cuisine
import com.foodtinder.network.Repository

private const val METERS_PER_MILE = 1609.34

class FilterViewModel : ViewModel() {
    private val repository: Repository = Repository.get()
    val cuisines: List<Cuisine> = repository.getCuisineList().categories

    val priceRange: LiveData<String> get() = _priceRange
    private val _priceRange = MutableLiveData("")

    val distance: LiveData<String> get() = _distance
    private val _distance = MutableLiveData("")

    val cuisineAliases: LiveData<MutableList<String>> get() = _cuisineAliases
    private val _cuisineAliases: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())

    fun setPriceRange(input: Float) {
        var priceRange = ""
        for (idx in 1..input.toInt()) {
            priceRange += "$idx,"
        }
        _priceRange.value = priceRange.trim(',')
    }

    fun setDistance(input: Float) {
        _distance.value = (input * METERS_PER_MILE).toInt().toString()
    }

    fun addCuisine(cuisine: String) = _cuisineAliases.value?.add(cuisine)
    fun removeCuisine(cuisine: String) = _cuisineAliases.value?.remove(cuisine)
    private fun getCuisineAliasesString() = cuisineAliases.value.toString()
        .filterNot { it.isWhitespace() }
        .trim('[', ']')

    fun onClickSearch() {
        repository.getRestaurants(
            distance.value.orEmpty(),
            priceRange.value.orEmpty(),
            getCuisineAliasesString()
        )
    }
}