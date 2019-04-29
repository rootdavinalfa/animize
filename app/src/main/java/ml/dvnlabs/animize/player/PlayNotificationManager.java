package ml.dvnlabs.animize.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;

public class PlayNotificationManager {
    private static NotificationManager notificationManager;
    private static final String CHANNEL_NAME = "Animize PlayerView";
    private static final String CHANNEL_DESC = "Animize player Notification";
    private static final String CHANNEL_ID = "PlayerFGBGNotification";
    private String mAppname = "Animize";
    private Resources resources;
    private PlayerService service;
    private int NOTIFICATION_ID = 555;
    private int REQUEST_CODE_PAUSE = 1;
    private int REQUEST_CODE_PLAY = 2;
    private int REQUEST_CODE_STOP = 3;
    PendingIntent pend;

    Context base;

    public PlayNotificationManager(PlayerService service,Context connt){

        this.service = service;
        this.base = connt;

    }


    public PendingIntent createAction(String action,int requestCode){
        Intent intent = new Intent(service,PlayerService.class);
        //action = intent.getAction();
        intent.setAction(action);
        return PendingIntent.getActivity(service,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }
    public void startNotify(String playbackStatus){
        int icon = R.drawable.ic_pause_noti;
        PendingIntent playPauseAction = createAction(PlayerService.ACTION_PAUSE,REQUEST_CODE_PAUSE);
        if(playbackStatus.equals(PlaybackStatus.PAUSED)){
            icon = R.drawable.ic_play_noti;
            playPauseAction = createAction(PlayerService.ACTION_PLAY, REQUEST_CODE_PLAY);

        }

        PendingIntent stopAction =createAction(PlayerService.ACTION_STOP, REQUEST_CODE_STOP);
        Intent intent = new Intent(service, animplay_activity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            channel.setSound(null,null);
            getNotificationManager().createNotificationChannel(channel);

        }
        else {
            NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);
        }

        //NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);
        //String CHANNEL_ID = "ml.dvnlabs.animplay_activity";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_animize)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_animize))
                .setContentTitle(mAppname)
                .setContentText("Playing...")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .addAction(icon, "pause", playPauseAction)
                .addAction(R.drawable.ic_stop_noti, "stop", stopAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction));
        service.startForeground(NOTIFICATION_ID,builder.build());




    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void notificationchannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DESC);
        notificationChannel.enableVibration(true);
        notificationChannel.enableLights(true);
        notificationChannel.canShowBadge();
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        //notificationChannel.setLightColor(getResources.getColor(R.color.colorAccent));
        getNotificationManager().createNotificationChannel(notificationChannel);
    }
    private NotificationManager getNotificationManager(){
        if(notificationManager == null) {
            notificationManager = (NotificationManager) base.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }




    public void cancelNotify(){
        service.stopForeground(true);
    }


}
