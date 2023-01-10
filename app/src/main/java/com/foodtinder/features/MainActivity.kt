package com.foodtinder.features

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foodtinder.R
import com.foodtinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create instance of binding class for use in this activity
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Make the root view the active view on the screen
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // set up NavController to conditionally show/hide bottom nav bar
        setUpBottomBarVisibility()
    }

    private fun setUpBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.filterFragment, R.id.addressSearch -> {
                    binding.bottomNavLayout.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavLayout.visibility = View.VISIBLE
                }
            }
        }
    }
}