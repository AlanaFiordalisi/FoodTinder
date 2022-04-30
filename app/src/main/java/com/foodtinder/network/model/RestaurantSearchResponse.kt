package com.foodtinder.network.model

import com.google.gson.annotations.SerializedName

// Do I need to include all the parts of the JSON response here?
class RestaurantSearchResponse(
    @SerializedName("total") var total: Int,
    @SerializedName("businesses") var businesses: List<BusinessEntity>
) {
}