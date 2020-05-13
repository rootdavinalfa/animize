/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.packagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.PlaylistModel;

public class packagelist_adapter extends RecyclerView.Adapter<packagelist_holder> {
    private Context mcontext;
    private ArrayList<PlaylistModel> playlistdata;
    private int itemResor;

    public packagelist_adapter(ArrayList<PlaylistModel>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.playlistdata = data;

    }
    @Override
    public packagelist_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new packagelist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(packagelist_holder holder,int position){
        PlaylistModel slm = this.playlistdata.get(position);
        holder.bind_playlist(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(playlistdata == null){
            return 0;
        }else{
            return playlistdata.size();
        }

    }

}
