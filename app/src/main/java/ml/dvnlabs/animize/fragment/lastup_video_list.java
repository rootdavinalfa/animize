package ml.dvnlabs.animize.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.adapter.EndlessRecyclerOnScrollListener;
import ml.dvnlabs.animize.adapter.video_list_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.video_list_model;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class lastup_video_list extends Fragment implements View.OnClickListener {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private Parcelable recyclerViewState;
    RecyclerView listView;
    ArrayList<video_list_model> modeldata;
    video_list_adapter adapter;
    AVLoadingIndicatorView progressBar;
    TextView textload;
    TextView texterrorload;
    Button btn_retry;
    ImageView iv_error;
    SwipeRefreshLayout swipe_list;
    private int page_list = 1;
    private Handler handler;
    private boolean isRefreshing = false;
    private LinearLayoutManager layoutManager;


    public lastup_video_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new Handler();
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_lastup_list, container, false);
        listView = (RecyclerView) view.findViewById(R.id.PlayVideoList);


        swipe_list = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        btn_retry = (Button) view.findViewById(R.id.loading_retry);
        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.progressBar);
        textload = (TextView) view.findViewById(R.id.loading_text);
        texterrorload = (TextView) view.findViewById(R.id.loading_errortxt);
        iv_error = (ImageView) view.findViewById(R.id.error_image);
        iv_error.setVisibility(View.GONE);
        textload.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        texterrorload.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
        btn_retry.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());

        String a = "INIT";
        Log.e("INF",a);
        //Call initialize like init array()
        initialize();
        getlist_V();


        swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                //listView.setVisibility(View.GONE);
                //adapter.notifyDataSetChanged();
                modeldata.clear();
                page_list =1;
                getlist_V();
                //getLoaderManager().restartLoader(0,null,lastup_video_list.this);
                btn_retry.setVisibility(View.GONE);
                texterrorload.setVisibility(View.GONE);
                iv_error.setVisibility(View.GONE);
                //getJson();
                // Your code here

                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipe_list.setRefreshing(false);
                        page_list = page_list+1;
                    }
                }, 2000); // Delay in millis
            }
        });

        swipe_list.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );


        return view;
    }
    private void initialize(){
        modeldata = new ArrayList<>();

    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        //getLoaderManager().initLoader(0,null,lastup_video_list.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!modeldata.isEmpty()){
            modeldata.clear();
        }

    }

    @Override
    public void onClick(View view){
        retry_load();
    }
    public static lastup_video_list newInstance(){
        return new lastup_video_list();

    }
    //VOLLEY NETWORKING
    private void getlist_V(){
        try{
            System.out.println("NOW PAGE::"+String.valueOf(page_list));

            String url = Api.url_page+page_list;
            APINetworkRequest api = new APINetworkRequest(getActivity(),getvideolist,url,CODE_GET_REQUEST,null);
        }catch (Exception e){
            Log.e("ERROR",e.getMessage());
        }
    }
    private void jsonvideolist(String data){
        try {
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                show_video(object.getJSONArray("anim"));
                //adapter.notifyDataSetChanged();
                //Log.e("INFAA",String.valueOf(adapter.getItemCount()));
                progressBar.setVisibility(View.GONE);
                textload.setVisibility(View.GONE);
                iv_error.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
                texterrorload.setVisibility(View.GONE);
            }
            if (object.getBoolean("error") && object.getString("message").equals("page not found")){
                page_list=1;
                System.out.println("ALL DATA CAUGHT UP!");
                progressBar.setVisibility(View.GONE);
                textload.setVisibility(View.GONE);
                iv_error.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
                texterrorload.setVisibility(View.GONE);

            }
        }catch (JSONException e){
            Log.e("JSONE",e.getMessage());
        }
    }
    FetchDataListener getvideolist = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            jsonvideolist(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            iv_error.setVisibility(View.VISIBLE);
            texterrorload.setVisibility(View.VISIBLE);
            btn_retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            textload.setVisibility(View.GONE);
        }

        @Override
        public void onFetchStart() {
            progressBar.setVisibility(View.VISIBLE);
            textload.setVisibility(View.VISIBLE);
        }
    };

    public void retry_load(){
        //getJson();
        //listView.setVisibility(View.GONE);
        iv_error.setVisibility(View.GONE);
        texterrorload.setVisibility(View.GONE);
        getlist_V();
        //getLoaderManager().restartLoader(0,null,this);

    }
    private void show_video(JSONArray video){
        listView.setLayoutManager(layoutManager);
        listView.setVisibility(View.VISIBLE);

        //recyclerViewState = layoutManager.onSaveInstanceState();
        if(page_list ==1){
            modeldata.clear();
        }

        try{
            for(int i = 0;i<video.length();i++){
                JSONObject jsonObject = video.getJSONObject(i);
                String url_tb = jsonObject.getString(Api.JSON_episode_thumb);
                String id = jsonObject.getString(Api.JSON_id_anim);
                String title_name = jsonObject.getString(Api.JSON_name_anim);
                String episode = jsonObject.getString(Api.JSON_episode_anim);
                //System.out.println("DATA:"+id);
                //Log.e("INF::",title_name);
                modeldata.add(new video_list_model(url_tb,id,title_name,episode));
                //adapter.notifyItemInserted(i);

                //adapter.notifyDataSetChanged();

            }
            if(page_list == 1){
                adapter = new video_list_adapter(modeldata,getActivity(),listView);
                listView.setAdapter(adapter);
            }
            Log.e("INFO COUNT ADAPT:",String.valueOf(adapter.getItemCount()));
            if(page_list > 1){
                adapter.notifyItemChanged(0,adapter.getItemCount());


                listView.invalidate();
            }



        }catch (JSONException e) {
            e.printStackTrace();
        }


        //listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);


        if(!isRefreshing){
            onscrool();
            page_list = page_list+1;
        }
        if(isRefreshing){
            isRefreshing = false;
            page_list = 1;
            onscrool();
            System.out.println("REFRESHING");


        }
    }
    private void onscrool(){
        listView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                modeldata.add(new video_list_model(null,null,null,null));
                //adapter.notifyItemInserted(modeldata.size());
                adapter.notifyDataSetChanged();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        modeldata.remove(modeldata.size() - 1);
                        adapter.notifyItemRemoved(modeldata.size());

                        getlist_V();

                        //getLoaderManager().restartLoader(0,null,lastup_video_list.this);
                        //add items one by one
                        //adapter.notifyDataSetChanged();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 500);
                //System.out.println("load");
                //System.out.println("DATA SIZE::::"+String.valueOf(modeldata.size()));


            }
        });

    }
}