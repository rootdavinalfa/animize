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
import ml.dvnlabs.animize.model.PlaylistModel
import java.util.*

class PlayListAdapter(data: ArrayList<PlaylistModel>, private val mcontext: Context, private val itemResor: Int, idanim: String) : RecyclerView.Adapter<PlayListHolder>() {
    private val playlistdata: ArrayList<PlaylistModel>?
    private var now = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return PlayListHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: PlayListHolder, position: Int) {
        val slm = playlistdata!![position]
        holder.bindPlaylist(slm, now, holder.absoluteAdapterPosition)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return playlistdata?.size ?: 0
    }

    init {
        playlistdata = data
        for (i in playlistdata.indices) {
            if (data[i].id_anim == idanim) {
                now = i
            }
        }
    }
}