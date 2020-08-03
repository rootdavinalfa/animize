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
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.custom.ColorHelper
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.StreamActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import kotlin.math.floor

class PackageListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, KoinComponent {
    private val animizeDB: AnimizeDatabase by inject { parametersOf(context) }
    private val episode: TextView = view.findViewById(R.id.rvTextEpisode)
    private val thumbnail: ImageView = view.findViewById(R.id.rvAnimeImage)
    private var playlist_model: PlaylistModel? = null
    fun bindPlaylist(plm: PlaylistModel?) {
        playlist_model = plm
        val ep = context.getString(R.string.episode_text) + ": " + playlist_model!!.episode
        episode.text = ep
        setupImage(playlist_model!!.id_anim)
    }

    private fun setupImage(anmID: String) = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            val recentPlay = animizeDB.recentPlayedDAO().getRecentByAnimeID(anmID)
            var max = 0L
            var current = 0L

            if (recentPlay != null) {
                max = recentPlay.duration
                current = recentPlay.timestamp
                //println("ANMID : ${recentPlay.anmid} TIME : ${recentPlay.timestamp} MAX: ${recentPlay.maxTime}")
            }

            val percent = when (max) {
                0L -> {
                    1F
                }

                else -> {
                    floor((current.toDouble() / max.toDouble()) * 100).toFloat()
                }
            }
            withContext(Dispatchers.Main) {
                Glide.with(itemView)
                        .asBitmap()
                        .load(playlist_model!!.url_image)
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 200))
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                thumbnail.background = null
                            }

                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                thumbnail.setImageBitmap(resource)
                                thumbnail.post {
                                    ColorHelper().filteringWithPercentage(ColorHelper.FILTER_VERTICAL, thumbnail, percent)
                                }
                            }
                        })
            }
        }
    }

    override fun onClick(v: View) {
        if (playlist_model != null) {
            val intent = Intent(context.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim", playlist_model!!.id_anim)
            context.startActivity(intent)
        }
    }

    init {
        itemView.setOnClickListener(this)
    }
}