package com.foodtinder.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodtinder.databinding.ActivityMainBinding

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