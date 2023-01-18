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

    val cuisineOptions: List<Cuisine> = repository.getCuisineList().categories

    val priceRange: LiveData<String> get() = _priceRange
    private val _priceRange = MutableLiveData("")

    val location: LiveData<String> get() = _location
    private val _location = MutableLiveData("")

    val usingCurrentLocation: LiveData<Boolean> get() = _usingCurrentLocation
    private var _usingCurrentLocation = MutableLiveData(false)

    val settingCurrentLocation: LiveData<Boolean> get() = _settingCurrentLocation
    private var _settingCurrentLocation = MutableLiveData(false)

    private var latitude: Double? = null
    private var longitude: Double? = null

    val distance: LiveData<String> get() = _distance
    private val _distance = MutableLiveData("")

    val cuisines: LiveData<MutableSet<String>> get() = _cuisines
    private val _cuisines: MutableLiveData<MutableSet<String>> =
        MutableLiveData(mutableSetOf())

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
        _settingCurrentLocation.value = true
        locationHelper.getCurrentLocation().addOnSuccessListener { location ->
            Log.d(TAG, "${location?.latitude}, ${location?.longitude}")
            latitude = location?.latitude
            longitude = location?.longitude
            _settingCurrentLocation.value = false
        }
    }

    fun resetCurrentLocation() {
        latitude = null
        longitude = null
    }

    fun setDistance(input: Float) {
        _distance.value = (input * METERS_PER_MILE).toInt().toString()
    }

    fun addCuisine(cuisine: String) = _cuisines.value?.add(cuisine)
    fun removeCuisine(cuisine: String) = _cuisines.value?.remove(cuisine)
    private fun getCuisineAliasesString() = cuisines.value.toString()
        .filterNot { it.isWhitespace() }
        .trim('[', ']')

    fun onClickSearch() {
        Log.d(TAG, "--------search!-------")
        Log.d(TAG, "Price Range: ${priceRange.value}")
        Log.d(TAG, "Using Current Location: ${usingCurrentLocation.value}")
        Log.d(TAG, "Distance: ${distance.value}")
        Log.d(TAG, "Cuisines: ${getCuisineAliasesString()}")

        if (usingCurrentLocation.value == true) {
            val lat = latitude
            val long = longitude

            Log.d(TAG, "Lat/Long: $lat/$long")
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
            Log.d(TAG, "Address: ${location.value}")
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

    fun locationEntryValid() = !location.value.isNullOrEmpty() || (latitude != null && longitude != null)
}