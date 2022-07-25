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

class Repository private constructor(private val context: Context){

    fun getCuisineList(): CuisineList {
        val jsonString = context.assets.open("cuisines.json").bufferedReader().use {
            it.readText()
        }

        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<CuisineList> = moshi.adapter(CuisineList::class.java)

        val cuisineList = jsonAdapter.fromJson(jsonString)
        return cuisineList!!
    }

    fun getRestaurants(
        distance: String,
        priceRange: String,
        categories: String
    ) {
        Log.d(TAG, "distance: $distance")
        Log.d(TAG, "price range: $priceRange")
        Log.d(TAG, "cuisines: $categories")

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
                    "radius" to distance,
                    "price" to priceRange,
                    "categories" to categories
                )
            )
            Log.d(TAG, "response total: ${response.total}")
            Log.d(TAG, "num businesses: ${response.businesses.size}")
        }

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