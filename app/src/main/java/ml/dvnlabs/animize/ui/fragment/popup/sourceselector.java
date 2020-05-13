/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.SourceList;
import ml.dvnlabs.animize.ui.recyclerview.list.sourcelist_adapter;

public class sourceselector extends BottomSheetDialogFragment {
    private static final int CODE_GET_REQUEST = 1024;
    private ArrayList<SourceList> sourcess;
    private RecyclerView listview;
    private AVLoadingIndicatorView loading;
    private TextView errors;
    private RelativeLayout infocontainer;
    private sourcelist_adapter adapter;

    private Context mcontext;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sheet_source,container,false);
        listview = view.findViewById(R.id.sourceslist_list);
        loading = view.findViewById(R.id.sourceslist_loading);
        errors = view.findViewById(R.id.sourceslist_error);
        infocontainer = view.findViewById(R.id.sourceslist_infocontainer);
        listview.setVisibility(View.GONE);
        infocontainer.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public void dismissselector(){
        dismiss();
    }
    public void language(String lang, String idanim,Context context){
        this.mcontext = context;
        String url = Api.url_sourcelist+idanim+"/lang="+lang;
        System.out.println("URL SOURCES GET: "+url);
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),fetchsource,url,CODE_GET_REQUEST,null);
    }

    private FetchDataListener fetchsource = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try {
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    listview.setVisibility(View.VISIBLE);
                    infocontainer.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    errors.setVisibility(View.GONE);
                    m_fetchsource(object.getJSONArray("anim"));
                }else {
                    listview.setVisibility(View.GONE);
                    infocontainer.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    errors.setVisibility(View.VISIBLE);
                }
            }catch (JSONException e){
                Log.e("ERROR:",e.toString());
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("WEB ERROR:",msg);

        }

        @Override
        public void onFetchStart() {

        }
    };

    private void m_fetchsource(JSONArray source){
        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            sourcess = new ArrayList<>();
            for (int i=0;i<source.length();i++){
                JSONObject object = source.getJSONObject(i);
                String id = object.getString("id_source");
                String by_user = object.getString("by_user");
                String sources = object.getString("source");
                sourcess.add(new SourceList(id,by_user,sources));
            }
            adapter = new sourcelist_adapter(sourcess,mcontext,R.layout.rv_sources);
            listview.setLayoutManager(linearLayoutManager);
            listview.setAdapter(adapter);


        }catch (JSONException e){
            Log.e("ERROR",e.toString());
        }

    }
}
