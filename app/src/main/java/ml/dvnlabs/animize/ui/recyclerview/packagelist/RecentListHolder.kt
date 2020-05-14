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
import ml.dvnlabs.animize.database.model.RecentLand
import ml.dvnlabs.animize.ui.activity.StreamActivity
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
        val day = format_date_friendly.format(date_modified)
        val today = Integer.valueOf(format_date_friendly.format(System.currentTimeMillis()))
        val friendly: String
        val diff = System.currentTimeMillis() - rec.modified
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        //System.out.println("NAME: "+rec.getPackage_name()+" Episode:"+rec.getEpisode()+" Times:"+ diffDays+"/"+diffHours+"/"+diffMinutes);
        friendly = if (Integer.valueOf(day) == today && Integer.valueOf(format_month.format(date_modified)) == Integer.valueOf(format_month.format(System.currentTimeMillis()))) {
            if (diffMinutes <= 58 && diffHours == 0L) {
                "Today, $diffMinutes Minutes ago"
            } else {
                "Today, $diffHours Hours ago"
            }
        } else {
            if (diffDays < 1) {
                "Yesterday, $diffHours Hours ago"
            } else {
                if (today - 1 == Integer.valueOf(day)) {
                    //friendly = "Yesterday, "+ diffHours +" Hours ago";
                    "Yesterday" + if (diffHours == 0L) "" else ", $diffHours Hours ago"
                } else {
                    diffDays.toString() + " Days " + if (diffHours == 0L) " Ago" else ", $diffHours Hours ago"
                    //friendly = "Yesterday"+ (diffHours==0?"":", "+diffHours+" Hours ago");
                }
            }
        }
        val format_playtime = "Last Time: " + format_timeplay.format(time_player)
        friendlytime.text = friendly
        playedon.text = played
        name.text = rec.packageName
        lasttime.text = format_playtime
        val episodee = "Episode: " + rec.episode
        episode.text = episodee
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
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