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
import android.widget.TextView;

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
import ml.dvnlabs.animize.ui.activity.StreamActivity;
import ml.dvnlabs.animize.ui.recyclerview.comment.commentThread_adapter;

public class threadComment extends Fragment {

    private TextView commentuser,content;
    private ArrayList<commentMainModel>models,repliedmodel;
    private ImageView closebutton,addsreply;
    private commentThread_adapter adapter;
    private RecyclerView listreply;
    private EditText replybox;

    private SharedPreferences pref;

    private String id_anim,parentcomment,id_user,token;


    private threadComment(ArrayList<commentMainModel> models,String idanim){
        this.models = models;
        this.id_anim = idanim;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_comment_thread,container,false);
        commentuser = view.findViewById(R.id.comments_thread_users);
        content = view.findViewById(R.id.comments_thread_content);
        closebutton = view.findViewById(R.id.comments_thread_close);
        listreply = view.findViewById(R.id.comments_thread_replied);
        addsreply = view.findViewById(R.id.comments_thread_adds);
        addsreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addreply();
            }
        });
        replybox = view.findViewById(R.id.comments_thread_edittext);
        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StreamActivity) requireActivity()).closeReplyFragment();
            }
        });
        receivedata();
        commenteditlistener();
        return view;
    }

    public static threadComment newInstance(ArrayList<commentMainModel> models,String idanim){
        return new threadComment(models,idanim);
    }

    //Listener for commentedit
    private void commenteditlistener(){
        addsreply.setVisibility(View.GONE);
        replybox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //addsbtn.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(replybox.getText().length()>0){
                    addsreply.setVisibility(View.VISIBLE);

                }
                else {
                    addsreply.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //addsbtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void receivedata(){
        if (models !=null){
            ArrayList<api_usermodel>users = models.get(0).getUsermodels();
            String name = users.get(0).getName_user();
            String contents = models.get(0).getContents();
            System.out.println(name+contents);
            commentuser.setText(name);
            content.setText(contents);
            parentcomment = models.get(0).getIdcomment();
            requestreply();
        }
    }

    private void requestreply(){
        String url = Api.url_commentthread+id_anim+"/parent/"+models.get(0).getIdcomment();
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),fetchReply,url,1024,null);
    }

    private void setFetchReply(JSONArray reply){
        try {
            repliedmodel = new ArrayList<>();
            for (int i=0;i<reply.length();i++){
                JSONObject object = reply.getJSONObject(i);
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
                repliedmodel.add(new commentMainModel(ids,status,content,usermodels));
            }
            adapter = new commentThread_adapter(repliedmodel,getContext(),R.layout.rv_comments_thread);
            LinearLayoutManager lnm = new LinearLayoutManager(getContext());
            listreply.setLayoutManager(lnm);
            listreply.setAdapter(adapter);

            //GET preferences
            token = pref.getString("token",null);
            id_user = pref.getString("idUser",null);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    FetchDataListener fetchReply = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try {
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")){
                    JSONArray comment = object.getJSONArray("comment");//Get array comment
                    JSONObject commentobj = comment.getJSONObject(0);//Get object on array 0
                    setFetchReply(commentobj.getJSONArray("reply"));
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

        }
    };

    private void addreply(){
        String url = Api.url_commentadd;
        String content =  replybox.getText().toString();
        HashMap<String,String> params = new HashMap<>();
        params.put("id_user",id_user);
        params.put("content",content);
        params.put("id_anim",id_anim);
        params.put("token",token);
        params.put("parent",parentcomment);
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),addreply,url,1025,params);
    }

    FetchDataListener addreply = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            replybox.getText().clear();

            //replybox.setEnabled(true);

            try {
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")){
                    requestreply();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("ERROR",msg);
        }

        @Override
        public void onFetchStart() {

            //replybox.setEnabled(false);

        }
    };



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pref = context.getSharedPreferences("aPlay",0);

    }
}
