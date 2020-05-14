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
import ml.dvnlabs.animize.model.HomeLastUploadModel
import java.util.*

class HomeLastUpAdapter(private val lastupdata: ArrayList<HomeLastUploadModel>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<HomeLastUpHolder>() {
    var pos = 0
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeLastUpHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return HomeLastUpHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: HomeLastUpHolder, position: Int) {
        pos = position
        val slm = lastupdata!![position]
        holder.bindLastUp(slm)
    }

    override fun getItemCount(): Int {
        return lastupdata?.size ?: 0
    }

}