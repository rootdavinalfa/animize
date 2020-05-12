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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.video_list_model;
import ml.dvnlabs.animize.ui.recyclerview.interfaces.OnLoadMoreListener;


public class video_list_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mcontext;
    private ArrayList<video_list_model> video_data;
    //private int itemResor;
    private RecyclerView itemResor;
    private OnLoadMoreListener listener;
    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public video_list_adapter(ArrayList<video_list_model>data,Context context,RecyclerView itemResource){
        //super(context, R.layout.video_list_view,data);
        this.video_data = data;
        this.mcontext = context;
        this.itemResor = itemResource;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) itemResource.getLayoutManager();
        itemResource.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (listener != null) {
                        listener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        int viewtype;
        if(video_data.get(position).getIdn() == null){
            //log.e("LOADINGggg:","OK");

            viewtype = VIEW_TYPE_LOADING;
        }
        else {
            //log.e("LOADINGggg:","OK");
            viewtype = VIEW_TYPE_ITEM;
        }

       return viewtype;

    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            //log.e("DATA:","OK");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_view, parent, false);
            return new video_list_holder(this.mcontext,view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            //log.e("LOADING:","OK");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_view_load, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                //log.e("DATSA:","OK");
                video_list_model slm = this.video_data.get(position);
                video_list_holder hold = (video_list_holder) holder;
                hold.bindvideo_list(slm);
                break;
            case 1:
                //log.e("LOADINGS:","OK");
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.frame.startShimmer();
                break;
        }
    }


    public void setOnloadMoreListener(OnLoadMoreListener listen){
        this.listener = listen;

    }
    public void setLoaded() {
        isLoading = false;
    }


    @Override
    public int getItemCount() {
        if(video_data == null){
            return 0;
        }else{
            return video_data.size();
        }
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        private ShimmerFrameLayout frame;
        private LoadingViewHolder(View view){
            super(view);
            frame = (ShimmerFrameLayout)view.findViewById(R.id.shimmer_lastup);
        }

    }
    /*
    @Override
    public video_list_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new video_list_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(video_list_holder holder,int position){
        video_list_model vlm = this.video_data.get(position);
        holder.bindvideo_list(vlm);

    }
    @Override
    public int getItemCount(){
        ////log.e("SIZE:",String.valueOf(video_data.size()));
        if(video_data == null){
            return 0;
        }else{
            return video_data.size();
        }

    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }*/


}
