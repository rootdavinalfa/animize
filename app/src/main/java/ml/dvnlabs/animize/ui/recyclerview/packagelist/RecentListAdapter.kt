/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.packagelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.database.RecentPlayed

class RecentListAdapter(private val itemResor: Int) : RecyclerView.Adapter<RecentListHolder>() {
    private var recentLands = emptyList<RecentPlayed>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentListHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return RecentListHolder(parent.context, view)
    }

    override fun onBindViewHolder(holder: RecentListHolder, position: Int) {
        val slm = recentLands[position]
        holder.bind_recent(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return recentLands.size
    }

    fun setRecentPlayed(recent: List<RecentPlayed>) {
        val diffCallback = RecentDiff(recent, this.recentLands)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.recentLands = recent
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecentDiff(private val newList: List<RecentPlayed>, private val oldList: List<RecentPlayed>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].animeID == newList[newItemPosition].animeID && oldList[oldItemPosition].modified == newList[newItemPosition].modified
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        @Nullable
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }

}