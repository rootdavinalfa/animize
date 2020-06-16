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
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
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
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class StarredNotificationWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams), KoinComponent {
    private val starredSQLite: PackageStarDBHelper by inject { parametersOf(applicationContext) }
    private val starredRoom: StarredNotificationDatabase by inject { parametersOf(applicationContext) }

    private val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var maxProgress = 0
    private var currentProgress = 0

    override suspend fun doWork(): Result {
        Log.i(TAG, "Doing EpisodeUpdater work! ><")
        getLocalInformation()
        notificationPush()
        return Result.success()
    }

    private suspend fun getLocalInformation() {
        withContext(Dispatchers.IO) {
            val sqLiteStarredPKG = starredSQLite.starredList
            val observerHelper = sqLiteStarredPKG?.toFlowable()
            val roomPKG = starredRoom.starredNotificationDAO().getStarredNotificationList().toFlowable()
            observerHelper?.subscribeOn(Schedulers.computation())?.doOnComplete {
                //currentProgress = 0
                cancelNotificationProgress()
            }?.doOnNext {
                currentProgress += 1
                maxProgress = sqLiteStarredPKG.size
                createNotificationProgress(title = "Updating Data...",
                        description = "Synchronizing ${it.packageid}"
                )
            }?.subscribe {
                runBlocking {
                    delay(1500)
                    println("PKG SYNC: ${it.packageid}")
                    APINetworkRequest(applicationContext, listener, "${Api.url_playlist_play}${it.packageid}", APINetworkRequest.CODE_GET_REQUEST, null)
                }
            }

            roomPKG.subscribeOn(Schedulers.computation()).subscribe {
                GlobalScope.launch {
                    if (starredRoom.starredNotificationDAO().checkPKGID(it.packageID) > 0
                            && !starredSQLite.isStarred(it.packageID)) {
                        println("DELETED ${it.packageID}")
                        starredRoom.starredNotificationDAO().deletePKGID(it.packageID)
                    }
                }
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
                        syncTime = System.currentTimeMillis()
                )
                GlobalScope.launch {
                    starredRoom.starredNotificationDAO().newNotification(notification)
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
            val flows = Flowable.fromArray(starredRoom.starredNotificationDAO().getStarredNotificationListNotOpenedAndNotPosted())
            flows.subscribeOn(Schedulers.single()).subscribe {
                for (i in it) {
                    GlobalScope.launch {
                        if (starredRoom.starredNotificationDAO().isPosted(i.animeID) == 0) {
                            starredRoom.starredNotificationDAO().notificationPosted(i.animeID)
                            withContext(Dispatchers.Main) {
                                createNotification(
                                        title = i.nameCatalogue.take(65),
                                        description = "Updated to episode: ${i.episode}",
                                        imgURL = i.thumbnailURL
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationProgress(title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(Notification.NOTIFICATION_CHANNEL_ID_LOADING, "Sync", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        println("CURRENT: $currentProgress MAX: $maxProgress")

        val notificationBuilder = NotificationCompat.Builder(applicationContext, Notification.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_refresh_light)
                .setProgress(maxProgress, currentProgress, false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
        notificationManager.notify(Notification.NOTIFICATION_ID_SYNCHRONIZE, notificationBuilder.build())
    }

    private fun createNotification(title: String, description: String, imgURL: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(Notification.NOTIFICATION_CHANNEL_ID, Notification.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
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

    private fun cancelNotificationProgress() {
        notificationManager.cancel(Notification.NOTIFICATION_ID_SYNCHRONIZE)
    }

    companion object {
        private const val TAG = "EpisodeUpdater"
        private const val DEFAULT_INTERVAL = 60

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