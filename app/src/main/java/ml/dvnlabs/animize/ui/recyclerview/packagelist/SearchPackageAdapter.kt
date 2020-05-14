/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.packagelist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.model.SearchListPackageModel
import java.util.*

class SearchPackageAdapter(private val data: ArrayList<SearchListPackageModel>?, private val context: Context, private val itemRes: Int) : RecyclerView.Adapter<SearchPackageHolder>() {
    override fun onBindViewHolder(holder: SearchPackageHolder, position: Int) {
        val model = data!![holder.absoluteAdapterPosition]
        holder.bind_search_package(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPackageHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemRes, parent, false)
        return SearchPackageHolder(context, view)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

}