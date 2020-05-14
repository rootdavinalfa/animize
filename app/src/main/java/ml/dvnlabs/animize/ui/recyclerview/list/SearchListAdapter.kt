/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.model.SearchListModel
import java.util.*

class SearchListAdapter //super(context, R.layout.video_list_view,data);
(private val video_data: ArrayList<SearchListModel>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<SearchListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return SearchListHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: SearchListHolder, position: Int) {
        val slm = video_data!![position]
        holder.bindsearch_list(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return video_data?.size ?: 0
    }

}