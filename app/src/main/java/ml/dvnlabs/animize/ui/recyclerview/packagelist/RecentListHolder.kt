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
import ml.dvnlabs.animize.database.legacy.model.RecentLand
import ml.dvnlabs.animize.ui.activity.StreamActivity
import ml.dvnlabs.animize.util.FriendlyTime
import java.text.SimpleDateFormat
import java.util.*

class RecentListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val friendlytime: TextView
    private val name: TextView
    private val episode: TextView
    private val lasttime: TextView
    private val playedon: TextView
    private val cover: ImageView
    private var recs: RecentLand? = null
    fun bind_recent(rec: RecentLand) {
        recs = rec
        val date_modified = Date(rec.modified)
        val time_player = Date(rec.timestamp)
        val cal_date = Calendar.getInstance()
        //Calendar cal_player = Calendar.getInstance();
        val tz = cal_date.timeZone
        val format_date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        format_date.timeZone = tz
        val format_date_friendly = SimpleDateFormat("dd", Locale.getDefault())
        format_date_friendly.timeZone = tz
        val format_timeplay = SimpleDateFormat("mm:ss", Locale.getDefault())
        val format_month = SimpleDateFormat("M", Locale.getDefault())
        format_month.timeZone = tz
        val played = "Played On: " + format_date.format(date_modified)

        //Day for today,2 day ago
        val friendly = FriendlyTime().getFriendlyTime(date_modified,rec.modified)
        val format_playtime = "Last Time: " + format_timeplay.format(time_player)
        friendlytime.text = friendly
        playedon.text = played
        name.text = rec.packageName
        lasttime.text = format_playtime
        val episodee = "Episode: " + rec.episode
        episode.text = episodee
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture_light)
                        .error(R.drawable.ic_picture_light))
                .load(recs!!.urlCover)
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(cover)
    }

    override fun onClick(v: View) {
        if (recs != null) {
            val intent = Intent(context.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim", recs!!.anmid)
            context.startActivity(intent)
        }
    }

    init {
        cover = view.findViewById(R.id.recentlist_cover)
        friendlytime = view.findViewById(R.id.recentlist_friendlyday)
        episode = view.findViewById(R.id.recentlist_episode)
        name = view.findViewById(R.id.recentlist_name)
        lasttime = view.findViewById(R.id.recentlist_lasttime)
        playedon = view.findViewById(R.id.recentlist_playedon)
        itemView.setOnClickListener(this)
    }
}