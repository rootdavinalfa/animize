/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.player;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import ml.dvnlabs.animize.ui.activity.StreamActivity;

public class PlayerManager {
    private static PlayerManager instance = null;
    private static PlayerService service;

    private Context context;

    private boolean serviceBound;

    public PlayerManager(Context context) {
        this.context = context;
        //serviceBound = false;
    }

    /*
    public static PlayerManager with(Context context) {
        if (instance == null)
            instance = new PlayerManager(context);

        return instance;
    }*/

    public static PlayerService getService(){
        return service;
    }


    public void playOrPause(String streamUrl) {
        //Log.e("URL STREAM:", streamUrl);
        if(service !=null){
            service.playOrPause(streamUrl);
        }else {
            Log.e("ERROR","NOT BOUND");
            bind();
        }

    }

    public void pause_video(){
        service.pause();
    }

    public boolean isPlaying() {
        return service.isPlaying();
    }

    public boolean isServiceBound() {
        return serviceBound;
    }

    public void bind() {
        Log.e("BINDING:","OK");
        //context.getApplicationContext().startService(new Intent(context,PlayerService.class));
        context.bindService(new Intent(context, PlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
        if (service != null)
            EventBus.getDefault().post(service.getStatus());
    }

    public void unbind() {
        Log.e("UNBINDING:", "OK");
        //if (serviceBound){
        //context.getApplicationContext().stopService(new Intent(context,PlayerService.class));
        context.unbindService(serviceConnection);
        serviceBound = false;
        service = null;

        //}
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            PlayerService.PlayerBinder playerBinder = (PlayerService.PlayerBinder) binder;
            service = playerBinder.getService();
            serviceBound = true;
            //((animplay_activity)context).playerServiceInit();
            //String id=((animplay_activity) context).getIntent().getStringExtra("id_anim");
            ((StreamActivity)context).getVideo();
            Log.e("BINDER STATUS:","OK");
            //service = ((PlayerService.PlayerBinder) binder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e(context.getPackageName(), "ServiceConnection::onServiceDisconnected() called");
            service = null;
            serviceBound = false;
            Log.e("BINDER STATUS:","DC");
        }
    };
}
