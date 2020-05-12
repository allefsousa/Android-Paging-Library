package com.developer.allef.boilerplateapp.data.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.developer.allef.boilerplateapp.api.GitHubService
import com.developer.allef.boilerplateapp.data.failure.RequestFailure
import com.developer.allef.boilerplateapp.data.failure.Retryable
import com.developer.allef.boilerplateapp.data.model.SearchItem
import com.developer.allef.boilerplateapp.data.model.SearchModel
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

/**
 * @author allef.santos on 12/05/20
 */
class SearchDataSource(
    val service: GitHubService, val query: String,
    val requestFailureLiveData: MutableLiveData<RequestFailure>
) : PageKeyedDataSource<Int, SearchItem>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, SearchItem>
    ) {
        // Initial page
        val page = 1
        val call = service.getSearchResults(query,page,params.requestedLoadSize)

        call.enqueue(object :retrofit2.Callback<SearchModel>{


            override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                val searchModel = response.body()
                if (searchModel == null) {
                    onFailure(call, HttpException(response))
                    return
                }
               callback.onResult(searchModel.searchItems,0,searchModel.totalCount,null,page+1)


            }

            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                // Allow user to retry the failed request
                val retryable: Retryable = object : Retryable {
                    override fun retry() {
                        loadInitial(params, callback)
                    }
                }

                handleError(retryable, t)
            }

        })

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SearchItem>) {
        // Next page.
        val page = params.key
        val call = service.getSearchResults(query,page,params.requestedLoadSize)
        call.enqueue(object :retrofit2.Callback<SearchModel>{


            override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                val searchModel = response.body()
                if (searchModel == null) {
                    onFailure(call, HttpException(response))
                    return
                }
                callback.onResult(searchModel.searchItems,page+1)


            }

            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                // Allow user to retry the failed request
                val retryable: Retryable = object : Retryable {
                    override fun retry() {
                        loadAfter(params, callback)
                    }
                }

                handleError(retryable, t)
            }

        })

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchItem>) {
    }
    private fun handleError(retryable: Retryable, t: Throwable) {
        requestFailureLiveData.postValue(RequestFailure(retryable, t.message!!))
    }
}