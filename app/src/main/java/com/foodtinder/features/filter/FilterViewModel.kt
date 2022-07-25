package com.foodtinder.features.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodtinder.model.Cuisine
import com.foodtinder.network.ApiService
import com.foodtinder.network.Repository
import com.foodtinder.network.model.RestaurantSearchResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FilterViewModel"

class FilterViewModel : ViewModel() {

    val priceRange: LiveData<String> get() = _priceRange
    private val _priceRange = MutableLiveData("")

    val distance: LiveData<String> get() = _distance
    private val _distance = MutableLiveData("")

    val cuisineAliases: LiveData<MutableList<String>> get() = _cuisineAliases
    private val _cuisineAliases: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())

    //    private var cuisineMap: Map<String, Any> = HashMap()
    var cuisines: List<Cuisine> = listOf()
//    private lateinit var cuisineList: CuisineList

    init {
        cuisines = Repository.get().getCuisineList().categories

        // in repository use application context to read file and
//        getAssets().open("./cuisines.json").bufferedReader().use { it.readText() }
//        val jsonString: String = File("./cuisines.json").readText(Charsets.UTF_8)
//        cuisineList = Gson().fromJson(jsonString, CuisineList::class.java)
//        Log.d(TAG, "$cuisineList")
//        Log.d(TAG, "${cuisineList.keys}")
    }

    val logText: LiveData<String> get() = _logText
    private val _logText = MutableLiveData<String>()

    fun setPriceRange(input: Float) {
        var priceRange = ""
        for (idx in 1..input.toInt()) {
            priceRange += "$idx,"
        }
        _priceRange.value = priceRange.trim(',')
    }

    fun setDistance(input: Float) {
        _distance.value = (input * 1609.34).toInt().toString()
    }

    fun addCuisine(cuisine: String) = _cuisineAliases.value?.add(cuisine)
    fun removeCuisine(cuisine: String) = _cuisineAliases.value?.remove(cuisine)

    // TODO: handle when user provides no input
    private fun getCuisinesAsString() = cuisineAliases.value?.reduce { acc, s ->
        "$acc, $s"
    }

    fun onClickSearch() {
        Log.d(TAG, "distance: ${distance.value}")
        Log.d(TAG, "price range: ${priceRange.value}")
        Log.d(TAG, "cuisines: ${cuisineAliases.value}")
        Log.d(TAG, "cuisines: ${getCuisinesAsString()}")

        // TODO move this outta here
        val service = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        var response: RestaurantSearchResponse

        CoroutineScope(Dispatchers.IO).launch {
            response = service.search(
                token = "Bearer FxhnaEnu31yrqfGl1XcyyH_mjgR7NuII6gJLvV2zj_fxVFlmsbY4od73X52AwXMBATyJtrHkp3C6cWuMMTLt6VEdbkc2E4ea9vS5AkcSFe5d7ELg7L8ipTunEDfXYXYx",
                mapOf(
                    "location" to "515 S Mangum St, Durham, NC 27701",
                    "radius" to distance.value.orEmpty(),
                    "price" to priceRange.value.orEmpty(),
                    "categories" to getCuisinesAsString().orEmpty()
                )
            )
            Log.d(TAG, "response total: ${response.total}")
            Log.d(TAG, "num businesses: ${response.businesses.size}")
        }

        _logText.value = "radius: ${distance.value} meters\n" +
                "price range: ${priceRange.value}\n" +
                "cuisines: ${cuisineAliases.value}\n"
    }
}