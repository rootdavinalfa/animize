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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.home_lastup_model;

public class home_lastup_adapter extends RecyclerView.Adapter<home_lastup_holder>{

    private Context mcontext;
    private ArrayList<home_lastup_model> lastupdata;
    private int itemResor;
    private int pos;
    public home_lastup_adapter(ArrayList<home_lastup_model>data,Context context,int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.lastupdata = data;

    }


    @NonNull
    @Override
    public home_lastup_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new home_lastup_holder(this.mcontext,view);
    }

    @Override
    public void onBindViewHolder(@NonNull home_lastup_holder holder, int position) {
        pos = position;
        home_lastup_model slm = this.lastupdata.get(position);
        holder.bind_lastup(slm);
    }

    @Override
    public int getItemCount() {
        if(lastupdata == null){
            return 0;
        }else{
            return lastupdata.size();
        }
    }
    public int getPos(){
        return pos;
    }
}