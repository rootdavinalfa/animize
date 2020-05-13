/*
 * Copyright (c) 2020. 
 * Animize Devs 
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *   
 */

package ml.dvnlabs.animize.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ml.dvnlabs.animize.app.AppController
import ml.dvnlabs.animize.event.PlayerBusError
import ml.dvnlabs.animize.event.PlayerBusStatus
import org.greenrobot.eventbus.EventBus

class PlayerService : Service() , AudioManager.OnAudioFocusChangeListener, Player.EventListener{
    companion object{
        const val ACTION_PLAY = "ml.dvnlabs.animize.player.PlayerService.ACTION_PLAY"
        const val ACTION_PAUSE = "ml.dvnlabs.animize.player.PlayerService.ACTION_PAUSE"
        const val ACTION_STOP = "ml.dvnlabs.animize.player.PlayerService.ACTION_STOP"
    }

    private val playerBind = PlayerBinder()

    var exoPlayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

    private val notificationManager: ml.dvnlabs.animize.player.PlayNotificationManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var audioManager: AudioManager? = null
    private var status: String? = null
    private var streamUrl: String? = null

    inner class PlayerBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

    private val mediasSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()
            pause()
        }

        override fun onStop() {
            super.onStop()
            stop()
            //notificationManager.cancelNotify();
        }

        override fun onPlay() {
            super.onPlay()
            resume()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //notificationManager = new PlayNotificationManager(this,getApplicationContext());
        mediaSession = MediaSessionCompat(this, javaClass.simpleName)
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive =true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setCallback(mediasSessionCallback)

        val renderersFactory = DefaultRenderersFactory(applicationContext)
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        //exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector,loadControl);
        exoPlayer!!.addListener(this)
        status = PlaybackStatus.IDLE
    }

    override fun onBind(intent: Intent): IBinder? {
        return playerBind
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action

        if (TextUtils.isEmpty(action))
            return Service.START_NOT_STICKY

        val result = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop()
            return Service.START_NOT_STICKY
        }
        if (action!!.equals(ACTION_PLAY, ignoreCase = true)) {
            transportControls!!.play()
        } else if (action.equals(ACTION_PAUSE, ignoreCase = true)) {

            if (PlaybackStatus.STOPPED === status) {
                transportControls!!.stop()
            } else {
                transportControls!!.pause()
            }
        } else if (action.equals(ACTION_STOP, ignoreCase = true)) {
            pause()
            //notificationManager.cancelNotify();
        }
        return Service.START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (status == PlaybackStatus.IDLE)
            stopSelf()

        // TODO: clear cache
//        MainApplication.getProxy(getApplicationContext()).unregisterCacheListener(this);
//        try {
//            Utils.cleanVideoCacheDir(getApplicationContext());
//            Log.d("__onDestroy", "clear cache");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("__IOException", e.toString());
//        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        pause()
        exoPlayer!!.release()
        exoPlayer!!.removeListener(this)
        //notificationManager.cancelNotify();
        mediaSession!!.release()
        super.onDestroy()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer!!.volume =0.8f
                resume()
            }

            AudioManager.AUDIOFOCUS_LOSS -> stop()

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (isPlaying()) pause()

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (isPlaying())
                exoPlayer!!.volume = 0.1f
        }
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> status = PlaybackStatus.LOADING
            Player.STATE_ENDED -> status = PlaybackStatus.STOPPED
            Player.STATE_IDLE -> status = PlaybackStatus.IDLE
            Player.STATE_READY -> status = if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            else -> status = PlaybackStatus.IDLE
        }

        if (status != PlaybackStatus.IDLE)
        //notificationManager.startNotify(status);

        //EventBus.getDefault().post(status);
            EventBus.getDefault().post(PlayerBusStatus(status!!))
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        val errorDetails: String?
        //EventBus.getDefault().post(PlaybackStatus.ERROR);
        when (error!!.type) {
            ExoPlaybackException.TYPE_OUT_OF_MEMORY->{}
            ExoPlaybackException.TYPE_REMOTE->{}
            ExoPlaybackException.TYPE_SOURCE -> {
                errorDetails = error.sourceException.message
                EventBus.getDefault().post(PlayerBusError(errorDetails!!))
            }
            ExoPlaybackException.TYPE_RENDERER -> {
                errorDetails = error.rendererException.message
                EventBus.getDefault().post(PlayerBusError(errorDetails!!))
            }

            ExoPlaybackException.TYPE_UNEXPECTED -> {
                errorDetails = error.unexpectedException.message
                EventBus.getDefault().post(PlayerBusError(errorDetails!!))
            }
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(applicationContext, "Animize")
        val dataSourceFactory = DefaultDataSourceFactory(this, DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true))
        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
        val cacheDataSourceFactory = CacheDataSourceFactory(AppController.setVideoCache(), dataSourceFactory)
        return ProgressiveMediaSource.Factory(cacheDataSourceFactory, extractorsFactory)
                .createMediaSource(uri)
    }

    fun getStatus(): String {
        return status!!
    }

    fun play() {
        exoPlayer!!.playWhenReady = true
    }

    fun pause() {
        exoPlayer!!.playWhenReady =false

        audioManager!!.abandonAudioFocus(this)
    }

    fun resume() {
        if (streamUrl != null)
            play()
    }

    fun stop() {
        exoPlayer!!.stop()
        exoPlayer!!.release()

        audioManager!!.abandonAudioFocus(this)
    }

    fun init(streamUrl: String) {
        this.streamUrl = streamUrl

        val mediaSource = buildMediaSource(Uri.parse(streamUrl))

        exoPlayer!!.prepare(mediaSource)
        exoPlayer!!.videoScalingMode=C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        exoPlayer!!.playWhenReady= true
    }

    fun playOrPause(urli: String?) {
        //Log.e("STREAM-OK:",urli);
        if (urli != null) {
            println("OK")
            if (streamUrl != null && streamUrl == urli) {
                play()
            } else {
                //Log.e("Service",urli);
                init(urli)

            }
        }

    }

    fun isPlaying(): Boolean {
        return this.status == PlaybackStatus.PLAYING
    }

    fun getMediaSession(): MediaSessionCompat {
        return mediaSession!!
    }

}