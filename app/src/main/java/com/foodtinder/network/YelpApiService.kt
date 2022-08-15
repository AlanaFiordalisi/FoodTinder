package com.foodtinder.network

import com.foodtinder.network.model.RestaurantSearchResponse
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

const val BASE_URL = "https://api.yelp.com/v3/"

interface YelpApiService {

    @GET("businesses/search?term=restaurants&limit=32&sortBy=rating")
    suspend fun search(
        @QueryMap paramsMap: Map<String, String>
    ): RestaurantSearchResponse

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
}

val interceptor = Interceptor { chain ->
    val newRequest = chain.request()
        .newBuilder()
        .addHeader("Authorization", com.foodtinder.BuildConfig.yelpapikey)
        .build()

    chain.proceed(newRequest)
}

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .client(client)
    .build()

object YelpApi {
    val retrofitService : YelpApiService by lazy {
        retrofit.create(YelpApiService::class.java)
    }
}