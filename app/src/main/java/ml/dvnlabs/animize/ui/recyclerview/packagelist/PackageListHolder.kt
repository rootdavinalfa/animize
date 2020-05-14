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
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.StreamActivity

class PackageListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val episode: TextView
    private val id_anim: TextView
    private val title: TextView
    private val thumbnail: ImageView
    private var playlist_model: PlaylistModel? = null
    fun bind_playlist(plm: PlaylistModel?) {
        playlist_model = plm
        title.text = playlist_model!!.title
        id_anim.text = playlist_model!!.id_anim
        val ep = context.getString(R.string.episode_text) + ": " + playlist_model!!.episode
        episode.text = ep
        Glide.with(itemView).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.ic_picture_light).error(R.drawable.ic_picture_light)).load(playlist_model!!.url_image).transition(DrawableTransitionOptions().crossFade()).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 200).fitCenter()).into(thumbnail)
    }

    override fun onClick(v: View) {
        if (playlist_model != null) {
            val intent = Intent(context.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim", playlist_model!!.id_anim)
            context.startActivity(intent)
        }
    }

    init {
        episode = view.findViewById<View>(R.id.playlist_episode) as TextView
        id_anim = view.findViewById<View>(R.id.playlist_id) as TextView
        title = view.findViewById<View>(R.id.playlist_title) as TextView
        thumbnail = view.findViewById<View>(R.id.playlist_imgthumb) as ImageView
        itemView.setOnClickListener(this)
    }
}