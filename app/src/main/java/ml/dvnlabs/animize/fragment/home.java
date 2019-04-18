package ml.dvnlabs.animize.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.dashboard_activity;
import ml.dvnlabs.animize.adapter.home_lastup_adapter;
import ml.dvnlabs.animize.adapter.lastup_listener;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.home_lastup_model;

public class home extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private int page_lastup = 0;
    private ShimmerFrameLayout lastup_loading;
    private TextView dash_button_lastupmore;
    private DiscreteScrollView listView_lastup;
    private ArrayList<home_lastup_model> modeldata_lastup;
    private home_lastup_adapter adapater_lastup;
    private LinearLayoutManager linearLayoutManager;
    CountDownTimer cTimer = null;
    public home(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container, false);
        listView_lastup = (DiscreteScrollView) view.findViewById(R.id.lastup_list);
        //episodean = (TextView)view.findViewById(R.id.episodes_lastupload_home);
        //namestitle = (TextView)view.findViewById(R.id.titles_lastupload_home);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        dash_button_lastupmore = (TextView)view.findViewById(R.id.dash_lastup_more);
        lastup_loading = (ShimmerFrameLayout)view.findViewById(R.id.loading_lastup);
        initial_setup();

        return view;
    }
    private void initial_setup(){
        modeldata_lastup = new ArrayList<>();
        ACT_Dash_button_lastupmore();
        getLast_Up();
    }
    private void ACT_Dash_button_lastupmore(){
        dash_button_lastupmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((dashboard_activity)getActivity()).display_lastup();
            }
        });
    }
    public void getLast_Up(){
        String url = Api.url_page+"1";
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(getActivity(),lastup,url,CODE_GET_REQUEST,null);
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
    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                lastup_scrollto();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
    FetchDataListener lastup = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            lastup_loading.stopShimmer();
            lastup_loading.setVisibility(View.GONE);
            parse_jsonlastup(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("ERROR",msg);
        }

        @Override
        public void onFetchStart() {
            lastup_loading.startShimmer();
            listView_lastup.setVisibility(View.VISIBLE);

        }
    };

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

                modeldata_lastup.add(new home_lastup_model(url_tb,id,title_name,episode));
                //adapter.notifyItemInserted(i);

                //adapter.notifyDataSetChanged();

            }
            adapater_lastup = new home_lastup_adapter(modeldata_lastup,getActivity(),R.layout.rv_lastupload);
            listView_lastup.setAdapter(adapater_lastup);
            listView_lastup.setHasFixedSize(true);
            DiscreteScrollItemTransformer transformer = new DiscreteScrollItemTransformer() {
                private static final float MIN_SCALE = 0.75f;
                @Override
                public void transformItem(View item, float position) {
                    int pageWidth = item.getWidth();

                    if (position < -1) { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        item.setAlpha(0f);

                    } else if (position <= 0) { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        item.setAlpha(1f);
                        item.setTranslationX(0f);
                        item.setScaleX(1f);
                        item.setScaleY(1f);

                    } else if (position <= 1) { // (0,1]
                        // Fade the page out.
                        item.setAlpha(1 - position);

                        // Counteract the default slide transition
                        item.setTranslationX(pageWidth * -position);

                        // Scale the page down (between MIN_SCALE and 1)
                        float scaleFactor = MIN_SCALE
                                + (1 - MIN_SCALE) * (1 - Math.abs(position));
                        item.setScaleX(scaleFactor);
                        item.setScaleY(scaleFactor);

                    } else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        item.setAlpha(0f);
                    }

                }
            };
            listView_lastup.setItemTransformer(transformer);
            page_lastup = listView_lastup.getCurrentItem();
            startTimer();
            listView_lastup.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        page_lastup = listView_lastup.getCurrentItem();
                    }
                }
            });



        }catch (JSONException e){
            Log.e("ERROR JSON:",e.toString());
        }
    }
    private void lastup_scrollto(){
        System.out.println("PAGE:"+page_lastup);
        listView_lastup.smoothScrollToPosition(page_lastup);
        page_lastup++;
        if(page_lastup>modeldata_lastup.size()-1){
            page_lastup = 0;
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
        super.onDestroy();
        if(!modeldata_lastup.isEmpty()){
            modeldata_lastup.clear();
        }

        cancelTimer();
    }

    public static home newInstance(){
        return new home();

    }
}
