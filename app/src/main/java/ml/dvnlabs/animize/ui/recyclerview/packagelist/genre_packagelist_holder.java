/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.packagelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.PackageStarDBHelper;
import ml.dvnlabs.animize.model.GenrePackageList;
import ml.dvnlabs.animize.ui.activity.PackageView;
import ml.dvnlabs.animize.ui.fragment.tabs.multiview.MultiView;

public class genre_packagelist_holder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
    private TextView episode;
    private TextView rate,mal;
    private TextView title;
    private ImageView thumbnail;
    private CardView container;

    private GenrePackageList data;
    private Context context;
    private PackageStarDBHelper packageStarDBHelper;

    public genre_packagelist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.episode = view.findViewById(R.id.genrepackage_episode);
        this.title = view.findViewById(R.id.genrepackage_name);
        this.thumbnail =view.findViewById(R.id.genrepackage_cover);
        this.rate = view.findViewById(R.id.genrepackages_rate);
        this.mal =view.findViewById(R.id.genrepackage_mal);
        this.container = view.findViewById(R.id.genrepack_container);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        packageStarDBHelper = new PackageStarDBHelper(context);
    }

    public void bind_playlist(GenrePackageList plm){
        this.data = plm;
        this.title.setText(data.getName());
        String ep_string = data.getNow()+" "+context.getString(R.string.string_of)+" "+data.getTot();
        this.episode.setText(ep_string);
        this.rate.setText(data.getRate());
        String mals = "MAL: "+ data.getMal();
        this.mal.setText(mals);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_picture_light)
                .error(R.drawable.ic_picture_light))
                .load(data.getCover())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(thumbnail);
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new BlurTransformation(20),new RoundedCornersTransformation(5,0)
        );
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(data.getCover()).transform(multi)
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
    public void onClick(View v){
        if(this.data!=null){
            AppCompatActivity activity = (AppCompatActivity)context;
            if (activity instanceof PackageView) {
                Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("genreFragment");
                if (fragment instanceof MultiView) {
                    ((MultiView) fragment).dismiss();
                }
                ((PackageView) activity).refreshActivity(this.data.getPack());
            }else {
                Intent intent = new Intent(context, PackageView.class);
                intent.putExtra("package",this.data.getPack());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Log.e("CLICK:",this.data.getPack());
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(this.data!=null){
            Boolean isStarred;
            AppCompatActivity activity = (AppCompatActivity) context;
            @SuppressLint("RestrictedApi") Menu menu = new MenuBuilder(context);
            if (packageStarDBHelper.isStarred(this.data.getPack())){
                isStarred = true;
                menu.add(0,Menu.FIRST,0,"UnStar This Package").setIcon(R.drawable.ic_star_nofill);
            }else {
                isStarred = false;
                menu.add(0,Menu.FIRST,0,"Star This Package").setIcon(R.drawable.ic_star);
            }
            changeStar change = new changeStar();
            new BottomSheetMenuDialogFragment.Builder(context)
                    .dark()
                    .setMenu(menu)
                    .setTitle("What do you want ?")
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object) {

                        }

                        @Override
                        public void onSheetItemSelected(@NonNull BottomSheetMenuDialogFragment bottomSheet, MenuItem item, @Nullable Object object) {
                            if (isStarred){
                                change.execute("UNSTAR");
                            }else {
                                change.execute("STAR");
                            }
                        }

                        @Override
                        public void onSheetDismissed(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object, int dismissEvent) {

                        }
                    })
                    .show(activity.getSupportFragmentManager(),"select_package");
        }
        return true;
    }


    private class changeStar extends AsyncTask<String,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String change = strings[0];
            if(change.equals("UNSTAR")){
                System.out.println("UNSTAR");
                packageStarDBHelper.unStar(data.getPack());
            }else if(change.equals("STAR")){
                System.out.println("STAR");
                packageStarDBHelper.addStar(data.getPack());
            }
            return packageStarDBHelper.isStarred(data.getPack());
        }

        @Override
        protected void onPostExecute(Boolean pa) {
            String status;
            if(pa){
                status = "Add to Star Success";
            }else {
                status = "Remove Star Success";
            }
            Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
        }
    }
}
