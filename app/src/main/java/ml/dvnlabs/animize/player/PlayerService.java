package ml.dvnlabs.animize.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;




import java.io.File;

import androidx.annotation.Nullable;
import ml.dvnlabs.animize.Event.PlayerBusError;
import ml.dvnlabs.animize.Event.PlayerBusStatus;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.app.AppController;


public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener, Player.EventListener, CacheListener {
    public static final String ACTION_PLAY = "ml.dvnlabs.animize.player.PlayerService.ACTION_PLAY";
    public static final String ACTION_PAUSE = "ml.dvnlabs.animize.player.PlayerService.ACTION_PAUSE";
    public static final String ACTION_STOP = "ml.dvnlabs.animize.player.PlayerService.ACTION_STOP";

    private final IBinder playerBind = new PlayerBinder();

    public SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;

    private ml.dvnlabs.animize.player.PlayNotificationManager notificationManager;
    private MediaControllerCompat.TransportControls transportControls;

    private AudioManager audioManager;

    private String status;

    private String streamUrl;

    protected class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();

            pause();
        }

        @Override
        public void onStop() {
            super.onStop();

            stop();

           //notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();

            resume();
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = new PlayNotificationManager(this,getApplicationContext());
        mediaSession = new MediaSessionCompat(this,getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediasSessionCallback);

        RenderersFactory renderersFactory = new DefaultRenderersFactory(getApplicationContext());
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DefaultLoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector,loadControl);
        exoPlayer.addListener(this);
        status = PlaybackStatus.IDLE;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return playerBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if(TextUtils.isEmpty(action))
            return START_NOT_STICKY;

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            stop();

            return START_NOT_STICKY;
        }

        if(action.equalsIgnoreCase(ACTION_PLAY)){
            transportControls.play();

        } else if(action.equalsIgnoreCase(ACTION_PAUSE)) {

            if (PlaybackStatus.STOPPED == status) {
                transportControls.stop();
            } else {
                transportControls.pause();
            }

        } else if(action.equalsIgnoreCase(ACTION_STOP)){
            pause();

            //notificationManager.cancelNotify();
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();

        // TODO: clear cache
//        MainApplication.getProxy(getApplicationContext()).unregisterCacheListener(this);
//        try {
//            Utils.cleanVideoCacheDir(getApplicationContext());
//            Log.d("__onDestroy", "clear cache");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("__IOException", e.toString());
//        }

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        pause();

        exoPlayer.release();
        exoPlayer.removeListener(this);

       //notificationManager.cancelNotify();

        mediaSession.release();

        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                exoPlayer.setVolume(0.8f);
                resume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying())
                    exoPlayer.setVolume(0.1f);
                break;
        }

    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = PlaybackStatus.STOPPED;
                break;
            case Player.STATE_IDLE:
                status = PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
                break;
            default:
                status = PlaybackStatus.IDLE;
                break;
        }

        if(!status.equals(PlaybackStatus.IDLE))
            //notificationManager.startNotify(status);

        //EventBus.getDefault().post(status);
            EventBus.getDefault().post(new PlayerBusStatus(status));
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        String errordetails;
        //EventBus.getDefault().post(PlaybackStatus.ERROR);
        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                errordetails = error.getSourceException().getMessage();
                EventBus.getDefault().post(new PlayerBusError(errordetails));
                break;

            case ExoPlaybackException.TYPE_RENDERER:
                errordetails = error.getRendererException().getMessage();
                EventBus.getDefault().post(new PlayerBusError(errordetails));
                break;

            case ExoPlaybackException.TYPE_UNEXPECTED:
                errordetails = error.getUnexpectedException().getMessage();
                EventBus.getDefault().post(new PlayerBusError(errordetails));
                break;
        }

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.d("__onCacheAvailable", percentsAvailable + "");
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getApplicationContext(), "Animize");
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent,null,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true))
                    .createMediaSource(uri);
    }

    public String getStatus(){
        return status;
    }

    public void play() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);

        audioManager.abandonAudioFocus(this);
    }

    public void resume() {
        if (streamUrl != null)
            play();
    }

    public void stop() {
        exoPlayer.stop();
        exoPlayer.release();

        audioManager.abandonAudioFocus(this);
    }
    public void init(String streamUrl) {
        this.streamUrl = streamUrl;

        // TODO: add caching
        //HttpProxyCacheServer proxy = AppController.getProxy(getApplicationContext());
        //proxy.registerCacheListener(this, streamUrl);
        //String proxyUrl = proxy.getProxyUrl(streamUrl);
        //Log.e("ER",proxyUrl);
        //MediaSource mediaSource;
        //if (streamUrl.contains(".mp")) {
          //  mediaSource = buildMediaSource(Uri.parse(proxyUrl));
        //} else {
        //    mediaSource = buildMediaSource(Uri.parse(streamUrl));
        //}

        MediaSource mediaSource = buildMediaSource(Uri.parse(streamUrl));
        exoPlayer.prepare(mediaSource);
        exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        exoPlayer.setPlayWhenReady(true);
    }

    public void playOrPause(String urli) {
        if(urli != null){
            System.out.println("OK");
            if (streamUrl != null && streamUrl.equals(urli)) {
                play();
            } else {
                Log.e("Service",urli);
                init(urli);

            }
        }

    }

    public boolean isPlaying(){
        return this.status.equals(PlaybackStatus.PLAYING);
    }

    public MediaSessionCompat getMediaSession(){
        return mediaSession;
    }

}
