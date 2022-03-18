package com.foodtinder.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("businesses/search?term=restaurants&limit=32&sortBy=rating")
    suspend fun search(
        @Header("Authorization") token: String,
        @QueryMap paramsMap: Map<String, String>,


//        @Query("term") term: String = "restaurants",
//        @Query("limit") limit: Int = 32,
//        @Query("sort_by") sortBy: String = "rating",
//        @Query("location") location: String,
//        @Query("radius") radius: Int,
//        @Query("categories") categories: String? = null,
//        @Query("price") price: String? = null
        // opennow?
        // lat
        // long
        // before calling search, ensure location != null || (lat != null && long != null)
        // structure ?!?
        )
}