package com.developer.allef.boilerplateapp.data.model


import com.google.gson.annotations.SerializedName

data class SearchModel(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val searchItems: List<SearchItem>,
    @SerializedName("total_count")
    val totalCount: Int
)