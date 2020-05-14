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
import ml.dvnlabs.animize.model.MetaGenreModel
import ml.dvnlabs.animize.ui.recyclerview.staggered.MetaGenreHolder.GotoPageGenre
import java.util.*

class MetaGenreAdapter(private val data: ArrayList<MetaGenreModel>?, private val mcontext: Context, private val itemResor: Int, var calls: GotoPageGenre) : RecyclerView.Adapter<MetaGenreHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaGenreHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return MetaGenreHolder(mcontext, view, calls)
    }

    override fun onBindViewHolder(holder: MetaGenreHolder, position: Int) {
        val slm = data!![position]
        holder.bind_data(slm, position)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return data?.size ?: 0
    }

}