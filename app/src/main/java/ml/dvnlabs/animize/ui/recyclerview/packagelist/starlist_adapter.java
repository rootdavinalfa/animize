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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.StarLand;
import ml.dvnlabs.animize.model.StarredModel;
import ml.dvnlabs.animize.ui.recyclerview.interfaces.addingQueue;

public class starlist_adapter extends RecyclerView.Adapter<StarlistHolder> implements addingQueue {
    private Context mcontext;
    public static ArrayList<StarLand> packagelists;
    private ArrayList<requestQueue> queue;
    private int itemResor;

    //
    public static ArrayList<readyStar> readyStars = new ArrayList<>();

    public starlist_adapter(ArrayList<StarLand>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        packagelists = data;
        readyStars.clear();
        queue = new ArrayList<>();

    }
    @NotNull
    @Override
    public StarlistHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(mcontext).inflate(this.itemResor,parent,false);
        return new StarlistHolder(this.mcontext,view,this);

    }

    @Override
    public void onBindViewHolder(StarlistHolder holder,int position){
        holder.setIsRecyclable(false);
        //System.out.println("CDATA:POS:"+position+":PKG:"+this.packagelists.get(position).getPackageid());
        StarLand slm = packagelists.get(holder.getAdapterPosition());
        holder.bindPlaylist(slm);
    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(packagelists == null){
            return 0;
        }else{
            return packagelists.size();
        }
    }
    //Callback for adding / remove queue
    @Override
    public void addQueue(String pkganim, int position) {
        queue.add(new requestQueue(pkganim,position));
    }

    @Override
    public void removeQueue(int position) {
        for (int i=0;i<queue.size();i++){
            if (queue.get(i).pos == position){
                queue.remove(i);
            }
        }
    }

    @Override
    public ArrayList<requestQueue> getQueue() {
        return queue;
    }

    //Model for request queue
    public class requestQueue{
        String pkg;
        int pos;
        requestQueue(String pkganim,int position){
            this.pkg = pkganim;
            this.pos = position;
        }

        public int getPos() {
            return pos;
        }

        public String getPkg() {
            return pkg;
        }
    }

    public static class readyStar{
        StarredModel model;
        int pos;
        readyStar(StarredModel models, int poss){
            this.model = models;
            this.pos = poss;
        }

        public int getPos() {
            return pos;
        }

        public StarredModel getModel() {
            return model;
        }
    }

}
