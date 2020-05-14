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
import ml.dvnlabs.animize.model.PackageList
import java.util.*

class LastPackageAdapter(private val data: ArrayList<PackageList>?, private val mcontext: Context, private val itemRes: Int) : RecyclerView.Adapter<LastPackageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastPackageHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemRes, parent, false)
        return LastPackageHolder(mcontext, view)
    }

    override fun onBindViewHolder(holder: LastPackageHolder, position: Int) {
        val slm = data!![position]
        holder.binding(slm)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

}