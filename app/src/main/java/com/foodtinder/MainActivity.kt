package com.foodtinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

//        binding.button.setOnClickListener {
//            buttonOnClick()
//        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.frag)
        if (currentFragment == null) {
            val fragment = FilterFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frag, fragment)
                .commit()
        }
    }

    private fun buttonOnClick() {
        val service = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        var response: RestaurantSearchResponse

        CoroutineScope(IO).launch {
            response = service.search(
                token = "Bearer FxhnaEnu31yrqfGl1XcyyH_mjgR7NuII6gJLvV2zj_fxVFlmsbY4od73X52AwXMBATyJtrHkp3C6cWuMMTLt6VEdbkc2E4ea9vS5AkcSFe5d7ELg7L8ipTunEDfXYXYx",
                mapOf(
                    "location" to "515 S Mangum St, Durham, NC 27701",
                    "radius" to "7000"
                )
            )
            Log.d("MainActivity", "${response.total}")
            Log.d("MainActivity", "${response.businesses.size}")
        }
    }
}