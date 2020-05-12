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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.ui.activity.StreamActivity;

public class playlist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView episode;
    private TextView id_anim;
    private TextView title;
    private ImageView thumbnail;
    private View now_sign;

    private playlist_model playlist_model;
    private Context context;

    public playlist_holder(Context context,View view){
        super(view);
        this.context = context;
        this.episode = view.findViewById(R.id.playlist_episode);
        this.id_anim =view.findViewById(R.id.playlist_id);
        this.title = view.findViewById(R.id.playlist_title);
        this.thumbnail = view.findViewById(R.id.playlist_imgthumb);
        this.now_sign = view.findViewById(R.id.playlist_now_sign);
        itemView.setOnClickListener(this);
    }

    public void bind_playlist(playlist_model plm,int now,int pos){
        this.playlist_model = plm;
        this.title.setText(playlist_model.getTitle());
        this.id_anim.setText(playlist_model.getId_anim());
        String ep = context.getString(R.string.episode_text)+": "+playlist_model.getEpisode();
        this.episode.setText(ep);
        if (now == pos){
            this.now_sign.setVisibility(View.VISIBLE);
        }
        Glide.with(itemView)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture))
                .load(playlist_model.getUrl_image()).transition(new DrawableTransitionOptions().crossFade()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600,200).fitCenter())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(10,0)))
                .into(thumbnail);

    }
    @Override
    public void onClick(View v){
        if(this.playlist_model!=null){
            ((StreamActivity)context).setIdAnim(this.playlist_model.getId_anim());
            ((StreamActivity)context).getVideo();


        }

    }


}
