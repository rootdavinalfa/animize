package ml.dvnlabs.animize.fragment.library_n;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.RecentPlayDBHelper;
import ml.dvnlabs.animize.database.model.recentland;
import ml.dvnlabs.animize.recyclerview.packagelist.recentlist_adapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class recent extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv_list;
    private RelativeLayout voided_layout;

    private recentlist_adapter adapter;
    private RecentPlayDBHelper recentPlayDBHelper;


    public recent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_recent, container, false);
        recentPlayDBHelper = new RecentPlayDBHelper(getActivity());
        refreshLayout = view.findViewById(R.id.recent_refresh);
        rv_list = view.findViewById(R.id.recent_list);
        voided_layout = view.findViewById(R.id.recent_void);

        if (recentPlayDBHelper.isRecentAvailLis()){
            voided_layout.setVisibility(View.GONE);
            readlist_recent recent = new readlist_recent();
            recent.execute("");
        }
        else{
            voided_layout.setVisibility(View.VISIBLE);
        }
        swipe_refresh();

        return view;
    }
    private void refreshing_list(){
        if (recentPlayDBHelper.isRecentAvailLis()){
            voided_layout.setVisibility(View.GONE);
            readlist_recent recent = new readlist_recent();
            recent.execute("");
        }
        else{
            voided_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshing_list();
    }

    private void swipe_refresh(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing_list();
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

    public class readlist_recent extends AsyncTask<String,Void, ArrayList<recentland>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<recentland> doInBackground(String... strings) {
            return recentPlayDBHelper.getrecentlist();
        }

        @Override
        protected void onPostExecute(ArrayList<recentland> recentlands) {
            super.onPostExecute(recentlands);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            adapter = new recentlist_adapter(recentlands,getActivity(),R.layout.rv_recentview);
            rv_list.setLayoutManager(layoutManager);
            rv_list.setAdapter(adapter);
        }
    }

}
