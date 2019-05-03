package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.recyclerview.playlist_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.playlist_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class more extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private ArrayList<playlist_model> playlist_models;
    playlist_adapter adapter;
    private RecyclerView listview;
    String pkganim;
    public more() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.ap_fragment_more_tabs, container, false);
        listview = view.findViewById(R.id.playlist_list);
        initial();
        return view;
    }
    private void initial(){


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
    public void receivedata(String pkg){
        pkganim = pkg;
        getplaylist();
    }

}
