package ml.dvnlabs.animize.fragment.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.metagenre_model;
import ml.dvnlabs.animize.pager.multitab_adapter;
import ml.dvnlabs.animize.recyclerview.staggered.metagenre_adapter;
import ml.dvnlabs.animize.recyclerview.staggered.metagenre_holder;


public class genre extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private TabLayout tabLayout;
    private ViewPager pager;
    private ArrayList<metagenre_model>metagenre_models;
    private LinearLayout loading;
    private ExpandableLayout expand_meta;

    private RecyclerView rv_meta;
    private metagenre_adapter metagenre_adapter;
    private StaggeredGridLayoutManager staggeredLayout;

    private ImageView btn_expand;
    private AdView mAdView;

    public genre(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_genre,container,false);
        tabLayout = view.findViewById(R.id.genre_tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager = view.findViewById(R.id.genre_viewpager);
        loading = view.findViewById(R.id.genre_title_load);
        rv_meta = view.findViewById(R.id.genre_rv_meta_staggered);
        expand_meta = view.findViewById(R.id.genre_meta_container);
        btn_expand = view.findViewById(R.id.genre_tabshow);
        /*Runnable runnableAdView = new Runnable() {
            @Override
            public void run() {
                ads_starter(view);
            }
        };*/
        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand_meta.toggle();
                float deg = (btn_expand.getRotation() == 180F) ? 0F : 180F;
                btn_expand.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });
        /*new Handler().postDelayed(runnableAdView,3000);*/
        getpagetitle();
        return view;
    }
    public void gotopagers(int page){
        pager.setCurrentItem(page,true);
        expand_meta.toggle();
        float deg = (btn_expand.getRotation() == 180F) ? 0F : 180F;
        btn_expand.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());

    }
    /*private void ads_starter(View view){
        AppController.initialize_ads(getActivity());
        mAdView = view.findViewById(R.id.genre_adView);
        if (AppController.isDebug(getActivity())){
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("48D9BD5E389E13283355412BC6A229A2").build();
            mAdView.loadAd(adRequest);
        }else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        //IF TESTING PLEASE UNCOMMENT testmode


    }*/
    private void getpagetitle(){
        metagenre_models= new ArrayList<>();
        String url = Api.url_genremeta;
        //Log.e("URL GENRE:",url);
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(getContext(),fetchgenre,url,CODE_GET_REQUEST,null);
    }

    private void initializetab(){
        Log.e("INITIALIZE","CHECK!");
        tabLayout.setupWithViewPager(pager);
        //num is number of tabs,pagetitle is List<>;
        multitab_adapter adapter = new multitab_adapter(getChildFragmentManager(),metagenre_models.size(),metagenre_models);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        staggeredLayout = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL);
        metagenre_adapter = new metagenre_adapter(metagenre_models,getActivity(),R.layout.rv_staggered,gotopage_genre);
        rv_meta.setLayoutManager(staggeredLayout);
        rv_meta.setAdapter(metagenre_adapter);
    }

    public static genre newInstance(){
        return new genre();
    }


    private void setTabTitle(JSONArray titles){
        try {
            for (int i = 0;i<titles.length();i++){
                JSONObject object = titles.getJSONObject(i);
                String title = object.getString("name_genre");
                String count = object.getString("sum_genre");
                metagenre_models.add(new metagenre_model(title,count));
            }
            initializetab();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    metagenre_holder.gotopage_genre gotopage_genre = new metagenre_holder.gotopage_genre() {
        @Override
        public void gotopager(int page) {
            gotopagers(page);
        }
    };

    FetchDataListener fetchgenre = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            loading.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    setTabTitle(object.getJSONArray("genre"));
                }
            }catch (JSONException e){
                Log.e("ERROR:",e.toString());
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            loading.setVisibility(View.GONE);
            Log.e("ERROR:",msg);

        }

        @Override
        public void onFetchStart() {
            loading.setVisibility(View.VISIBLE);

        }
    };
}
