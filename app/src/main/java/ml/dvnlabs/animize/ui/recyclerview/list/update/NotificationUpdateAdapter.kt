/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.list.update

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.rv_update.view.*
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.notification.StarredNotification
import ml.dvnlabs.animize.ui.activity.StreamActivity
import ml.dvnlabs.animize.util.FriendlyTime
import java.util.*

class NotificationUpdateAdapter(
        private val itemRes: Int) : RecyclerView.Adapter<NotificationUpdateAdapter.NotificationUpdateViewHolder>() {
    private var context: Context? = null
    private var notificationList = emptyList<StarredNotification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationUpdateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val view = inflater.inflate(itemRes, parent, false)
        return NotificationUpdateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationUpdateViewHolder, position: Int) {
        val notify = notificationList[position]
        //holder.receivedON.visibility = View.GONE
        val synchronized = Date(notify.synchronized)
        holder.receivedON.text = "Received On: ${FriendlyTime().getFriendlyTime(synchronized,notify.synchronized)}"
        holder.title.text = notify.nameCatalogue
        holder.episode.text = "Episode: ${notify.episode}"
        Glide.with(context!!)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture_light)
                        .error(R.drawable.ic_picture_light))
                .load(notify.thumbnailURL)
                .transform(RoundedCorners(5))
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.image)
    }

    internal fun setNotification(notification : List<StarredNotification>){
        this.notificationList = notification
        notifyDataSetChanged()

    }


    inner class NotificationUpdateViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val receivedON = view.updateReceivedOn
        val image = view.updateThumbnail
        val title = view.updateAnimeTitle
        val episode = view.updateAnimeEpisode
        init {
           view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val intent = Intent(context!!.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim",notificationList[absoluteAdapterPosition].animeID)
            context!!.startActivity(intent)
        }
    }

}