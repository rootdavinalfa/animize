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
import ml.dvnlabs.animize.model.GenrePackageList
import java.util.*

class GenrePackageListAdapter(private val packagelists: ArrayList<GenrePackageList>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<GenrePackageListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenrePackageListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return GenrePackageListHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: GenrePackageListHolder, position: Int) {
        val slm = packagelists!![position]
        holder.bindPlaylist(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return packagelists?.size ?: 0
    }

}