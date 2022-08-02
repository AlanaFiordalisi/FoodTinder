package com.foodtinder.features

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.foodtinder.databinding.ActivityMainBinding
import com.foodtinder.network.ApiService
import com.foodtinder.network.model.RestaurantSearchResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create instance of binding class for use in this activity
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Make the root view the active view on the screen
        val view = binding.root
        setContentView(view)
    }
}