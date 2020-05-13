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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.SearchListPackageModel;

public class search_package_adapter extends RecyclerView.Adapter<search_package_holder> {
    private Context context;
    private ArrayList<SearchListPackageModel> data;
    private int itemRes;
    public search_package_adapter(ArrayList<SearchListPackageModel> models, Context context, int itemResor){
        this.data = models;
        this.context = context;
        this.itemRes = itemResor;
    }

    @Override
    public void onBindViewHolder(@NonNull search_package_holder holder, int position) {
        SearchListPackageModel model = this.data.get(holder.getAdapterPosition());
        holder.bind_search_package(model);

    }

    @NonNull
    @Override
    public search_package_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemRes,parent,false);
        return new search_package_holder(this.context,view);
    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }else {
            return  data.size();
        }
    }
}