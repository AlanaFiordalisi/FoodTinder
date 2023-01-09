package com.foodtinder.features.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtinder.helpers.LocationHelper
import com.foodtinder.model.Cuisine
import com.foodtinder.network.Repository
import kotlinx.coroutines.launch

private const val TAG = "FilterViewModel"
private const val METERS_PER_MILE = 1609.34

class FilterViewModel : ViewModel() {
    private val repository: Repository = Repository.get()
    private val locationHelper: LocationHelper = LocationHelper.get()

    val cuisines: List<Cuisine> = repository.getCuisineList().categories

    val priceRange: LiveData<String> get() = _priceRange
    private val _priceRange = MutableLiveData("")

    val location: LiveData<String> get() = _location
    private val _location = MutableLiveData("")

    val usingCurrentLocation: LiveData<Boolean> get() = _usingCurrentLocation
    private var _usingCurrentLocation = MutableLiveData(false)

    private var latitude: Double? = null
    private var longitude: Double? = null

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

    fun setLocation(input: String) {
        _location.value = input
    }

    fun setUsingCurrentLocation(using: Boolean) {
        _usingCurrentLocation.value = using
    }

    fun setCurrentLocation() {
        locationHelper.getCurrentLocation().addOnSuccessListener { location ->
            Log.d(TAG, "${location?.latitude}, ${location?.longitude}")
            latitude = location?.latitude
            longitude = location?.longitude
        }
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
        if (usingCurrentLocation.value == true) {
            val lat = latitude
            val long = longitude

            if (lat != null && long != null) {
                viewModelScope.launch {
                    val response = repository.getRestaurantsByCoordinates(
                        priceRange.value.orEmpty(),
                        distance.value.orEmpty(),
                        getCuisineAliasesString(),
                        lat,
                        long
                    )
                }
            }
        } else {
            viewModelScope.launch {
                val response = repository.getRestaurantsByAddress(
                    priceRange.value.orEmpty(),
                    distance.value.orEmpty(),
                    getCuisineAliasesString(),
                    location.value.orEmpty()
                )
            }
        }
    }
}