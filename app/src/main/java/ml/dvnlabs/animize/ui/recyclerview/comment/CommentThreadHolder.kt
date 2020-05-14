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
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_comment_thread.view.*
import ml.dvnlabs.animize.model.CommentMainModel

class CommentThreadHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var model: CommentMainModel? = null
    fun bindComment(commentMainModel: CommentMainModel?) {
        model = commentMainModel
        val usermodels = model!!.userModels
        itemView.comments_thread_users.text = usermodels[0].name_user
        itemView.comments_thread_content.text = model!!.contents
    }

    override fun onClick(v: View) {
    }

    init {
        itemView.setOnClickListener(this)
    }
}