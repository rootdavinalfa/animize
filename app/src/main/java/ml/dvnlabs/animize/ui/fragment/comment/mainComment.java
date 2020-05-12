/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.comment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.api_usermodel;
import ml.dvnlabs.animize.model.commentMainModel;
import ml.dvnlabs.animize.ui.recyclerview.comment.commentMain_adapter;

public class mainComment extends Fragment {

    private ArrayList<commentMainModel> commentMainModels;
    private RecyclerView commentar;
    private commentMain_adapter adapter;
    private LinearLayout contain;
    private RelativeLayout loads;
    private EditText commentedit;
    private ImageView addsbtn;


    private SharedPreferences pref;
    private String idanim,token,id_user;
    public mainComment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_comment,container,false);
        commentar = view.findViewById(R.id.aplay_fragment_comment_rv);
        contain = view.findViewById(R.id.aplay_fragment_comment_container);
        loads = view.findViewById(R.id.aplay_fragment_comment_loading);
        commentedit = view.findViewById(R.id.aplay_comments_edittext);
        addsbtn = view.findViewById(R.id.aplay_comments_adds);
        commentEditListener();
        addsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComments();
            }
        });
        contain.setVisibility(View.GONE);
        loads.setVisibility(View.VISIBLE);
        return view;
    }
    public static mainComment newInstance(){
        return new mainComment();
    }

    private void fetchingComment(){
        String url = Api.url_commentlist+idanim;
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),fetchcomment,url,1024,null);
    }
    public void receiveData(String id_anim){
        this.idanim = id_anim;
        fetchingComment();
    }

    //Listener for commentedit
    private void commentEditListener(){
        addsbtn.setVisibility(View.GONE);
        commentedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //addsbtn.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(commentedit.getText().length()>0){
                    addsbtn.setVisibility(View.VISIBLE);

                }
                else {
                    addsbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //addsbtn.setVisibility(View.VISIBLE);
            }
        });
    }

    //SHOW COMMENT AFTER GET String data from web
    private void showingComment(JSONArray comment){
        try {
            commentMainModels = new ArrayList<>();
            for (int i=0;i<comment.length();i++){
                JSONObject object = comment.getJSONObject(i);
                String ids = object.getString("id_comment");
                String status = object.getString("status");
                String content = object.getString("content");
                JSONArray user = object.getJSONArray("user");
                ArrayList<api_usermodel>usermodels = new ArrayList<>();
                for (int j=0;j<user.length();j++){
                    JSONObject jsonObject = user.getJSONObject(j);
                    String username = jsonObject.getString("username");
                    String nameus = jsonObject.getString("name_user");
                    usermodels.add(new api_usermodel(username,nameus));
                }
                commentMainModels.add(new commentMainModel(ids,status,content,usermodels));
            }
            adapter = new commentMain_adapter(commentMainModels,getContext(),R.layout.rv_comments);
            LinearLayoutManager lnm = new LinearLayoutManager(getContext());
            commentar.setLayoutManager(lnm);
            commentar.setAdapter(adapter);

            //Get token and iduser
            token = pref.getString("token",null);
            id_user = pref.getString("idUser",null);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    FetchDataListener fetchcomment = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            contain.setVisibility(View.VISIBLE);
            loads.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")){
                    showingComment(object.getJSONArray("comment"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            contain.setVisibility(View.VISIBLE);
            loads.setVisibility(View.GONE);
        }

        @Override
        public void onFetchStart() {


        }
    };

    private void addComments(){
        String url = Api.url_commentadd;
        String content =  commentedit.getText().toString();
        HashMap<String,String> params = new HashMap<>();
        params.put("id_user",id_user);
        params.put("content",content);
        params.put("id_anim",idanim);
        params.put("token",token);
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),addcomment,url,1025,params);
    }

    FetchDataListener addcomment = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            commentedit.getText().clear();

            //commentedit.setEnabled(true);

            try {
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    fetchingComment();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("ERROR:",msg);

        }

        @Override
        public void onFetchStart() {

            //commentedit.setEnabled(false);


        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pref = context.getSharedPreferences("aPlay",0);
    }
}
