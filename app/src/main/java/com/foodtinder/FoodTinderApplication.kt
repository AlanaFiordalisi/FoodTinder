package com.foodtinder

import android.app.Application
import com.foodtinder.helpers.LocationHelper
import com.foodtinder.network.Repository

class FoodTinderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(context = this)
        LocationHelper.initialize(context = this)
    }
}