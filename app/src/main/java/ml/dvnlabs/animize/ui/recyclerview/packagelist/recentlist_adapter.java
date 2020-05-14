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

import ml.dvnlabs.animize.database.model.RecentLand;

public class recentlist_adapter extends RecyclerView.Adapter<recentlist_holder> {
    private Context mcontext;
    private ArrayList<RecentLand> recentLands;
    private int itemResor;
    public recentlist_adapter(ArrayList<RecentLand> data, Context context, int itemResource){
        this.mcontext = context;
        this.recentLands = data;
        this.itemResor = itemResource;
    }
    @Override
    public recentlist_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new recentlist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(recentlist_holder holder,int position){
        RecentLand slm = this.recentLands.get(position);
        holder.bind_recent(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(recentLands == null){
            return 0;
        }else{
            return recentLands.size();
        }

    }
}
