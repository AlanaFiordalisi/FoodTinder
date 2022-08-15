package com.foodtinder.network

import android.content.Context
import android.util.Log
import com.foodtinder.model.CuisineList
import com.foodtinder.network.model.RestaurantSearchResponse
import com.google.gson.GsonBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        distance: String,
        priceRange: String,
        categories: String
    ) : RestaurantSearchResponse {
        Log.d(TAG, "distance: $distance")
        Log.d(TAG, "price range: $priceRange")
        Log.d(TAG, "cuisines: $categories")

        val response: RestaurantSearchResponse = YelpApi.retrofitService.search(
            mapOf(
                "location" to "515 S Mangum St, Durham, NC 27701",
                "radius" to distance,
                "price" to priceRange,
                "categories" to categories
            )
        )

        Log.d(TAG, "response total: ${response.total}")
        Log.d(TAG, "num businesses: ${response.businesses.size}")

        return response
    }


    companion object {
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