package com.developer.allef.boilerplateapp.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developer.allef.boilerplateapp.R
import com.developer.allef.boilerplateapp.api.GitHubService
import com.developer.allef.boilerplateapp.data.failure.RequestFailure
import com.developer.allef.boilerplateapp.data.model.SearchItem
import com.developer.allef.boilerplateapp.data.paging.SearchDataSource
import com.developer.allef.boilerplateapp.util.MainThreadExecutor
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    val DEFAULT_PER_PAGE: Int = 10
    lateinit var parentLayout: View


    private lateinit var executor: MainThreadExecutor
    private lateinit var adapter: SearchListAdapter
    private lateinit var service: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parentLayout = findViewById<View>(android.R.id.content)

        executor = MainThreadExecutor()
        setupGitHubService()
        setupSearch()
        setupRecyclerView()

    }

    private fun setupGitHubService() {
        val client = OkHttpClient()
            .newBuilder()
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        service = retrofit.create(GitHubService::class.java)
    }

    private fun setupSearch() {
        val searchEditText: EditText = findViewById(R.id.et_search)
        searchEditText.setOnEditorActionListener(OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setupDataSource(textView.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun setupRecyclerView() {
        adapter = SearchListAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun setupDataSource(queryString: String) { // Initialize Data Source
        val dataSource = SearchDataSource(service, queryString)
        // Configure paging
        val config: PagedList.Config =
            PagedList.Config.Builder() // Number of items to fetch at once. [Required]
                .setPageSize(DEFAULT_PER_PAGE) // Number of items to fetch on initial load. Should be greater than Page size. [Optional]
                .setInitialLoadSizeHint(DEFAULT_PER_PAGE * 2)
                .setEnablePlaceholders(true) // Show empty views until data is available
                .build()
        // Build PagedList
        val list: PagedList<SearchItem> =
            PagedList.Builder(
                dataSource,
                config
            ) // Can pass `pageSize` directly instead of `config`
// Do fetch operations on the main thread. We'll instead be using Retrofit's
// built-in enqueue() method for background api calls.
                .setFetchExecutor(executor) // Send updates on the main thread
                .setNotifyExecutor(executor)
                .build()
        // Ideally, the above code should be placed in a ViewModel class so that the list can be
// retained across configuration changes.
// Required only once. Paging will handle fetching and updating the list.
        adapter.submitList(list)
        dataSource.getRequestFailureLiveData().observe(this, object : Observer<RequestFailure?> {
            override fun onChanged(@Nullable requestFailure: RequestFailure?) {
                if (requestFailure == null) return
                Snackbar.make(
                    parentLayout,
                    requestFailure.errorMessage,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("RETRY") {
                        // Retry the failed request
                        requestFailure.retryable.retry()
                    }.show()
            }
        })
    }
}
