package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import ml.dvnlabs.animize.Event.PlayerBusError;
import ml.dvnlabs.animize.Event.PlayerBusStatus;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.InitInternalDBHelper;
import ml.dvnlabs.animize.database.RecentPlayDBHelper;
import ml.dvnlabs.animize.database.model.recentland;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.driver.util.RequestQueueVolley;
import ml.dvnlabs.animize.fragment.comment.threadComment;
import ml.dvnlabs.animize.fragment.popup.sourceSelector;
import ml.dvnlabs.animize.fragment.tabs.animplay.PlaylistFragment;
import ml.dvnlabs.animize.model.commentMainModel;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.fragment.tabs.animplay.details;
import ml.dvnlabs.animize.model.videoplay_model;
import ml.dvnlabs.animize.pager.aplayViewPagerAdapter;
import ml.dvnlabs.animize.pager.passdata_arraylist;
import ml.dvnlabs.animize.player.PlaybackStatus;
import ml.dvnlabs.animize.player.PlayerManager;
import ml.dvnlabs.animize.view.VideoOnSwipeTouchListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ml.dvnlabs.animize.player.PlayerManager.getService;

public class animplay_activity extends AppCompatActivity{
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private boolean isRestart = false;
    private PlayerView playerView;

    private InitInternalDBHelper initInternalDBHelper;
    private RecentPlayDBHelper recentPlayDBHelper;
    private boolean isFullscreen = false;
    public boolean isLocked = false;
    private boolean isInit = true;

    private Handler handler_player_recent;
    private Runnable update_recent;

    ArrayList<videoplay_model> modeldata;
    private TextView errortxt;
    private AVLoadingIndicatorView loadbar;
    private ImageView errorIMG;
    private LinearLayout contbdy;
    private RelativeLayout errcont;
    private TabLayout aplay_tabs;
    private TabItem aplay_details,aplay_more;
    private ViewPager aplay_viewpager;
    private ImageView fs_btn,video_artwork,locker,anim_download;
    private TextView ply_name,ply_episod,video_seektime;
    AVLoadingIndicatorView video_buffer;

    private PlayerManager playerManager;

    int media_height;
    int media_width;
    private String idanim,id_source;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animplay_activity);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        initial_setup();
        modeldata = new ArrayList<>();
        initInternalDBHelper = new InitInternalDBHelper(this);
        recentPlayDBHelper = new RecentPlayDBHelper(this);
        SqliteRead sqliteReadUser = new SqliteRead();
        sqliteReadUser.execute("OK");

        Intent intent = getIntent();
        if(getIntent().getStringExtra("id_anim") != null){
            setIdanim(intent.getStringExtra("id_anim"));
        }
        if(!idanim.isEmpty()){

            System.out.println(idanim);
        }
        playerManager = new PlayerManager(this);
        if(getService() == null){
            playerManager.bind();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notbuffering();
    }
    private void recentplayed(){
        if (recentPlayDBHelper.isRecentAvail(idanim)){
            read_rect();
        }else{
            playerrecent_write first_write = new playerrecent_write();
            first_write.execute("FIRST");
        }
    }

FetchDataListener getvideo = new FetchDataListener() {
    @Override
    public void onFetchComplete(String data) {
        try{
            JSONObject object = new JSONObject(data);
            if (!object.getBoolean("error")) {
                if(isInit){
                    show_video(object.getJSONArray("anim"));
                }
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
    public void getVideo(){
        System.out.println("KLKLKLKL "+this.idanim);
        String urlnew = Api.api_animplay+this.idanim;
        //Log.e("REQUEST: ",urlnew);
        new APINetworkRequest(this,getvideo,urlnew,CODE_GET_REQUEST,null);
    }
    private void initial_setup(){
        playerView = findViewById(R.id.animplay_views);
        aplay_tabs = findViewById(R.id.aplay_tabs);
        aplay_details = findViewById(R.id.aplay_tabs_details);
        aplay_more = findViewById(R.id.aplay_tabs_more);
        aplay_viewpager = findViewById(R.id.aplay_pager);
        aplay_tabs.setupWithViewPager(aplay_viewpager);
        aplayViewPagerAdapter adapter = new aplayViewPagerAdapter(getSupportFragmentManager(),aplay_tabs.getTabCount(),this);
        aplay_viewpager.setAdapter(adapter);
        aplay_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(aplay_tabs));
        fs_btn = findViewById(R.id.exo_fullscreen_icon);
        video_buffer = findViewById(R.id.exo_buffering);
        video_artwork = findViewById(R.id.exo_artwork);
        video_seektime = findViewById(R.id.exo_seektime);
        anim_download = findViewById(R.id.anim_download);
        locker = findViewById(R.id.exo_controller_lock);
        ply_name = findViewById(R.id.player_name);
        ply_name.setSelected(true);
        ply_episod = findViewById(R.id.player_episode);
        errortxt = findViewById(R.id.aplay_txt_error);
        contbdy = findViewById(R.id.aplay_details_cont);
        errcont = findViewById(R.id.aplay_details_errorcont);
        loadbar = findViewById(R.id.aplay_bar_loading);
        errorIMG = findViewById(R.id.aplay_img_error);
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
        TextView texterror = findViewById(R.id.exo_error_message);
        texterror.setText(error.geterror());

        System.out.println("ERROR:"+error.geterror());
    }
    public void buffering(){
        video_buffer = findViewById(R.id.exo_buffering);
        video_buffer.show();
    }
    public void notbuffering(){
        video_buffer = findViewById(R.id.exo_buffering);
        video_buffer.hide();
    }

    public void setIdanim(String idanim){
        this.idanim = idanim;
    }
    public void setSourceID(String idsource){
        this.id_source = idsource;
    }
    public void onConfigurationChanged(@NonNull Configuration newConfig){
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
        show_locker();
        isFullscreen =true;

    }
    @SuppressLint("InlinedApi")
    private void showSystemUIFullscreen(){
        FrameLayout.LayoutParams media = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        media.height = media_height;
        media.width = FrameLayout.LayoutParams.MATCH_PARENT;
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        hide_locker();
        isFullscreen = false;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void btn_fullscreen(View view){
        Drawable buttun_fullscren;

        if(isFullscreen){
            buttun_fullscren = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_fullscreen_expand,null);
            fs_btn.setImageDrawable(buttun_fullscren);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(!isFullscreen){
            buttun_fullscren = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_fullscreen_skrink,null);
            fs_btn.setImageDrawable(buttun_fullscren);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        /*
        if(getService() == null){
            playerManager.bind();
        }*/
        isRestart = true;
    }

    //Method for new video from new source
    public void newVideoWithNewSource(){
        System.out.println("KLKLKLKL "+this.idanim);
        String urlnew = Api.api_animplay+this.idanim+"/source/"+id_source;
        //Log.e("REQUEST: ",urlnew);
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(this,getvideo,urlnew,CODE_GET_REQUEST,null);
    }
    private void show_video(JSONArray video){
        if (!modeldata.isEmpty()){
            modeldata.clear();
        }
        //video_loadba = (AVLoadingIndicatorView) findViewById(R.id.video_loadingbar);

        //detail_loadba = (AVLoadingIndicatorView) findViewById(R.id.detail_loadingbar);
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
                String cover = jsonObject.getString("cover");
                JSONArray genre_json =jsonObject.getJSONArray("genres") ;
                List<String> genres =new ArrayList<>();
                for (int j=0;j<genre_json.length();j++){
                    genres.add(genre_json.getString(j));
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                String thmb = jsonObject.getString("thumbnail");
                //Log.e("DATA: ",nm+tot);
                modeldata.add(new videoplay_model(nm,epi,tot,rat,syi,pack,ur,genres,thmb,cover));

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
        //Log.e("DATA",modeldata.get(0).getEpisode());
        ply_episod.setText(epe);

        errcont.setVisibility(View.GONE);
        errortxt.setVisibility(View.GONE);
        errorIMG.setVisibility(View.GONE);
        if (modeldata!=null){
            sendmodelplay(modeldata,datasender);
            sendpkg(modeldata.get(0).getPack(),idanim);
            playerContanti();
        }
    }
    private void playerContanti(){
        getService().playOrPause(url);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        playerView.setUseArtwork(true);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        playerView.setUseController(true);
        locker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocked && isFullscreen){
                    locker.setImageResource(R.drawable.ic_locked);
                    playerView.setUseController(true);
                    //Toast.makeText(animplay_activity.this,"Controller unLocked!",Toast.LENGTH_SHORT).show();
                    isLocked = false;
                }else if(!isLocked && isFullscreen){
                    locker.setImageResource(R.drawable.ic_unlocked);
                    playerView.setUseController(false);
                    //Toast.makeText(animplay_activity.this,"Controller Locked!",Toast.LENGTH_SHORT).show();
                    isLocked = true;
                }
            }
        });
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility==0 && isFullscreen){
                    hide_locker();

                    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }else if(visibility >0 &&  isFullscreen){
                    show_locker();

                    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
        });



        playerView.setOnTouchListener(new VideoOnSwipeTouchListener(this,playerView,getService().getExoPlayer()){
            private int counting;
            private Handler handler = new Handler();
            public void onSwipeRight() {
                if (!isLocked){
                    String seektimes = "Forward: "+counting+ " Seconds";
                    video_seektime.setText(seektimes);
                    show_seektime();
                    unVisible();
                }
                //Toast.makeText(animplay_activity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                if (!isLocked){
                    String seektimes = "Rewind: "+counting+ " Seconds";
                    video_seektime.setText(seektimes);
                    show_seektime();
                    unVisible();
                }
                //Toast.makeText(animplay_activity.this, "left", Toast.LENGTH_SHORT).show();
            }
            private void show_seektime(){

                video_seektime.animate()
                        .translationY(video_seektime.getHeight())
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                video_seektime.setVisibility(View.VISIBLE);
                            }
                        });
                video_seektime.setVisibility(View.VISIBLE);
            }
            private void unVisible(){

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        video_seektime.animate()
                                .translationY(video_seektime.getHeight())
                                .alpha(0f)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        video_seektime.setVisibility(View.GONE);
                                    }
                                });
                        //video_seektime.setVisibility(View.GONE);
                    }
                },3000);
            }

            @Override
            public void onCounting(int count) {
                this.counting = count;
                //super.onCounting(count);
            }
        });
        playerView.setUseArtwork(true);
        playerView.setDefaultArtwork(getResources().getDrawable(R.drawable.ic_astronaut));
        playerView.setPlayer(getService().getExoPlayer());
        Glide.with(this).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_picture_light).error(R.drawable.ic_picture_light))
                .load(modeldata.get(0).getUrl_thmb())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()).into(video_artwork);
        recentplayed();
    }
    private void show_locker(){
        locker.animate()
                .translationY(locker.getHeight())
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        locker.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void hide_locker(){
        locker.animate()
                .translationY(locker.getHeight())
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        locker.setVisibility(View.GONE);
                    }
                });
    }

    //send pkg id to more fragment
    private void sendpkg(String pkg,String anim){
        String tag = "android:switcher:" + R.id.aplay_pager + ":" + 1;
        PlaylistFragment f = (PlaylistFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if(f != null){
            f.receivedata(pkg,anim);
        }
        String tag1 = "android:switcher:" + R.id.aplay_pager + ":" + 0;
        details f1 = (details) getSupportFragmentManager().findFragmentByTag(tag1);
        if(f1 != null){
            f1.receivestring(pkg,modeldata.get(0).getCover());
        }
    }

    //Sending arraylist to details fragment
    private void sendmodelplay(ArrayList<videoplay_model>data,passdata_arraylist senddata){
        Log.e("CHECK ID ANIM",idanim);
        if (!data.isEmpty()){
            senddata.onDataReceived(data,idanim);
        }

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
        super.onResume();
        if(getService() != null){
            recentplayed();
            //playerManager.bind();
        }


    }
    @Override
    protected void onPause(){
        //playerManager.unbind();
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);

        //playerManager.bind();


    }
    @Override
    protected void onStop(){
        //playerManager.unbind();
        //playerView.setPlayer(null);
        EventBus.getDefault().unregister(this);
        handler_player_recent.removeCallbacks(update_recent);
        super.onStop();
    }

    public void releaseall(){
        if (getService() != null){
            playerManager.unbind();
        }
    }
    @Override
    protected void onDestroy() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("aplay",0);

        super.onDestroy();
        if(pref != null){
            Log.e("CLEARING:","CLEAR SharedPreference");
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

        }
        handler_player_recent.removeCallbacks(update_recent);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed(){
        Drawable buttun_fullscren;
        threadComment threadComment = (threadComment)getSupportFragmentManager().findFragmentByTag("COMMENT_THREAD");
        ImageView imageView = (ImageView) findViewById(R.id.exo_fullscreen_icon);

        //Check is threadComment fragment visible or not
        if (threadComment!=null&&threadComment.isVisible()){
            System.out.println("THREAD COMMENT UNVISIBLING");
            closereplyfragment();
        }else {
            if(isFullscreen){
                buttun_fullscren = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_fullscreen_expand,null);
                imageView.setImageDrawable(buttun_fullscren);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                playerView.setUseController(true);
                isLocked = false;

            }
            if(!isFullscreen){
                if(getService()!=null&&playerManager.isServiceBound()){
                    playerManager.unbind();
                    if (!modeldata.isEmpty()){
                        modeldata.clear();
                    }
                    //idanim = null;
                }
                SharedPreferences pref = getApplicationContext().getSharedPreferences("aplay",0);
                if(pref != null){
                    Log.e("CLEARING:","CLEAR SharedPreference");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();

                }
                if (handler_player_recent != null){
                    handler_player_recent.removeCallbacks(update_recent);
                }
                RequestQueueVolley queue = new RequestQueueVolley(this);
                queue.clearRequest();
                super.onBackPressed();
                //finish();
                //unbindService(serviceConnection);
                //Intent intent = new Intent(this, dashboard_activity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            }
        }
    }
    public void showsourceselector(String lang,String idanim){
        sourceSelector sourceselector = new sourceSelector();
        sourceselector.show(getSupportFragmentManager(),"sourceselector");
        sourceselector.language(lang,idanim,this);
    }

    public void showreplyfragment(ArrayList<commentMainModel> model){
        aplay_tabs.setVisibility(View.GONE);
        aplay_viewpager.setVisibility(View.GONE);

        threadComment se = threadComment.newInstance(model,idanim);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,R.anim.slide_down)
                .replace(R.id.aplay_fragment_comment_thread,se,"COMMENT_THREAD")
                .addToBackStack("COMMENT_THREAD").commit();
        //se.receivedata(model);

    }
    public void closereplyfragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        threadComment simpleFragment = (threadComment) fragmentManager
                .findFragmentById(R.id.aplay_fragment_comment_thread);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        aplay_tabs.setVisibility(View.VISIBLE);
        aplay_viewpager.setVisibility(View.VISIBLE);
    }
    //Recent Method
    private void read_rect(){
        playerrecent_read read = new playerrecent_read();
        read.execute("");
    }
    private void update_rect(){
        if (getService() !=null){
            if (Objects.requireNonNull(getService().getExoPlayer()).getPlaybackState() == Player.STATE_READY || getService().getExoPlayer().getPlaybackState() == Player.STATE_BUFFERING){
                update_recent = new Runnable() {
                    @Override
                    public void run() {
                        playerrecent_write first_write = new playerrecent_write();
                        first_write.execute("UPDATE");
                    }
                };
                handler_player_recent = new Handler();
                handler_player_recent.postDelayed(update_recent,5000);
            }
        }
    }
    private long getCurrentPlayTime(){
        return getService().getExoPlayer().getCurrentPosition();
    }
    private void seekPlayer(long positition){
        getService().getExoPlayer().seekTo(positition);
    }
    //Endof recent method

    /*
    * This section for getRecent and updateRecent + firstime
    * */
    //SQLite for write last played with timestamp
    public class playerrecent_write extends AsyncTask<String,Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            if (!modeldata.isEmpty()){
                if (params[0].equals("FIRST")){
                    String package_id = modeldata.get(0).getPack();
                    String package_name = modeldata.get(0).getName_anim();
                    String anmid =idanim;
                    int episode= Integer.valueOf(modeldata.get(0).getEpisode());
                    String url_cover = modeldata.get(0).getCover();
                    long timestamp = getCurrentPlayTime();
                    recentPlayDBHelper.add_recent(package_id,package_name,anmid,episode,url_cover,timestamp);

                }
                if (params[0].equals("UPDATE")){
                    String package_id = modeldata.get(0).getPack();
                    String package_name = modeldata.get(0).getName_anim();
                    String anmid =idanim;
                    int episode= Integer.valueOf(modeldata.get(0).getEpisode());
                    String url_cover = modeldata.get(0).getCover();
                    long timestamp = getCurrentPlayTime();
                    recentPlayDBHelper.update_recent(package_id,package_name,anmid,episode,url_cover,timestamp);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void vod) {
            super.onPostExecute(vod);
            update_rect();
        }
    }
    //SQLite for read last played with timestamp
    public class playerrecent_read extends AsyncTask<String,Void, recentland>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected recentland doInBackground(String... strings) {
            return recentPlayDBHelper.read_recent(idanim);
        }

        @Override
        protected void onPostExecute(recentland recentland) {
            System.out.println("TIMESTAMP::"+recentland.getTimestamp());
            if (getCurrentPlayTime() < recentland.getTimestamp()){
               seekPlayer(recentland.getTimestamp());
            }
            update_rect();
        }
    }

    /*
    * THIS SECTION FOR READ TOKEN ONLY
    * */
    //Sqlite for read token and id on local db
    public class SqliteRead extends AsyncTask<String,Void, userland> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected userland doInBackground(String... params){

            return initInternalDBHelper.getUser();

        }

        @Override
        protected void onPostExecute(userland usl){
            //Log.e("TOKEN:",usl.getToken());
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("aplay",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("iduser",usl.getIdUser());
            editor.putString("token",usl.getToken());
            editor.commit();
            //dash_profile_username.setText(usl.getNameUser());

        }
    }
}
