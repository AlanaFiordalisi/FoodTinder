package com.foodtinder.network.model

import com.google.gson.annotations.SerializedName

// Do I need to include all the parts of the JSON response here?
// Is it best to include all of the values in the API response and delete, or only include what you need when you need it?
// Should the types of each value be nullable?
// Need to create other model classes for things like Coordinates{lat, long}?
class BusinessEntity(
    @SerializedName("rating") var rating: Float?,
    @SerializedName("price") var price: String?,
    @SerializedName("phone") var phone: String?,
    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("image_url") var image_url: String?,

) {

}