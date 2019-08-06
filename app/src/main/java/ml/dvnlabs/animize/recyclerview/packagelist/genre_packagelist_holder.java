package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.model.genre_packagelist;
import ml.dvnlabs.animize.model.playlist_model;

public class genre_packagelist_holder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
    private TextView episode;
    private TextView rate,mal;
    private TextView title;
    private ImageView thumbnail;
    private CardView container;

    private genre_packagelist data;
    private Context context;

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
    }

    public void bind_playlist(genre_packagelist plm){
        this.data = plm;
        this.title.setText(data.getname());
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
            Intent intent = new Intent(context, packageView.class);
            intent.putExtra("package",this.data.getPack());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.e("CLICK:",this.data.getPack());

        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(this.data!=null){
            AppCompatActivity activity = (AppCompatActivity) context;
            new BottomSheetMenuDialogFragment.Builder(context)
                    .dark()
                    .setSheet(R.menu.package_select)
                    .setTitle("What do you want ?")
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object) {

                        }

                        @Override
                        public void onSheetItemSelected(@NonNull BottomSheetMenuDialogFragment bottomSheet, MenuItem item, @Nullable Object object) {
                            Toast.makeText(context,"Not implemented",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSheetDismissed(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object, int dismissEvent) {

                        }
                    })
                    .show(activity.getSupportFragmentManager(),"select_package");
        }
        return true;
    }
}
