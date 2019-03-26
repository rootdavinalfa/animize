package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.Event.PlayerBusError;
import ml.dvnlabs.animize.Event.PlayerBusStatus;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.adapter.playlist_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.RequestHandler;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.loader.animplay_loader;
import ml.dvnlabs.animize.model.play_model;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.model.videoplay_model;
import ml.dvnlabs.animize.player.PlaybackStatus;
import ml.dvnlabs.animize.player.PlayerManager;
import ml.dvnlabs.animize.player.PlayerService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static ml.dvnlabs.animize.activity.MainActivity.setWindowFlag;
import static ml.dvnlabs.animize.player.PlayerManager.getService;

public class animplay_activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private boolean isRestart = false;
    private PlayerView playerView;

    private boolean isFullscreen = false;
    private boolean isXpandSynopsis = false;
    private boolean isXpandPlaylist = false;
    private boolean isInit = true;
    private CardView body_play;

    ArrayList<videoplay_model> modeldata;
    ArrayList<playlist_model> playlist_models;
    playlist_adapter adapter;

    private LinearLayout synopsis_cont;
    private LinearLayout playlist_cont;
    private ExpandableLayout synopsis_bdy;
    private ExpandableLayout playlist_bdy;
    private ImageView more_img;
    private ImageView more_imgplay;
    private ImageView fs_btn;
    private ProgressBar ply;
    private TextView ply_name,ply_episod;

    AVLoadingIndicatorView video_loadba;
    AVLoadingIndicatorView detail_loadba;
    AVLoadingIndicatorView video_buffer;

    private PlayerManager playerManager;
    private TextView tn;
    private TextView ep;
    private TextView rate;
    private TextView syn;
    private Animation animation;

    String pkg_anim;
    int request_step=1;
    int media_height;
    int media_width;
    String idanim;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animplay_activity);


        //video_loadba.setVisibility(View.VISIBLE);
        //detail_loadba.setVisibility(View.VISIBLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        idanim = getIntent().getStringExtra("id_anim");
        playerManager = PlayerManager.with(this);
        if(getService() == null){
            playerManager.bind();
        }

        if(getSupportLoaderManager().getLoader(0)!=null){
            String a = "RESTART";
            Log.e("INF",a);
            getSupportLoaderManager().initLoader(0,null,this);
        }
        if(getSupportLoaderManager().getLoader(0)==null){
            String a = "INIT";
            Log.e("INF",a);
            getSupportLoaderManager().restartLoader(0,null,this);
        }
        body_play = (CardView)findViewById(R.id.card_view);
        synopsis_cont = (LinearLayout)findViewById(R.id.play_synopsis_container);
        playlist_cont = (LinearLayout)findViewById(R.id.play_playlist_container);
        more_imgplay = (ImageView)findViewById(R.id.img_playlist_more);
        more_img = (ImageView)findViewById(R.id.img_synopsis_more);
        fs_btn = (ImageView)findViewById(R.id.exo_fullscreen_icon);
        ply_name = (TextView)findViewById(R.id.player_name);
        ply_episod = (TextView)findViewById(R.id.player_episode);
        video_buffer = (AVLoadingIndicatorView)findViewById(R.id.exo_buffering);
        body_play.setVisibility(View.GONE);
        synopsis_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synopsis_xpand();
                System.out.println("CLICKED SYNOPSIS");
            }
        });
        playlist_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlist_xpand();
            }
        });
        ply = (ProgressBar)findViewById(R.id.progress_playlist);

        synopsis_bdy = (ExpandableLayout) findViewById(R.id.synopsis_body);
        playlist_bdy = (ExpandableLayout)findViewById(R.id.playlist_body);
        syn = (TextView)findViewById(R.id.synop_play);
        playlist_models = new ArrayList<>();
        notbuffering();
    }

    @Subscribe
    public void onEvent(PlayerBusStatus status){
        if(status.getStatus().equals(PlaybackStatus.LOADING)){
            buffering();
        }
        else {
            notbuffering();
        }

    }
    @Subscribe
    public void onEvent(PlayerBusError error){
        TextView texterror = (TextView)findViewById(R.id.exo_error_message);
        texterror.setText(error.geterror());

        System.out.print("ERROR:"+error.geterror());
    }
    public void buffering(){
        video_buffer = (AVLoadingIndicatorView)findViewById(R.id.exo_buffering);
        video_buffer.show();
    }
    public void notbuffering(){
        video_buffer = (AVLoadingIndicatorView)findViewById(R.id.exo_buffering);
        video_buffer.hide();
    }

    public void setIdanim(String idanim){
        this.idanim = idanim;
    }
    public void newvideo(){
        body_play.setVisibility(View.GONE);
        isInit = true;
        detail_loadba.setVisibility(View.VISIBLE);
        video_loadba.setVisibility(View.VISIBLE);
        if(isXpandSynopsis){
            //isXpandSynopsis = false;
            synopsis_xpand();
        }
        if(isXpandPlaylist){
            //isXpandPlaylist =false;
            playlist_xpand();
        }


        more_imgplay.setImageResource(R.drawable.ic_expand_expand);
        more_img.setImageResource(R.drawable.ic_expand_expand);
        getSupportLoaderManager().restartLoader(0,null,this);

    }
    private void playlist_xpand(){
        String packa = pkg_anim;
        System.out.println("STATUS:"+String.valueOf(isXpandPlaylist)+" PACK:"+packa);
        if(isXpandPlaylist){

            //synopsis_bdy.setVisibility(View.GONE);
            more_imgplay.setImageResource(R.drawable.ic_expand_expand);
            playlist_bdy.collapse();
            isXpandPlaylist = false;
        }else{
            isInit = false;

            //synopsis_bdy.setVisibility(View.VISIBLE);
            more_imgplay.setImageResource(R.drawable.ic_expand_collapse);
            isXpandPlaylist = true;
            if(playlist_models.isEmpty()){
                ply.setVisibility(View.VISIBLE);
                getPL();
            }

            //getSupportLoaderManager().initLoader(1,null,this);
            playlist_bdy.expand();
        }

    }
    private void synopsis_xpand(){

        System.out.println("STATUS:"+String.valueOf(isXpandSynopsis));
        if(isXpandSynopsis){

            //synopsis_bdy.setVisibility(View.GONE);
            more_img.setImageResource(R.drawable.ic_expand_expand);
            synopsis_bdy.collapse();
            isXpandSynopsis = false;
        }else{

            //synopsis_bdy.setVisibility(View.VISIBLE);
            more_img.setImageResource(R.drawable.ic_expand_collapse);
            isXpandSynopsis = true;
            synopsis_bdy.expand();
        }

    }
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        int currentOrient = getResources().getConfiguration().orientation;
        if(currentOrient == Configuration.ORIENTATION_LANDSCAPE){

            hideSystemUIFullscreen();
        }else{

            showSystemUIFullscreen();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUIFullscreen(){

        FrameLayout.LayoutParams media = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        media_height = media.height;
        media_width = media.width;
        media.height = FrameLayout.LayoutParams.MATCH_PARENT;
        media.width = FrameLayout.LayoutParams.MATCH_PARENT;

        CardView cardView = (CardView)findViewById(R.id.card_view);
        cardView.setVisibility(View.GONE);

        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        isFullscreen =true;

    }
    @SuppressLint("InlinedApi")
    private void showSystemUIFullscreen(){
        FrameLayout.LayoutParams media = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        media.height = media_height;
        media.width = FrameLayout.LayoutParams.MATCH_PARENT;
        CardView cardView = (CardView)findViewById(R.id.card_view);
        cardView.setVisibility(View.VISIBLE);
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        isFullscreen = false;
    }

    public void btn_fullscreen(View view){
        Drawable buttun_fullscren;

        if(isFullscreen){
            buttun_fullscren = getResources().getDrawable(R.drawable.ic_fullscreen_expand);
            fs_btn.setImageDrawable(buttun_fullscren);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(!isFullscreen){
            buttun_fullscren = getResources().getDrawable(R.drawable.ic_fullscreen_skrink);
            fs_btn.setImageDrawable(buttun_fullscren);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onRestart(){
        //playerManager.bind();

        super.onRestart();
        isRestart = true;
    }
    private void show_video(JSONArray video){
        video_loadba = (AVLoadingIndicatorView) findViewById(R.id.video_loadingbar);
        playerView = (PlayerView)findViewById(R.id.animplay_view);
        detail_loadba = (AVLoadingIndicatorView) findViewById(R.id.detail_loadingbar);

        modeldata = new ArrayList<>();
        try{
            for(int i = 0;i<video.length();i++){
                JSONObject jsonObject = video.getJSONObject(i);
                String ur = jsonObject.getString(Api.JSON_source);
                String nm = jsonObject.getString("name_catalogue");
                String epi = jsonObject.getString(Api.JSON_episode_anim);
                String tot = jsonObject.getString("total_ep_anim");
                String rat = jsonObject.getString("rating");
                String pack = jsonObject.getString("package_anim");
                String syi = jsonObject.getString("synopsis");
                Log.e("DATA: ",nm+tot);
                modeldata.add(new videoplay_model(nm,epi,tot,rat,syi,pack,ur));

            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
        detail_loadba.setVisibility(View.GONE);
        url = modeldata.get(0).getSource_url();
        pkg_anim = modeldata.get(0).getPack();
        //Log.e("INFO",url);
        tn = (TextView) findViewById(R.id.anime_name_play);
        ply_name.setText(modeldata.get(0).getName_anim());
        tn.setText(modeldata.get(0).getName_anim());
        ep = (TextView) findViewById(R.id.episode_play);
        ply_episod.setText(getString(R.string.episode_text)+": "+modeldata.get(0).getEpisode());
        String epe = getString(R.string.episode_text)+": "+modeldata.get(0).getEpisode()+" "+getString(R.string.string_of)+" "+modeldata.get(0).getTotal_ep_anim();
        ep.setText(epe);
        rate = (TextView)findViewById(R.id.rating_play);
        String rate_text =getString(R.string.rating_text)+modeldata.get(0).getRating();
        rate.setText(rate_text);
        //syn = (TextView)findViewById(R.id.synop_play);

        syn.setText(modeldata.get(0).getSysnop());

        if(playerManager.isServiceBound()){
            playerManager.playOrPause(url);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            playerView.setPlayer(getService().exoPlayer);
        }
        body_play.setVisibility(View.VISIBLE);
        video_loadba.setVisibility(View.GONE);
    }
    private void setplaylist(JSONArray playlist){

        RecyclerView rv = (RecyclerView)findViewById(R.id.playlist_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        try{
            for (int i = 0;i<playlist.length();i++){
                JSONObject object=playlist.getJSONObject(i);
                String url_img = object.getString("thumbnail");
                String title = object.getString("name_catalogue");
                String episode = object.getString("episode_anim");
                String id_an = object.getString("id_anim");
                String pkg = object.getString("package_anim");
                playlist_models.add(new playlist_model(url_img,title,episode,id_an,pkg));
            }
            adapter = new playlist_adapter(playlist_models,this,R.layout.playlist_view);

            rv.setAdapter(adapter);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        playerManager.bind();
        super.onResume();
    }
    @Override
    protected void onPause(){
        //playerManager.unbind();
        super.onPause();
    }

    @Override
    protected void onStart(){
        playerManager.bind();
        super.onStart();
        EventBus.getDefault().register(this);

        //playerManager.bind();


    }
    @Override
    protected void onStop(){
        //playerManager.unbind();
        //playerView.setPlayer(null);
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void releaseall(){
        if (getService() != null){
            playerManager.unbind();
        }
    }
    @Override
    protected void onDestroy() {

        if (getService() != null){
            playerManager.unbind();
        }
        //playerView.setPlayer(null);
        //if(isMyServiceRunning(playerManager.getClass())){

        //}

        super.onDestroy();
    }

    @Override
    public void onBackPressed(){

        Drawable buttun_fullscren;
        ImageView imageView = (ImageView) findViewById(R.id.exo_fullscreen_icon);
        if(isFullscreen){
            buttun_fullscren = getResources().getDrawable(R.drawable.ic_fullscreen_expand);
            imageView.setImageDrawable(buttun_fullscren);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
        if(!isFullscreen){
            Intent intent = new Intent(this, animlist_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
    private void getPL(){
        try{
            String url_play = Api.url_playlist_play+pkg_anim;
            APINetworkRequest getpl = new APINetworkRequest(this,playlist_getter,url_play,CODE_GET_REQUEST,null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //PLAYLIST JSON PARSER
    private void pl_json_parser(String data){
        try {
            JSONObject object = new JSONObject(data);
            if (!object.getBoolean("error")) {
                ply.setVisibility(View.GONE);
                setplaylist(object.getJSONArray("anim"));
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }
    FetchDataListener playlist_getter = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            pl_json_parser(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("URL:",msg);
        }

        @Override
        public void onFetchStart() {

        }
    };
    @Override
    public Loader<String> onCreateLoader(int id,Bundle args){
    String urli;
    if(isInit){
        urli = Api.api_animplay+idanim;
        return new animplay_loader(this,urli,null,CODE_GET_REQUEST);
    }
    /*
    else{
        urli = Api.url_playlist_play+pkg_anim;
        return new animplay_loader(this,urli,null,CODE_GET_REQUEST);
    } */
    return null;
    }

@Override
    public void onLoadFinished(Loader<String> loader,String data){
    try {
        JSONObject object = new JSONObject(data);
        if (!object.getBoolean("error")) {

            //refreshing the animlist after every operation
            //so we get an updated list
            //we will create this method right now it is commented
            //because we haven't created it yet
            if(isInit){
                show_video(object.getJSONArray("anim"));
            }
            /*
            if(isXpandPlaylist){
                ply.setVisibility(View.GONE);
                setplaylist(object.getJSONArray("anim"));
            }*/





        }
    } catch (JSONException e) {

        e.printStackTrace();
    }
}
    @Override
    public void onLoaderReset(Loader<String> loader) {}
}
