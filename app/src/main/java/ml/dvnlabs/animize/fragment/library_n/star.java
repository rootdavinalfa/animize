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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
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
    private int counter =0;
    private RecyclerView rv_starred;
    private starlist_adapter adapter;
    private AutoGridLayoutManager LayoutManager;
    private RelativeLayout loading,voided;
    private SwipeRefreshLayout refreshLayout;
    private String tempid;



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
        return view;
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
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
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
                starmodels.add(new starmodel(packages,nameanim,totep,rate,mal));
            }
            if (counter == starmodels.size()){
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
            for (int i =0;i<pa.size();i++){
                getRestPackage(pa.get(i).getPackageid());
            }

        }

    }
}
