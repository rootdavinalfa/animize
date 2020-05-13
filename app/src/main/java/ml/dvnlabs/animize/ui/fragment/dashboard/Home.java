/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.BannerListMdl;
import ml.dvnlabs.animize.model.HomeLastUploadModel;
import ml.dvnlabs.animize.model.PackageList;
import ml.dvnlabs.animize.ui.activity.DashboardActivity;
import ml.dvnlabs.animize.ui.recyclerview.banner.banner_adapter;
import ml.dvnlabs.animize.ui.recyclerview.list.home_lastup_adapter;
import ml.dvnlabs.animize.ui.recyclerview.packagelist.lastpackage_adapter;

public class Home extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private int page_lastup = 0;
    private ShimmerFrameLayout lastup_loading,package_loading,banner_loading;
    private RelativeLayout dash_button_lastupmore;
    private DiscreteScrollView listView_lastup,rv_bannerlist;
    private DiscreteScrollView rv_lastpackage;
    private ArrayList<HomeLastUploadModel> modeldata_lastup;
    private ArrayList<PackageList> modeldatapackage;
    private ArrayList<BannerListMdl> bannerlist_models;


    private Handler banner_scrolling;
    private Runnable banner_runnable;

    private home_lastup_adapter adapater_lastup;
    private lastpackage_adapter adapterlastpackage;
    private banner_adapter adapter_banner;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refresh_home;
    private FrameLayout adContainer,adcontainer1;
    private Context mContext;

    public Home() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container, false);
        listView_lastup = view.findViewById(R.id.lastup_list);
        //episodean = (TextView)view.findViewById(R.id.episodes_lastupload_home);
        //namestitle = (TextView)view.findViewById(R.id.titles_lastupload_home);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        dash_button_lastupmore = view.findViewById(R.id.dash_lastup_more);
        lastup_loading = view.findViewById(R.id.loading_lastup);
        banner_loading = view.findViewById(R.id.shimmer_banner);
        package_loading = view.findViewById(R.id.shimmer_package);
        rv_lastpackage = view.findViewById(R.id.rv_lastpackage);
        rv_bannerlist = view.findViewById(R.id.rv_banner);
        refresh_home = view.findViewById(R.id.dash_refresh_home);

        initial_setup();
        swipe_refresh();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    private void swipe_refresh(){
        refresh_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBanner();
                getLast_Up();
                getLastPackage();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animateView (This will be after 3 seconds)
                        refresh_home.setRefreshing(false);
                    }
                }, 2000); // Delay in millis
            }
        });
        refresh_home.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }
    private void initial_setup(){
        modeldata_lastup = new ArrayList<>();
        modeldatapackage = new ArrayList<>();
        bannerlist_models = new ArrayList<>();
        ACT_Dash_button_lastupmore();
        getBanner();
        getLast_Up();
        getLastPackage();


    }
    private void ACT_Dash_button_lastupmore(){
        dash_button_lastupmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).display_lastup();
            }
        });
    }

    public void getBanner(){
        String url = Api.url_banner;
        new APINetworkRequest(getActivity(),bannerlist,url,CODE_GET_REQUEST,null);
    }

    public void getLast_Up(){
        String url = Api.url_page+"1";
        new APINetworkRequest(getActivity(),lastup,url,CODE_GET_REQUEST,null);
    }
    public void getLastPackage(){
        String url = Api.url_packagepage+"1";
        new APINetworkRequest(getActivity(),lastpackage,url,CODE_GET_REQUEST,null);
    }

    private void parse_jsonlastup(String data){
        try{
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                show_video(object.getJSONArray("anim"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    FetchDataListener bannerlist = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            rv_bannerlist.setVisibility(View.VISIBLE);
            banner_loading.stopShimmer();
            banner_loading.setVisibility(View.GONE);
            try{
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    banner_list(object.getJSONArray("banner"));
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            banner_loading.stopShimmer();
            banner_loading.startShimmer();
            banner_loading.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFetchStart() {
            rv_bannerlist.setVisibility(View.GONE);
            banner_loading.startShimmer();
            banner_loading.setVisibility(View.VISIBLE);

        }
    };

    FetchDataListener lastup = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            listView_lastup.setVisibility(View.VISIBLE);
            lastup_loading.stopShimmer();
            lastup_loading.setVisibility(View.GONE);
            parse_jsonlastup(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            lastup_loading.startShimmer();
            lastup_loading.setVisibility(View.VISIBLE);
            Log.e("ERROR",msg);
        }

        @Override
        public void onFetchStart() {
            lastup_loading.startShimmer();
            lastup_loading.setVisibility(View.VISIBLE);
            listView_lastup.setVisibility(View.GONE);

        }
    };

    FetchDataListener lastpackage = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            rv_lastpackage.setVisibility(View.VISIBLE);
            package_loading.stopShimmer();
            package_loading.setVisibility(View.GONE);
            parseJSONPackage(data);

        }

        @Override
        public void onFetchFailure(String msg) {
            package_loading.startShimmer();
            package_loading.setVisibility(View.VISIBLE);

        }

        @Override
        public void onFetchStart() {
            package_loading.startShimmer();
            package_loading.setVisibility(View.VISIBLE);
            rv_lastpackage.setVisibility(View.GONE);

        }
    };

    private void parseJSONPackage(String data){
        try {
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                package_list(object.getJSONArray("anim"));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void banner_list(JSONArray banner){
        try {
            bannerlist_models.clear();
            for (int i =0;i<banner.length();i++){
                JSONObject object = banner.getJSONObject(i);
                String banner_image = object.getString("banner_image");
                String banner_url = object.getString("banner_url");
                String banner_title = object.getString("banner_title");
                bannerlist_models.add(new BannerListMdl(banner_image,banner_title,banner_url));
            }
            adapter_banner = new banner_adapter(bannerlist_models,getActivity(),R.layout.rv_banner);
            rv_bannerlist.setAdapter(adapter_banner);

            //System.out.println("Bnnaer count:"+bannerlist_models.size());
            banner_scrolling = new Handler();
            banner_runnable = new Runnable() {
                int currentBanner;
                @Override
                public void run() {
                    currentBanner = rv_bannerlist.getCurrentItem();
                    //System.out.println("Old:"+currentBanner+" To New:"+(currentBanner+1));
                    if(adapter_banner.getItemCount() != 0){
                        if (bannerlist_models.size() - 1 > currentBanner){
                            rv_bannerlist.smoothScrollToPosition(currentBanner + 1);
                            rv_bannerlist.setItemTransitionTimeMillis(250);
                        }else {
                            rv_bannerlist.smoothScrollToPosition(0);
                            rv_bannerlist.setItemTransitionTimeMillis(250);
                        }
                    }
                    banner_scrolling.postDelayed(banner_runnable,5000);
                }
            };

            banner_scrolling.postDelayed(banner_runnable,5000);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    private void package_list(JSONArray anim){
        try {
            modeldatapackage.clear();
            for (int i= 0;i<anim.length();i++){
                JSONObject object = anim.getJSONObject(i);
                String packages = object.getString("package_anim");
                String nameanim = object.getString("name_anim");
                String nowep = object.getString("now_ep_anim");
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
                modeldatapackage.add(new PackageList(packages,nameanim,nowep,totep,rate,mal,genres,cover));
            }
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

            adapterlastpackage = new lastpackage_adapter(modeldatapackage,getActivity(),R.layout.rv_newanime);
            //rv_lastpackage.setLayoutManager(linearLayoutManager);
            rv_lastpackage.setAdapter(adapterlastpackage);
            /*rv_lastpackage.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());*/

        }catch (JSONException e){
            Log.e("JSON ERROR:",e.toString());
        }

    }

    private void show_video(JSONArray video){
        try{
            modeldata_lastup.clear();
            //listView_lastup.setLayoutManager(linearLayoutManager);
            listView_lastup.setVisibility(View.VISIBLE);
            for(int i = 0;i<video.length();i++){
                JSONObject jsonObject = video.getJSONObject(i);
                String url_tb = jsonObject.getString(Api.JSON_episode_thumb);
                String id = jsonObject.getString(Api.JSON_id_anim);
                String title_name = jsonObject.getString(Api.JSON_name_anim);
                String episode = jsonObject.getString(Api.JSON_episode_anim);

                modeldata_lastup.add(new HomeLastUploadModel(url_tb,id,title_name,episode));
                //adapter.notifyItemInserted(i);

                //adapter.notifyDataSetChanged();

            }
            adapater_lastup = new home_lastup_adapter(modeldata_lastup,getActivity(),R.layout.rv_newepisode);
            listView_lastup.setAdapter(adapater_lastup);
            listView_lastup.setSlideOnFling(true);
            page_lastup = listView_lastup.getCurrentItem();


        }catch (JSONException e){
            Log.e("ERROR JSON:",e.toString());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        if(!modeldata_lastup.isEmpty()){
            modeldata_lastup.clear();
        }
        super.onDestroy();
    }


    public static Home newInstance() {
        return new Home();

    }
}