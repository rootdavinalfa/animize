package ml.dvnlabs.animize.player;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class PlayerManager {
    private static PlayerManager instance = null;
    private static PlayerService service;

    private Context context;

    private boolean serviceBound;

    private PlayerManager(Context context) {
        this.context = context;
        //serviceBound = false;
    }

    public static PlayerManager with(Context context) {
        if (instance == null)
            instance = new PlayerManager(context);

        return instance;
    }

    public static PlayerService getService(){
        return service;
    }


    public void playOrPause(String streamUrl) {
        Log.e("URL STREAM:", streamUrl);
        service.playOrPause(streamUrl);
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
        //context.startService(new Intent(context,PlayerService.class));
        context.getApplicationContext().bindService(new Intent(context, PlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
        if (service != null)
            EventBus.getDefault().post(service.getStatus());
    }
 public void unbind() {

        if (service!=null) {
            //context.stopService(new Intent(context,PlayerService.class));
        context.getApplicationContext().unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = ((PlayerService.PlayerBinder) binder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(context.getPackageName(), "ServiceConnection::onServiceDisconnected() called");
            service = null;
            serviceBound = false;
        }
    };
}
