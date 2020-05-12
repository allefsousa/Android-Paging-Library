package com.developer.allef.boilerplateapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developer.allef.boilerplateapp.R
import com.developer.allef.boilerplateapp.data.model.SearchItem

/**
 * @author allef.santos on 12/05/20
 */
class SearchListAdapter internal constructor() :
    PagedListAdapter<SearchItem, SearchListAdapter.SearchListViewHolder>(SearchItem.DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        return SearchListViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(@NonNull holder: SearchListViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    class SearchListViewHolder private constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_title)
        private val description: TextView = itemView.findViewById(R.id.tv_description)
        @SuppressLint("SetTextI18n")
        fun bindTo(searchItem: SearchItem?) { // If placeholders are enabled, Paging will pass null first and then pass the actual data when it's available.
            if (searchItem != null) {
                title.text = searchItem.fullName
                description.text = searchItem.description
            } else {
                title.text = "Loading..."
                description.text = "Loading..."
            }
        }

        companion object {
            fun create(parent: ViewGroup?): SearchListViewHolder {
                val view = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.search_item, parent, false)
                return SearchListViewHolder(
                    view
                )
            }
        }

    }


}