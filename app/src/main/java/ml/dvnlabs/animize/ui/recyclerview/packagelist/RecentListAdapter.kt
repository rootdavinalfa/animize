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
import ml.dvnlabs.animize.database.legacy.model.RecentLand
import java.util.*

class RecentListAdapter(private val recentLands: ArrayList<RecentLand>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<RecentListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return RecentListHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: RecentListHolder, position: Int) {
        val slm = recentLands!![position]
        holder.bind_recent(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return recentLands?.size ?: 0
    }

}