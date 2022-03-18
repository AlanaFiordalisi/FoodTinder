package com.foodtinder.network

class SearchApiParameters {

    private var queryParams = mutableMapOf<String, String>()

    // Removing this bc can hardcode these three values in the API endpoint in ApiService
//    private var queryParams = mutableMapOf(
//        "term" to "restaurants",
//        "limit" to "32",
//        "sortBy" to "rating"
//    )

    fun getQueryParams() = queryParams

    fun setSortBy(sort: String) {
        queryParams["sort_by"] = sort
    }

    fun setRadius(radius: String) {
        // TODO: Guarantee radius is a valid value < 40000
        // ^^ Will be taken care of by the input fields on the filter page
        queryParams["radius"] = radius
    }

    fun setCategories(categories: String) {
        queryParams["categories"] = categories
    }

    fun setPrice(price: String) {
        queryParams["price"] = price
    }

    // TODO: How do I guarantee one or both of location / lat&long?

    // TODO: OpenNow?
}