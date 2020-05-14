/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.banner

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.model.BannerListMdl
import java.util.*

class BannerAdapter(private val bannermodel: ArrayList<BannerListMdl>?, private val mContext: Context, private val itemResor: Int) : RecyclerView.Adapter<BannerHolder>() {
    override fun onBindViewHolder(holder: BannerHolder, position: Int) {
        val slm = bannermodel!![position]
        holder.bindBanner(slm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return BannerHolder(mContext, view)
    }

    override fun getItemCount(): Int {
        return bannermodel?.size ?: 0
    }

}