/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.bannerlist_model;

public class banner_adapter extends RecyclerView.Adapter<banner_holder> {
    private Context mcontext;
    private ArrayList<bannerlist_model> bannermodel;
    private int itemResor;

    public banner_adapter(ArrayList<bannerlist_model> data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.bannermodel = data;

    }
    @Override
    public void onBindViewHolder(@NonNull banner_holder holder, int position) {
        bannerlist_model slm = this.bannermodel.get(position);
        holder.bindBanner(slm);
    }

    @NonNull
    @Override
    public banner_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new banner_holder(this.mcontext,view);
    }

    @Override
    public int getItemCount() {
        if (bannermodel == null){
            return 0;
        }else {
            return bannermodel.size();
        }

    }
}
