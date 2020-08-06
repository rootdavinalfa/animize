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
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.StreamActivity

class PlayListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val episode: TextView
    private val id_anim: TextView
    private val title: TextView
    private val thumbnail: ImageView
    private val now_sign: View
    private var playlist_model: PlaylistModel? = null
    fun bindPlaylist(plm: PlaylistModel?, now: Int, pos: Int) {
        playlist_model = plm
        title.text = playlist_model!!.title
        id_anim.text = playlist_model!!.id_anim
        val ep = context.getString(R.string.episode_text) + ": " + playlist_model!!.episode
        episode.text = ep
        if (now == pos) {
            now_sign.visibility = View.VISIBLE
        }
        Glide.with(itemView)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture))
                .load(playlist_model!!.url_image).transition(DrawableTransitionOptions().crossFade()).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 200).fitCenter())
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(10, 0)))
                .into(thumbnail)
    }

    override fun onClick(v: View) {
        if (playlist_model != null) {
            (context as StreamActivity).setIdAnim(playlist_model!!.id_anim)
        }
    }

    init {
        episode = view.findViewById(R.id.playlist_episode)
        id_anim = view.findViewById(R.id.playlist_id)
        title = view.findViewById(R.id.playlist_title)
        thumbnail = view.findViewById(R.id.playlist_imgthumb)
        now_sign = view.findViewById(R.id.playlist_now_sign)
        itemView.setOnClickListener(this)
    }
}