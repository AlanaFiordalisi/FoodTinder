package com.foodtinder.network

import android.content.Context
import android.util.Log
import com.foodtinder.model.CuisineList
import com.foodtinder.network.model.RestaurantSearchResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

private const val TAG = "Repository"

class Repository private constructor(private val context: Context) {

    fun getCuisineList(): CuisineList {
        val jsonString = context.assets.open("cuisines.json").bufferedReader().use {
            it.readText()
        }

        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<CuisineList> = moshi.adapter(CuisineList::class.java)

        val cuisineList = jsonAdapter.fromJson(jsonString)
        return cuisineList!!
    }

    suspend fun getRestaurants(
        priceRange: String,
        distance: String,
        categories: String,
        location: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ) : RestaurantSearchResponse {
        Log.d(TAG, "price range: $priceRange")
        Log.d(TAG, "location: $location")
        Log.d(TAG, "distance: $distance")
        Log.d(TAG, "cuisines: $categories")
        Log.d(TAG, "lat: $latitude")
        Log.d(TAG, "long: $longitude")

        val response: RestaurantSearchResponse = YelpApi.retrofitService.search(
            mapOf(
                "price" to priceRange,
                "location" to location,
                "radius" to distance,
                "categories" to categories
            )
        )

        Log.d(TAG, "response total: ${response.total}")
        Log.d(TAG, "num businesses: ${response.businesses.size}")

        return response
    }


    companion object {
        // TODO: fix memory leak o.o

        private var INSTANCE: Repository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("Repository must be initialized")
        }
    }
}