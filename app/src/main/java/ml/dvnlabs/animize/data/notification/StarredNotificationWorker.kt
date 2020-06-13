/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.*
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.constant.Notification
import ml.dvnlabs.animize.database.legacy.PackageStarDBHelper
import ml.dvnlabs.animize.database.notification.StarredNotification
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StarredNotificationWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private var starredSQLite: PackageStarDBHelper? = null
    private var starredRoom: StarredNotificationDatabase? = null
    private val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var maxProgress = 0
    private var currentProgress = 0

    override suspend fun doWork(): Result {
        Log.i(TAG, "Doing EpisodeUpdater work! ><")
        starredSQLite = PackageStarDBHelper(context)
        starredRoom = StarredNotificationDatabase.getDatabase(applicationContext)
        getLocalInformation()
        notificationPush()
        return Result.success()

    }

    private suspend fun getLocalInformation() {
        withContext(Dispatchers.IO) {
            val sqLiteStarredPKG = starredSQLite!!.starredList
            val roomPKG = starredRoom!!.starredNotificationDAO().getStarredNotificationList()
            if (starredSQLite!!.isAvail && sqLiteStarredPKG != null) {
                maxProgress = sqLiteStarredPKG.size
                // Check packageID on sqLite
                for (pkg in sqLiteStarredPKG) {
                    val checkRoom = roomPKG.singleOrNull {
                        it.packageID == pkg.packageid
                    }
                    if (checkRoom == null){
                        APINetworkRequest(applicationContext, listener, "${Api.url_playlist_play}${pkg.packageid}", APINetworkRequest.CODE_GET_REQUEST, null)
                    }
                }
                //Check if starred on sqlite removed, then remove on room
                for (pkg in roomPKG){
                    val checkSqLite = sqLiteStarredPKG.singleOrNull {
                        it.packageid == pkg.packageID
                    }
                    if (checkSqLite == null){
                        starredRoom!!.starredNotificationDAO().deletePKGID(pkg.packageID)
                    }
                }
            } else {
                println("DELETING ALL NOTIFICATION")
                starredRoom!!.starredNotificationDAO().deleteALL()
            }
        }
    }

    private val listener: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    parsePlaylist(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            Log.e("NotificationUpdater", msg!!)
        }

        override fun onFetchStart() {
        }
    }

    private fun parsePlaylist(playlist: JSONArray) {
        try {
            currentProgress += 1
            val temp = ArrayList<TemporaryDataAnime>()
            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val url_img = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val id_an = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                temp.add(TemporaryDataAnime(
                        title, pkg, episode.toInt(), id_an, url_img
                ))
            }

            for (i in temp.reversed()) {
                val notification = StarredNotification(
                        nameCatalogue = i.nameCatalogue,
                        packageID = i.packageID,
                        episode = i.episode,
                        animeID = i.animeID,
                        thumbnailURL = i.thumbnailURL,
                        synchronized = System.currentTimeMillis()
                )

                createNotificationProgress(title = "Updating Data...",
                        description = "Please wait, your library being updated"
                )

                GlobalScope.launch {
                    starredRoom!!.starredNotificationDAO().newNotification(notification)
                    notificationPush()
                }
            }
            //Don't forget to clear temp, because we don't need on next data sequence
            temp.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun notificationPush() {
        withContext(Dispatchers.IO) {
            val notification = starredRoom!!.starredNotificationDAO().getStarredNotificationListNotOpenedAndNotPosted()
            for (i in notification) {
                starredRoom!!.starredNotificationDAO().notificationPosted(i.animeID)
                delay(1500)
                createNotification(
                        title = i.nameCatalogue.take(65),
                        description = "Updated to episode: ${i.episode}",
                        imgURL = i.thumbnailURL
                )
            }
        }
    }

    private fun createNotificationProgress(title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(Notification.NOTIFICATION_CHANNEL_ID_LOADING, Notification.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, Notification.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_refresh_light).setProgress(maxProgress, currentProgress, false)
                .setOnlyAlertOnce(true)
        if (maxProgress == currentProgress) {
            notificationManager.cancel(Notification.NOTIFICATION_ID_NEW_EPISODE)
        } else {
            notificationManager.notify(Notification.NOTIFICATION_ID_NEW_EPISODE, notificationBuilder.build())
        }

    }

    private fun createNotification(title: String, description: String, imgURL: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(Notification.NOTIFICATION_CHANNEL_ID, Notification.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        Glide.with(context)
                .asBitmap()
                .placeholder(R.drawable.ic_picture_light)
                .load(imgURL)
                .transition(BitmapTransitionOptions.withCrossFade())
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 200)
                        .fitCenter()).into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val notificationBuilder = NotificationCompat.Builder(applicationContext, Notification.NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setSmallIcon(R.drawable.ic_notification_update)
                                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                        notificationManager.notify((0..1000).random(), notificationBuilder.build())
                    }
                })
    }

    companion object {
        private const val TAG = "EpisodeUpdater"
        private const val DEFAULT_INTERVAL = 30

        fun setupTaskImmediately(context: Context) {
            val constraints: Constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val request = OneTimeWorkRequestBuilder<StarredNotificationWorker>()
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueue(request)

        }

        fun setupTaskPeriodic(context: Context, interval: Int = DEFAULT_INTERVAL) {
            if (interval > 0) {
                val request = PeriodicWorkRequestBuilder<StarredNotificationWorker>(
                        interval.toLong(), TimeUnit.MINUTES,
                        10, TimeUnit.MINUTES
                )
                        .addTag(TAG)
                        .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, request)
            } else {
                WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
            }
        }
    }
}

data class TemporaryDataAnime(
        val nameCatalogue: String,
        val packageID: String,
        val episode: Int,
        val animeID: String,
        val thumbnailURL: String
)

data class TemporarySource(
        val pkgIDSqlite : String,
        val pkgIDRoom : String
)