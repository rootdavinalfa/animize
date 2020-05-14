/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.model.CommentMainModel
import java.util.*

class CommentMainAdapter(private val commentMainModels: ArrayList<CommentMainModel>?, private val mcontext: Context, private val itemResor: Int) : RecyclerView.Adapter<CommentMainHolder>() {
    override fun onBindViewHolder(holder: CommentMainHolder, position: Int) {
        val slm = commentMainModels!![position]
        holder.bindComment(slm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentMainHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResor, parent, false)
        return CommentMainHolder(mcontext, view)
    }

    override fun getItemCount(): Int {
        return commentMainModels?.size ?: 0
    }

}