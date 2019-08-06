package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.database.PackageStarDBHelper;
import ml.dvnlabs.animize.database.RecentPlayDBHelper;
import ml.dvnlabs.animize.database.model.recentland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.fragment.information.coverview;
import ml.dvnlabs.animize.model.packageinfo;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.recyclerview.packagelist.packagelist_adapter;
import ml.dvnlabs.animize.recyclerview.staggered.package_metagenre_adapter;

import static android.view.View.GONE;
import static ml.dvnlabs.animize.activity.MainActivity.setWindowFlag;

public class packageView extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView cover_toolbar,recent_img;
    private static final int CODE_GET_REQUEST = 1024;
    private ArrayList<playlist_model> playlist_models;
    private ArrayList<packageinfo> modelinfo;
    packagelist_adapter adapter;
    private RecyclerView listview,genrelist;
    private TextView genr,synops,recent_title,recent_episode;
    private CoordinatorLayout package_parent;
    private RelativeLayout recent_container;
    String pkganim;

    private MenuItem pack_star;

    PackageStarDBHelper packageStarDBHelper;
    RecentPlayDBHelper recentPlayDBHelper;

    private NestedScrollView container;
    private AVLoadingIndicatorView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_view);

        //make translucent statusBar on kitkat devices


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //make fully Android Transparent Status bar

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);




        initialize();
        Intent intent = getIntent();
        if(getIntent().getStringExtra("package") != null){
            //setIdanim(intent.getStringExtra("id_anim"));
            pkganim = intent.getStringExtra("package");
            //intent.removeExtra("id_anim");
        }
        if(!pkganim.isEmpty()){

            System.out.println(pkganim);
            packageStarDBHelper = new PackageStarDBHelper(this);
            recentPlayDBHelper = new RecentPlayDBHelper(this);

        }
        GetInfo();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize(){
        Toolbar toolbar = findViewById(R.id.pv_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("");
        collapsingToolbarLayout = findViewById(R.id.pv_collapse_toolbar);
        collapsingToolbarLayout.setTitle("Loading...");
        listview = findViewById(R.id.packageview_list);
        genrelist = findViewById(R.id.packageview_genrelist);
        synops = findViewById(R.id.packageview_synopsis);
        /*genr = findViewById(R.id.packageview_genre);*/
        container =findViewById(R.id.packageview_container);
        loading = findViewById(R.id.packageview_loading);
        cover_toolbar = findViewById(R.id.toolbar_cover);
        package_parent = findViewById(R.id.package_parent);
        recent_container = findViewById(R.id.packageview_recent_container);
        recent_episode = findViewById(R.id.packageview_recent_episode);
        recent_title = findViewById(R.id.packageview_recent_title);
        recent_img = findViewById(R.id.packageview_recent_img);
        //container.setVisibility(View.GONE);

        modelinfo = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        show_recent();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.package_toolbar, menu);
        pack_star = menu.findItem(R.id.package_star);
        if(packageStarDBHelper.isAvail()){
            System.out.println("ON Read DB star");
            readStarStatus read = new readStarStatus();
            read.execute();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ChangeStar star = new ChangeStar();
        if(id == R.id.package_star){

            if(packageStarDBHelper.isStarred(pkganim)){
                Log.e("CLICKED","UNSTAR");
                star.execute("UNSTAR");
            }else {
                Log.e("CLICKED","STAR");
                star.execute("STAR");
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getplaylist(){
        String url = Api.url_playlist_play+pkganim;
        APINetworkRequest networkRequest = new APINetworkRequest(this,getplaylist,url,CODE_GET_REQUEST,null);

    }

    private void GetInfo(){
        String url = Api.url_packageinfo+pkganim;
        APINetworkRequest networkRequest = new APINetworkRequest(this,getInfo,url,CODE_GET_REQUEST,null);

    }


    FetchDataListener getInfo = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try{
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")) {
                    parserInfo(object.getJSONArray("anim"));
                }
            }catch (JSONException e){
                e.printStackTrace();

            }
            getplaylist();
        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {
            loading.setVisibility(View.VISIBLE);
            container.setVisibility(GONE);

        }
    };

    FetchDataListener getplaylist = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try{
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")) {
                    parseplaylist(object.getJSONArray("anim"));
                }
            }catch (JSONException e){
                e.printStackTrace();

            }
            loading.setVisibility(GONE);
            container.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {

        }
    };
    private void parserInfo(JSONArray info){
        try {
            modelinfo.clear();
            for (int i= 0;i<info.length();i++){
                JSONObject object = info.getJSONObject(i);
                String packages = object.getString("package_anim");
                String nameanim = object.getString("name_catalogue");
                String synop = object.getString("synopsis");
                String totep = object.getString("total_ep_anim");
                String rate = object.getString("rating");
                String mal = object.getString("mal_id");
                String cover = object.getString("cover");
                JSONArray genre_json =object.getJSONArray("genre") ;
                List<String> genres =new ArrayList<>();
                for (int j=0;j<genre_json.length();j++){
                    genres.add(genre_json.getString(j));
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                modelinfo.add(new packageinfo(packages,nameanim,synop,totep,rate,mal,genres,cover));
            }
            collapsingToolbarLayout.setTitle(modelinfo.get(0).getname());
            synops.setText(modelinfo.get(0).getSynopsis());
            /*List<String> genres = new ArrayList<>();
            genres = modelinfo.get(0).getGenre();*/
            package_metagenre_adapter adaptergenre = new package_metagenre_adapter(modelinfo.get(0).getGenre(),this,R.layout.rv_staggered);
            int spanStaggered = adaptergenre.getItemCount() < 7 ? 1 : 2;
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanStaggered,StaggeredGridLayoutManager.HORIZONTAL);
            genrelist.setLayoutManager(layoutManager);
            genrelist.setAdapter(adaptergenre);
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo.get(0).getCover())
                    .transition(new DrawableTransitionOptions()
                            .crossFade()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(cover_toolbar);
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo.get(0).getCover()).transform(new BlurTransformation(10,3))
                    .transition(new DrawableTransitionOptions()
                            .crossFade()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    package_parent.setBackground(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    package_parent.setBackground(placeholder);
                }
            });
            cover_toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    coverview alertDialog = coverview.newInstance();
                    alertDialog.setUrl(modelinfo.get(0).getCover());
                    alertDialog.show(fm, "coverview");


                }
            });
            show_recent();
        }catch (JSONException e){
            Log.e("JSON ERROR:",e.toString());
        }
    }

    private void show_recent(){
        if (recentPlayDBHelper.isRecentPackAvail(pkganim)){
            System.out.println("RECENT AVAIL");
            readrecent readrecent = new readrecent();
            readrecent.execute("");
            /*recent_container.setVisibility(View.VISIBLE);*/
        }else{
            System.out.println("RECENT NOTAVAIL");
            recent_container.setVisibility(GONE);
        }
    }
    private void parseplaylist(JSONArray playlist){
        try {
            playlist_models = new ArrayList<>();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            listview.setLayoutManager(layoutManager);

            for (int i = 0;i<playlist.length();i++){
                JSONObject object=playlist.getJSONObject(i);
                String url_img = object.getString("thumbnail");
                String title = object.getString("name_catalogue");
                String episode = object.getString("episode_anim");
                String id_an = object.getString("id_anim");
                String pkg = object.getString("package_anim");
                playlist_models.add(new playlist_model(url_img,title,episode,id_an,pkg));

            }

            adapter = new packagelist_adapter(playlist_models,this,R.layout.package_playlist_view);

            listview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private class readStarStatus extends AsyncTask<String,Void,Boolean>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Boolean doInBackground(String... params){

            return packageStarDBHelper.isStarred(pkganim);

        }

        @Override
        protected void onPostExecute(Boolean pa){
            if(pa){
                pack_star.setIcon(R.drawable.ic_star);
            }else {
                pack_star.setIcon(R.drawable.ic_star_nofill);
            }

        }
    }
    private class ChangeStar extends AsyncTask<String,Void,Boolean>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Boolean doInBackground(String... params){
            String change = params[0];
            if(change.equals("UNSTAR")){
                System.out.println("UNSTAR");
                packageStarDBHelper.unStar(pkganim);
            }else if(change.equals("STAR")){
                System.out.println("STAR");
                packageStarDBHelper.add_star(pkganim);
            }
            return packageStarDBHelper.isStarred(pkganim);


        }

        @Override
        protected void onPostExecute(Boolean pa){
            System.out.println("OK");
            String status;
            if(pa){
                System.out.println("STAR");
                pack_star.setIcon(R.drawable.ic_star);
                status = "Add to Star Success";
            }else {
                System.out.println("UNSTAR");
                pack_star.setIcon(R.drawable.ic_star_nofill);
                status = "Remove Star Success";
            }
            Toast.makeText(getApplicationContext(),status,Toast.LENGTH_SHORT).show();
        }
    }

    private class readrecent extends AsyncTask<String,Void, recentland>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected recentland doInBackground(String... strings) {
            return recentPlayDBHelper.read_recent_pn_package(pkganim);
        }

        @Override
        protected void onPostExecute(recentland recentland) {
            super.onPostExecute(recentland);
            recent_container.setVisibility(View.VISIBLE);
            recent_title.setText(recentland.getPackage_name());
            String ep = "Episode: "+recentland.getEpisode();
            recent_episode.setText(ep);
            Glide.with(getBaseContext())
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(recentland.getUrl_cover()).transform(new RoundedCornersTransformation(10,0))
                    .transition(new DrawableTransitionOptions()
                            .crossFade()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    recent_img.setBackground(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    recent_img.setBackground(placeholder);
                }
            });
            recent_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), animplay_activity.class);
                    intent.putExtra("id_anim",recentland.getAnmid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Log.e("CLICKED",this.vl_model.getIdn());
                    getApplicationContext().startActivity(intent);
                }
            });
        }
    }
}
