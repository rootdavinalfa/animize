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
import ml.dvnlabs.animize.model.PlaylistModel
import java.util.*

class PackageListAdapter(private val playlistdata: ArrayList<PlaylistModel>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<PackageListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return PackageListHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: PackageListHolder, position: Int) {
        val slm = playlistdata!![position]
        holder.bind_playlist(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return playlistdata?.size ?: 0
    }

}