package com.developer.allef.boilerplateapp.api

import com.developer.allef.boilerplateapp.data.model.SearchModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * @author allef.santos on 12/05/20
 */
interface GitHubService {
     val DEFAULT_PER_PAGE: Int
         get() = 10

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("/search/repositories")
    fun getSearchResults(
        @Query("q") searchQuery: String?,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Call<SearchModel>
}