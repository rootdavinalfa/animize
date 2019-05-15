package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.packageinfo;
import ml.dvnlabs.animize.model.packagelist;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.recyclerview.packagelist_adapter;
import ml.dvnlabs.animize.recyclerview.playlist_adapter;

import static ml.dvnlabs.animize.activity.MainActivity.setWindowFlag;

public class packageView extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private static final int CODE_GET_REQUEST = 1024;
    private ArrayList<playlist_model> playlist_models;
    private ArrayList<packageinfo> modelinfo;
    packagelist_adapter adapter;
    private RecyclerView listview;
    private TextView genr,synops;
    String pkganim;

    private NestedScrollView container;
    private AVLoadingIndicatorView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_view);

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
        }
        GetInfo();

    }
    private void initialize(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.pv_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = findViewById(R.id.pv_collapse_toolbar);
        collapsingToolbarLayout.setTitle("");
        listview = (RecyclerView)findViewById(R.id.packageview_list);
        synops = (TextView)findViewById(R.id.packageview_synopsis);
        genr = (TextView)findViewById(R.id.packageview_genre);
        container = (NestedScrollView)findViewById(R.id.packageview_container);
        loading = (AVLoadingIndicatorView)findViewById(R.id.packageview_loading);
        //container.setVisibility(View.GONE);

        modelinfo = new ArrayList<>();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.package_toolbar, menu);
        return true;
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
            container.setVisibility(View.GONE);

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
            loading.setVisibility(View.GONE);
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
                JSONArray genre_json =object.getJSONArray("genre") ;
                List<String> genres =new ArrayList<>();
                for (int j=0;j<genre_json.length();j++){
                    genres.add(genre_json.getString(j));
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                modelinfo.add(new packageinfo(packages,nameanim,synop,totep,rate,mal,genres));
            }
            collapsingToolbarLayout.setTitle(modelinfo.get(0).getname());
            synops.setText(modelinfo.get(0).getSynopsis());
            List<String> genres = new ArrayList<>();
            genres = modelinfo.get(0).getGenre();

            StringBuilder sb = new StringBuilder();
            int counter;
            for (counter=0;counter < genres.size();counter++) {
                sb.append(genres.get(counter));
                if(genres.size()-1> counter){
                    sb.append(",");
                }
                //counter++;
            }
            String genree =sb.toString();
            genr.setText(genree);

        }catch (JSONException e){
            Log.e("JSON ERROR:",e.toString());
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

            adapter = new packagelist_adapter(playlist_models,this,R.layout.playlist_view);

            listview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
