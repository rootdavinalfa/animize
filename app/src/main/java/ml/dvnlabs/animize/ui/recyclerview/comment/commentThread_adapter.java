/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.CommentMainModel;

public class commentThread_adapter extends RecyclerView.Adapter<commentThread_holder> {

    private Context mcontext;
    private ArrayList<CommentMainModel> commentMainModels;
    private int itemResor;

    public commentThread_adapter(ArrayList<CommentMainModel> data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.commentMainModels = data;

    }
    @Override
    public void onBindViewHolder(@NonNull commentThread_holder holder, int position) {
        CommentMainModel slm = this.commentMainModels.get(position);
        holder.bindComment(slm);
    }

    @NonNull
    @Override
    public commentThread_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new commentThread_holder(this.mcontext,view);
    }

    @Override
    public int getItemCount() {
        if (commentMainModels == null){
            return 0;
        }else {
            return commentMainModels.size();
        }

    }

}
