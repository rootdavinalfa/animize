/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.staggered;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.MetaGenreModel;

public class metagenre_adapter extends RecyclerView.Adapter<metagenre_holder> {
    private ArrayList<MetaGenreModel> data;
    metagenre_holder.gotopage_genre calls;
    private Context mcontext;
    private int itemResor;
    public metagenre_adapter(ArrayList<MetaGenreModel> data, Context context, int itemResource, metagenre_holder.gotopage_genre call){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.data = data;
        this.calls = call;

    }
    @Override
    public metagenre_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new metagenre_holder(this.mcontext,view,calls);

    }

    @Override
    public void onBindViewHolder(metagenre_holder holder,int position){
        MetaGenreModel slm = this.data.get(position);
        holder.bind_data(slm,position);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(data == null){
            return 0;
        }else{
            return data.size();
        }

    }
}