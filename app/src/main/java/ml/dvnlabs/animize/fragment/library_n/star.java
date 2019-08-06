package ml.dvnlabs.animize.fragment.library_n;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.util.ArrayList;


import ml.dvnlabs.animize.R;

import ml.dvnlabs.animize.database.PackageStarDBHelper;
import ml.dvnlabs.animize.database.model.starland;

import ml.dvnlabs.animize.recyclerview.packagelist.starlist_adapter;
import ml.dvnlabs.animize.view.AutoGridLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class star extends Fragment {

    private PackageStarDBHelper packageStarDBHelper;
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
        /*loading = view.findViewById(R.id.starred_loading);*/
        refreshLayout = view.findViewById(R.id.starred_refresh);
        packageStarDBHelper = new PackageStarDBHelper(getActivity());
        refresh_list();
        swipe_refresh();

        return view;
    }
    private void refresh_list(){
        if (packageStarDBHelper.isAvail()){
            voided.setVisibility(View.GONE);
            getStarredPackage getStarredPackage = new getStarredPackage();
            getStarredPackage.execute();
        }else {
            /*loading.setVisibility(View.GONE);*/
            voided.setVisibility(View.VISIBLE);
        }
    }





    @Override
    public void onResume() {
        super.onResume();
    }

    private void swipe_refresh(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_list();
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
            if (!pa.isEmpty()){
                LayoutManager = new AutoGridLayoutManager(getContext(),500);
                adapter = new starlist_adapter(pa,getActivity(),R.layout.rv_starredpackage);
                rv_starred.setLayoutManager(LayoutManager);
                rv_starred.setHasFixedSize(true);
                rv_starred.setAdapter(adapter);
            }else{
                voided.setVisibility(View.VISIBLE);
            }
        }

    }
}
