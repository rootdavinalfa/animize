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
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.SearchListModel;
import ml.dvnlabs.animize.ui.activity.StreamActivity;


public class search_list_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final TextView title_nm;
    private final TextView ep_num;
    private final TextView idn;
    private final ImageView title_image;

    private SearchListModel vl_model;
    private Context context;
    public search_list_holder(Context context, View view){
        super(view);
        this.context = context;
        this.title_nm = (TextView) view.findViewById(R.id.srctitle_video_list);
        this.ep_num = (TextView)view.findViewById(R.id.srcepisode_view);
        this.idn = (TextView)view.findViewById(R.id.srcid_anime);
        this.title_image = (ImageView)view.findViewById(R.id.srctitle_image);

        itemView.setOnClickListener(this);
    }

    public void bindsearch_list(SearchListModel vlm){
        this.vl_model = vlm;
        //  Log.e("DATAAA:",vl_model.getTitle_nm());
        this.title_nm.setText(vl_model.getTitle_nm());
        //Log.e("INFOEW",vl_model.getTitle_nm());
        Glide.with(itemView).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture)).load(vl_model.getUrlImageTitle()).transition(new DrawableTransitionOptions().crossFade()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600,200).fitCenter()).into(title_image);
        this.idn.setText(vl_model.getIdAnim());
        String ep = context.getString(R.string.list_view_episode)+vl_model.getEp_num();
        this.ep_num.setText(ep);

    }
    @Override
    public void onClick(View v){
        if(this.vl_model!=null){
            Intent intent = new Intent(context.getApplicationContext(), StreamActivity.class);
            intent.putExtra("id_anim",this.vl_model.getIdAnim());
            context.startActivity(intent);
        }

    }
}
