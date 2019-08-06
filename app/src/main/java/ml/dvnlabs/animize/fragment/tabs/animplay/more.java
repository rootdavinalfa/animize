package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.recyclerview.list.playlist_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.playlist_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class more extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private ImageView sourceid,sourceen,sourceraw;
    private Context context;
    private ArrayList<playlist_model> playlist_models;
    playlist_adapter adapter;
    private RecyclerView listview;
    String pkganim,id_anim;
    public more() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.ap_fragment_more_tabs, container, false);
        listview = view.findViewById(R.id.playlist_list);
        sourceen = view.findViewById(R.id.animplay_btn_sourceEN);
        sourceid = view.findViewById(R.id.animplay_btn_sourceID);
        sourceraw = view.findViewById(R.id.animplay_btn_sourceRAW);
        initial();
        return view;
    }
    private void initial(){
        sourceen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceselector("EN");
            }
        });
        sourceid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceselector("ID");
            }
        });
        sourceraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceselector("RAW");
            }
        });
    }

    private void sourceselector(String lang){
        ((animplay_activity)context).showsourceselector(lang,id_anim);

    }
    private void getplaylist(){
        String url = Api.url_playlist_play+pkganim;
        APINetworkRequest networkRequest = new APINetworkRequest(getActivity(),getplaylist,url,CODE_GET_REQUEST,null);

    }


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
        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {

        }
    };

    private void parseplaylist(JSONArray playlist){
        try {
            playlist_models = new ArrayList<>();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
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

            adapter = new playlist_adapter(playlist_models,getActivity(),R.layout.playlist_view);

            listview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void receivedata(String pkg,String anim){
        pkganim = pkg;
        id_anim = anim;
        getplaylist();
    }

}
