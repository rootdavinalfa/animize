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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.home_lastup_model;
import ml.dvnlabs.animize.ui.activity.StreamActivity;
import ml.dvnlabs.animize.ui.recyclerview.lastup_listener;

public class home_lastup_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView title,episode;
    //ImageView img_src;
    MaterialCardView container;
    lastup_listener listener;
    private home_lastup_model model;
    private Context context;

    public home_lastup_holder(Context context,View view){
        super(view);
        this.context = context;
        this.episode = view.findViewById(R.id.episode_lastupload_home);
        this.title = view.findViewById(R.id.title_lastupload_home);
        this.container = view.findViewById(R.id.newepisode_container);
        itemView.setOnClickListener(this);

    }
    public void bind_lastup(home_lastup_model plm){
        this.model = plm;
        this.title.setText(model.getTitle_nm());
        String ep = context.getString(R.string.episode_text)+": "+model.getEp_num();
        this.episode.setText(ep);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model.getUrl_imagetitle()).transform(new RoundedCornersTransformation(5,0))
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                container.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                container.setBackground(placeholder);
            }
        });

    }
    @Override
    public void onClick(View v){
        if(this.model!=null){
            Intent intent = new Intent(context.getApplicationContext(), StreamActivity.class);
            intent.putExtra("id_anim",this.model.getIdn());
            context.startActivity(intent);

        }

    }
}
