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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.StreamActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import kotlin.math.floor

class PackageListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, KoinComponent {
    private val animizeDB: AnimizeDatabase by inject { parametersOf(context) }
    private val episode: TextView = view.findViewById(R.id.rvText)
    private val progress: ProgressBar = view.findViewById(R.id.rvProgress)
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

            val percent: Int = when (max) {
                0L -> {
                    0
                }

                else -> {
                    floor((current.toDouble() / max.toDouble()) * 100).toInt()
                }
            }
            progress.progress = percent
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