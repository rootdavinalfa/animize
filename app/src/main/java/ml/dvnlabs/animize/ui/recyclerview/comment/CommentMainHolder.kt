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
import kotlinx.android.synthetic.main.rv_comments.view.*
import ml.dvnlabs.animize.model.CommentMainModel
import ml.dvnlabs.animize.ui.activity.StreamActivity
import java.util.*

class CommentMainHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var model: CommentMainModel? = null
    fun bindComment(commentMainModel: CommentMainModel?) {
        model = commentMainModel
        val usermodels = model!!.userModels
        itemView.comments_users.text = usermodels[0].name_user
        itemView.comments_content.text = model!!.contents
    }

    override fun onClick(v: View) {
        if (model != null) {
            val data = ArrayList<CommentMainModel>()
            data.add(model!!)
            (context as StreamActivity).showReplyFragment(data)
        }
    }

    init {
        itemView.setOnClickListener(this)
    }
}