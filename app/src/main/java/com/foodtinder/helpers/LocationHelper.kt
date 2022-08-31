package com.foodtinder.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import java.lang.IllegalStateException

class LocationHelper(context: Context) {

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Task<Location> = fusedLocationProviderClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    )

    companion object {
        // TODO: fix memory leak o.o

        private var INSTANCE: LocationHelper? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = LocationHelper(context)
            }
        }

        fun get() = INSTANCE ?: throw IllegalStateException("LocationHelper must be initialized")
    }
}