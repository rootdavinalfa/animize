package ml.dvnlabs.animize.player

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.activity.animplay_activity

class PlayerNotificationManager(private val service: PlayerService) {

    private val mAppname: String
    private var notificationManager: NotificationManager? = null
    private val resources: Resources = service.resources

    init {
        mAppname = resources.getString(R.string.app_name)
    }

    fun createAction(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(service, PlayerService::class.java)
        intent.action = action
        return PendingIntent.getService(service, requestCode, intent, 0)
    }

    fun startNotify(playbackStatus: String) {

        var icon = R.drawable.ic_pause_noti

        var playPauseAction = createAction(PlayerService.ACTION_PAUSE, REQUEST_CODE_PAUSE)

        if (playbackStatus == PlaybackStatus.PAUSED) {
            icon = R.drawable.ic_play_noti

            playPauseAction = createAction(PlayerService.ACTION_PLAY, REQUEST_CODE_PLAY)
        }

        val stopAction = createAction(PlayerService.ACTION_STOP, REQUEST_CODE_STOP)

        val intent = Intent(service, animplay_activity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(service, 0, intent, 0)

        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID)

        val channelid = "ml.dvnlabs.animplay_activity"
        val builder = NotificationCompat.Builder(service, channelid)
        val notif = builder.setOngoing(true)
                .setSmallIcon(R.drawable.exo_edit_mode_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(mAppname)
                .setContentText("Hello World! Testing video service")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .addAction(icon, "pause", playPauseAction)
                .addAction(R.drawable.ic_stop_noti, "stop", stopAction)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.mediaSession.sessionToken)
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction))
                .build()
        service.startForeground(NOTIFICATION_ID, notif)
    }

    fun cancelNotify() {
        service.stopForeground(true)
    }



    companion object {

        private val NOTIFICATION_ID = 555

        private val REQUEST_CODE_PAUSE = 1
        private val REQUEST_CODE_PLAY = 2
        private val REQUEST_CODE_STOP = 3
    }

}