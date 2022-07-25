package com.foodtinder.network

import android.content.Context
import android.util.Log

import com.foodtinder.model.CuisineList
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.File
import java.lang.IllegalStateException
import java.nio.charset.Charset

private const val TAG = "Repository"

class Repository private constructor(private val context: Context){

    fun getCuisineList(): CuisineList {
        // WORKS:
//        val inputStream = context.assets.open("cuisines.json")
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer)
//        val jsonString = String(buffer, Charset.defaultCharset())

        val jsonString = context.assets.open("cuisines.json").bufferedReader().use { it.readText() }

//        open(fileName).bufferedReader().use{it.readText()}
//        getActivity(context)?.assets?.open("yourfilename.json")
//        assets.open("./cuisines.json").bufferedReader().use { it.readText() }
//        val jsonString: String = File("./cuisines.json").readText(Charsets.UTF_8)
//        val cuisineList = Gson().fromJson(jsonString, CuisineList::class.java)
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<CuisineList> = moshi.adapter(CuisineList::class.java)
        val cuisineList = jsonAdapter.fromJson(jsonString)
        cuisineList?.categories?.forEach { Log.d(TAG, it.toString()) }
        return cuisineList!!
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