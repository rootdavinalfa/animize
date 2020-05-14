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
import ml.dvnlabs.animize.database.model.StarLand
import ml.dvnlabs.animize.model.StarredModel
import ml.dvnlabs.animize.ui.recyclerview.interfaces.AddingQueue
import java.util.*

class StarListAdapter(private var packageLists: ArrayList<StarLand>?, private val mContext: Context, private val itemResor: Int) : RecyclerView.Adapter<StarListHolder>(), AddingQueue {
    override val queue: ArrayList<requestQueue?>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarListHolder {
        val view = LayoutInflater.from(mContext).inflate(itemResor, parent, false)
        return StarListHolder(mContext, view, this, packageLists!!.size)
    }

    override fun onBindViewHolder(holder: StarListHolder, position: Int) {
        holder.setIsRecyclable(false)
        //System.out.println("CDATA:POS:"+position+":PKG:"+this.packagelists.get(position).getPackageid());
        val slm = packageLists!![holder.absoluteAdapterPosition]
        holder.bindPlaylist(slm)
    }

    override fun getItemCount(): Int {
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        return if (packageLists == null) {
            0
        } else {
            packageLists!!.size
        }
    }

    //Callback for adding / remove queue
    override fun addQueue(pkganim: String, position: Int) {
        if (queue!!.size == 0) {
            queue.add(requestQueue(pkganim, position))
        } else {
            val single = queue.singleOrNull {
                it!!.pos == position
            }
            if (single == null) {
                queue.add(requestQueue(pkganim, position))
            }
        }
    }

    override fun removeQueue(position: Int) {
        val single = queue!!.singleOrNull {
            it!!.pos == position
        }
        if (single != null) {
            queue.remove(single)
        }

    }

    //Model for request queue
    inner class requestQueue internal constructor(var pkg: String, var pos: Int)

    class readyStar internal constructor(var model: StarredModel, var pos: Int)

    companion object {
        var readyStars = ArrayList<readyStar>()
    }

    init {
        readyStars.clear()
        queue = ArrayList()
    }
}