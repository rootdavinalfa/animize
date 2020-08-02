/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import ml.dvnlabs.animize.player.PlayerService.PlayerBinder
import org.greenrobot.eventbus.EventBus

class PlayerManager
(private val context: Context) {
    var isServiceBound = false
        private set

    fun bind() {
        Log.e("BINDING:", "OK")
        //context.getApplicationContext().startService(new Intent(context,PlayerService.class));
        context.bindService(Intent(context, PlayerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
        isServiceBound = true
        if (service != null) EventBus.getDefault().post(service!!.getStatus())
    }

    fun unbind() {
        Log.e("UNBINDING:", "OK")
        //if (serviceBound){
        //context.getApplicationContext().stopService(new Intent(context,PlayerService.class));
        context.unbindService(serviceConnection)
        isServiceBound = false
        service = null

        //}
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            val playerBinder = binder as PlayerBinder
            service = playerBinder.service
            isServiceBound = true
            Log.i("BINDER STATUS:", "OK")
            //service = ((PlayerService.PlayerBinder) binder).getService();
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i(context.packageName, "ServiceConnection::onServiceDisconnected() called")
            service = null
            isServiceBound = false
            Log.i("BINDER STATUS:", "DC")
        }
    }

    companion object {
        @JvmStatic
        var service: PlayerService? = null
            private set

    }

}