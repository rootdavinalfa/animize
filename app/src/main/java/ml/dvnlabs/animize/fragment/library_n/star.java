package ml.dvnlabs.animize.fragment.library_n;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.database.PackageStarDBHelper;
import ml.dvnlabs.animize.database.model.starland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.starmodel;
import ml.dvnlabs.animize.recyclerview.packagelist.starlist_adapter;
import ml.dvnlabs.animize.view.AutoGridLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class star extends Fragment {

    private PackageStarDBHelper packageStarDBHelper;
    private ArrayList<starmodel> starmodels;
    private ArrayList<starland> indexstarred;
    private int counter =0;
    private RecyclerView rv_starred;
    private starlist_adapter adapter;
    private AutoGridLayoutManager LayoutManager;
    private RelativeLayout loading,voided;
    private SwipeRefreshLayout refreshLayout;
    private String tempid;
    private AdView mAdView;



    public star() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_star, container, false);
        rv_starred = view.findViewById(R.id.star_list);
        voided = view.findViewById(R.id.starred_void);
        loading = view.findViewById(R.id.starred_loading);
        refreshLayout = view.findViewById(R.id.starred_refresh);
        packageStarDBHelper = new PackageStarDBHelper(getActivity());
        if (packageStarDBHelper.isAvail()){
            voided.setVisibility(View.GONE);
            getStarredPackage getStarredPackage = new getStarredPackage();
            getStarredPackage.execute();
        }else {
            loading.setVisibility(View.GONE);
            voided.setVisibility(View.VISIBLE);
        }
        swipe_refresh();
        Runnable runnableAdView = new Runnable() {
            @Override
            public void run() {
                ads_starter(view);
            }
        };
        new Handler().postDelayed(runnableAdView,2000);
        swipe_refresh();
        return view;
    }

    private void ads_starter(View view){
        AppController.initialize_ads(getActivity());
        mAdView = view.findViewById(R.id.adView_star);
        //IF TESTING PLEASE UNCOMMENT testmode
        if (AppController.isDebug(getActivity())){
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("48D9BD5E389E13283355412BC6A229A2").build();
            mAdView.loadAd(adRequest);
        }else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

    }
    private void swipe_refresh(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (packageStarDBHelper.isAvail()){
                    voided.setVisibility(View.GONE);
                    starmodels.clear();
                    getStarredPackage getStarredPackage = new getStarredPackage();
                    getStarredPackage.execute();
                }else {
                    starmodels.clear();
                    loading.setVisibility(View.GONE);
                    voided.setVisibility(View.VISIBLE);
                }
                // To keep animateView for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animateView (This will be after 3 seconds)
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000); // Delay in millis
            }
        });

        refreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    private void addToArrayPackage(JSONArray pack){
        try {
            for (int i=0;i<pack.length();i++){
                JSONObject object = pack.getJSONObject(i);
                String packages = object.getString("package_anim");
                String nameanim = object.getString("name_catalogue");
                String totep = object.getString("total_ep_anim");
                String rate = object.getString("rating");
                String mal = object.getString("mal_id");
                String cover = object.getString("cover");
                for (int j=0;j<indexstarred.size();j++){
                    if (indexstarred.get(j).getPackageid().equals(packages)){
                        //System.out.println("Setting:"+j);
                        starmodels.set(j,new starmodel(packages,nameanim,totep,rate,mal,cover));
                    }
                }


            }
            /*Log.e("isNUlled:",String.valueOf(isAllNull(starmodels)));*/
            if (!isAllNull(starmodels)){
                /*Log.e("SIZE starmodels:",String.valueOf(starmodels.size()));
                for (int k = 0;k<starmodels.size();k++){
                    Log.e("COVER",starmodels.get(k).getCover());
                }*/
                loading.setVisibility(View.GONE);
                LayoutManager = new AutoGridLayoutManager(getContext(),500);
                adapter = new starlist_adapter(starmodels,getActivity(),R.layout.rv_starredpackage);
                rv_starred.setLayoutManager(LayoutManager);
                rv_starred.setAdapter(adapter);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static boolean isAllNull(ArrayList<starmodel> list){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getName() != null){
                if (i == list.size()-1){
                    return false;
                }
            }
        }
        return true;
    }
    private void getRestPackage(String id){
        this.tempid = id;
        String url = Api.url_packageinfo+id;
        Log.e("REQ: ",url);
        APINetworkRequest api = new APINetworkRequest(getActivity(),fetchPackage,url,1024,null);
    }

    FetchDataListener fetchPackage = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try {
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")){
                    addToArrayPackage(object.getJSONArray("anim"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            getRestPackage(tempid);
        }

        @Override
        public void onFetchStart() {
            voided.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);

        }
    };

    private class getStarredPackage extends AsyncTask<String,Void, ArrayList<starland>>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected ArrayList<starland> doInBackground(String... params){

            return packageStarDBHelper.getStarredList();

        }

        @Override
        protected void onPostExecute(ArrayList<starland> pa){
            counter = pa.size();
            System.out.println("COUNTER: "+counter);
            starmodels = new ArrayList<>();
            indexstarred = pa;
            for (int i =0;i<pa.size();i++){
                starmodels.add(new starmodel(null,null,null,null,null,null));
                getRestPackage(pa.get(i).getPackageid());
            }

        }

    }
}
