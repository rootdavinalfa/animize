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

import java.util.List;

public class package_metagenre_adapter extends RecyclerView.Adapter<package_metagenre_holder> {
    private List<String> data;
    private Context mcontext;
    private int itemResor;
    public package_metagenre_adapter(List<String> data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.data = data;

    }
    @Override
    public package_metagenre_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new package_metagenre_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(package_metagenre_holder holder,int position){
        holder.bind_data(data.get(position));

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
