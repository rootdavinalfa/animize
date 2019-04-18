package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ml.dvnlabs.animize.Event.PlayerBusError;
import ml.dvnlabs.animize.Event.PlayerBusStatus;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.adapter.playlist_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.fragment.tabs.animplay.details;
import ml.dvnlabs.animize.loader.animplay_loader;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.model.videoplay_model;
import ml.dvnlabs.animize.pager.aplay_viewpageradapter;
import ml.dvnlabs.animize.pager.passdata_arraylist;
import ml.dvnlabs.animize.player.PlaybackStatus;
import ml.dvnlabs.animize.player.PlayerManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static ml.dvnlabs.animize.player.PlayerManager.getService;

public class animplay_activity extends AppCompatActivity{
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private boolean isRestart = false;
    private PlayerView playerView;

    private boolean isFullscreen = false;
    private boolean isInit = true;

    ArrayList<videoplay_model> modeldata;
    ArrayList<playlist_model> playlist_models;
    playlist_adapter adapter;

    private TextView errortxt;
    private AVLoadingIndicatorView loadbar;
    private ImageView errorIMG;
    private LinearLayout contbdy;
    private RelativeLayout errcont;
    private TabLayout aplay_tabs;
    private TabItem aplay_details,aplay_more;
    private ViewPager aplay_viewpager;
    private ImageView fs_btn;
    private TextView ply_name,ply_episod;
    AVLoadingIndicatorView video_buffer;

    private PlayerManager playerManager;

    String pkg_anim;
    int request_step=1;
    int media_height;
    int media_width;
    String idanim;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.animplay_activity);
        initial_setup();

        idanim = getIntent().getStringExtra("id_anim");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        playerManager = PlayerManager.with(this);
        if(getService() == null){
            playerManager.bind();
        }
        getVideo();
        notbuffering();
    }
FetchDataListener getvideo = new FetchDataListener() {
    @Override
    public void onFetchComplete(String data) {
        try{
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
        }catch (JSONException e){
            e.printStackTrace();

        }
    }
    @Override
    public void onFetchFailure(String msg) {
        //aplay_details.setVisibility(View.GONE);
        errcont.setVisibility(View.VISIBLE);
        errortxt.setVisibility(View.VISIBLE);
        errortxt.setText(msg);
        errorIMG.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFetchStart() {
        //aplay_details.setVisibility(View.GONE);
        errcont.setVisibility(View.VISIBLE);
        errortxt.setVisibility(View.GONE);
        errorIMG.setVisibility(View.GONE);
    }
};
    private void getVideo(){
        String urli = Api.api_animplay+idanim;
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(this,getvideo,urli,CODE_GET_REQUEST,null);
    }
    private void initial_setup(){
        aplay_tabs = (TabLayout)findViewById(R.id.aplay_tabs);
        aplay_details = (TabItem)findViewById(R.id.aplay_tabs_details);
        aplay_more = (TabItem)findViewById(R.id.aplay_tabs_more);
        aplay_viewpager = (ViewPager)findViewById(R.id.aplay_pager);
        aplay_tabs.setupWithViewPager(aplay_viewpager);
        aplay_viewpageradapter adapter = new aplay_viewpageradapter(getSupportFragmentManager(),aplay_tabs.getTabCount());
        aplay_viewpager.setAdapter(adapter);
        aplay_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(aplay_tabs));
        fs_btn = (ImageView)findViewById(R.id.exo_fullscreen_icon);
        video_buffer = (AVLoadingIndicatorView)findViewById(R.id.exo_buffering);
        ply_name = (TextView)findViewById(R.id.player_name);
        ply_episod = (TextView)findViewById(R.id.player_episode);
        errortxt = (TextView)findViewById(R.id.aplay_txt_error);
        contbdy = (LinearLayout)findViewById(R.id.aplay_details_cont);
        errcont = (RelativeLayout)findViewById(R.id.aplay_details_errorcont);
        loadbar = (AVLoadingIndicatorView)findViewById(R.id.aplay_bar_loading);
        errorIMG = (ImageView)findViewById(R.id.aplay_img_error);

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

        System.out.println("ERROR:"+error.geterror());
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
        //video_loadba = (AVLoadingIndicatorView) findViewById(R.id.video_loadingbar);
        playerView = (PlayerView)findViewById(R.id.animplay_view);
        //detail_loadba = (AVLoadingIndicatorView) findViewById(R.id.detail_loadingbar);

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
        url = modeldata.get(0).getSource_url();
        //pkg_anim = modeldata.get(0).getPack();
        //Log.e("INFO",url);
        ply_name.setText(modeldata.get(0).getName_anim());
        String epe = getString(R.string.episode_text)+": "+modeldata.get(0).getEpisode()+" "+getString(R.string.string_of)+" "+modeldata.get(0).getTotal_ep_anim();
        ply_episod.setText(epe);
        if(playerManager.isServiceBound()){
            playerManager.playOrPause(url);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            playerView.setPlayer(getService().exoPlayer);
        }
        //aplay_details.setVisibility(View.VISIBLE);
        errcont.setVisibility(View.GONE);
        errortxt.setVisibility(View.GONE);
        errorIMG.setVisibility(View.GONE);
        sendmodelplay(modeldata,datasender);

    }
    private void sendmodelplay(ArrayList<videoplay_model>data,passdata_arraylist senddata){
        senddata.onDataReceived(data,idanim);

    }
    passdata_arraylist datasender = new passdata_arraylist() {
        @Override
        public void onDataReceived(ArrayList<videoplay_model> data,String id) {
            String tag = "android:switcher:" + R.id.aplay_pager + ":" + 0;
            details f = (details) getSupportFragmentManager().findFragmentByTag(tag);
            f.receivedata(data,id);
        }
    };

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
            modeldata.clear();
        }
        playerView.setPlayer(null);
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
            Intent intent = new Intent(this, dashboard_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
}
