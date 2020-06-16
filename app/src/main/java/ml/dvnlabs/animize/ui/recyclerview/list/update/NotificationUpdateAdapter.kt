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
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.rv_update.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.notification.StarredNotification
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.ui.activity.StreamActivity
import ml.dvnlabs.animize.util.FriendlyTime
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
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
        holder.bindItem()
    }

    internal fun setNotification(notification : List<StarredNotification>){
        val diffCallback = NotificationDiff(notification,this.notificationList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.notificationList = notification
        diffResult.dispatchUpdatesTo(this)
    }


    inner class NotificationUpdateViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener,KoinComponent {
        private val starredRoom: StarredNotificationDatabase by inject { parametersOf(context) }
        val receivedON = view.updateReceivedOn
        val image = view.updateThumbnail
        val title = view.updateAnimeTitle
        val episode = view.updateAnimeEpisode
        init {
           view.setOnClickListener(this)
        }

        fun bindItem(){
            val notify = notificationList[absoluteAdapterPosition]
            //holder.receivedON.visibility = View.GONE
            val synchronized = Date(notify.syncTime)
            receivedON.text = "Received On: ${FriendlyTime().getFriendlyTime(synchronized, notify.syncTime)}"

            if (notify.opened){
                println("POSITION: $absoluteAdapterPosition")
                title.setTextColor(ContextCompat.getColor(context!!,R.color.disabled))
                episode.setTextColor(ContextCompat.getColor(context!!,R.color.disabled))
            }else{
                title.setTextColor(ContextCompat.getColor(context!!,R.color.white))
                episode.setTextColor(ContextCompat.getColor(context!!,R.color.white))
            }
            title.text = notify.nameCatalogue
            episode.text = "Episode: ${notify.episode}"
            Glide.with(context!!)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture_light)
                            .error(R.drawable.ic_picture_light))
                    .load(notify.thumbnailURL)
                    .transform(RoundedCorners(5))
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)).into(image)
        }

        override fun onClick(v: View?) {

            GlobalScope.launch {
                starredRoom.starredNotificationDAO().notificationOpened(notificationList[absoluteAdapterPosition].animeID)
            }
            val intent = Intent(context!!.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim",notificationList[absoluteAdapterPosition].animeID)
            context!!.startActivity(intent)
        }
    }

    inner class NotificationDiff(private val newList: List<StarredNotification>, private val oldList: List<StarredNotification>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].animeID == newList[newItemPosition].animeID
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        @Nullable
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }

}