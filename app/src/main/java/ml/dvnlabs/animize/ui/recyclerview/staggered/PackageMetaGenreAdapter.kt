/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.staggered

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PackageMetaGenreAdapter(private val data: List<String>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<PackageMetaGenreHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageMetaGenreHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return PackageMetaGenreHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: PackageMetaGenreHolder, position: Int) {
        holder.bind_data(data!![position])
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return data?.size ?: 0
    }

}