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
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.SearchListPackageModel;
import ml.dvnlabs.animize.ui.activity.PackageView;

public class search_package_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private Context context;
    private SearchListPackageModel model;

    private CardView container;
    private TextView titlename,rate,episode;
    private ImageView cover;
    public search_package_holder(Context context, View view){
        super(view);
        this.context = context;
        this.container = view.findViewById(R.id.searchpack_container);
        this.cover = view.findViewById(R.id.searchpackage_cover);
        this.titlename = view.findViewById(R.id.searchpackage_name);
        this.rate = view.findViewById(R.id.searchpackages_rate);
        this.episode = view.findViewById(R.id.searchpackage_episode);
        this.container.setOnClickListener(this);
    }
    public void bind_search_package(SearchListPackageModel model){
        this.model = model;
        this.titlename.setText(model.getTitle());
        this.rate.setText(model.getRating());
        String ep = model.getNow() + " OF "+model.getTot();
        this.episode.setText(ep);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model.getCover()).transform(new RoundedCornersTransformation(10,0))
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(cover);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model.getCover()).transform(new BlurTransformation(25,3))
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(new CustomTarget<Drawable>() {
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
    public void onClick(View v) {
        if (model != null){
            Intent intent = new Intent(context, PackageView.class);
            intent.putExtra("package",model.getPkgid());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
