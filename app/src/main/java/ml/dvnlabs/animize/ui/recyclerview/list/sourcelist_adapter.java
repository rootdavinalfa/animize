/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.SourceList;

public class sourcelist_adapter extends RecyclerView.Adapter<sourcelist_holder> {
    private Context mcontext;
    private ArrayList<SourceList> sources;
    private int itemResor;

    public sourcelist_adapter(ArrayList<SourceList>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.sources = data;

    }
    @Override
    public sourcelist_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new sourcelist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(sourcelist_holder holder,int position){
        SourceList slm = this.sources.get(position);
        holder.bind_playlist(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(sources == null){
            return 0;
        }else{
            return sources.size();
        }

    }

}
