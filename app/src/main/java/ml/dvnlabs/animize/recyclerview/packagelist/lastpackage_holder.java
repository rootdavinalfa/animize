package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.model.packagelist;

public class lastpackage_holder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private List<String>genres = new ArrayList<>();
    private TextView rate,mal,ep,name,genre;
    private ImageView cover;
    private packagelist data;
    private Context mcontext;
    private CardView container;


    public lastpackage_holder(Context context,View view){
        super(view);
        this.mcontext = context;
        this.rate = view.findViewById(R.id.package_rate);
        this.ep = view.findViewById(R.id.package_episode);
        this.name = view.findViewById(R.id.package_name);
        this.genre = view.findViewById(R.id.package_genre);
        this.mal = view.findViewById(R.id.package_mal);
        this.cover = view.findViewById(R.id.package_cover);
        this.container = view.findViewById(R.id.rv_lastpackage_container);
        itemView.setOnClickListener(this);
    }

    public void binding(packagelist data){
        this.data = data;
        rate.setText(data.getRate());
        String ep_string = data.getNow()+" "+mcontext.getString(R.string.string_of)+" "+data.getTot();
        ep.setText(ep_string);
         genres = data.getGenre();

         StringBuilder sb = new StringBuilder();
         int counter;
        for (counter=0;counter < genres.size();counter++) {
            sb.append(genres.get(counter));
            if(genres.size()-1> counter){
                sb.append(",");
            }
            //counter++;
        }
        String genree =sb.toString();

        
        name.setText(data.getname());
        genre.setText(genree);
        String mals = "MAL: "+ data.getMal();
        mal.setText(mals);

        Glide.with(mcontext)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture_light)
                        .error(R.drawable.ic_picture_light))
                .load(data.getCoverur()).transform(new RoundedCornersTransformation(10,0))
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(cover);

        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new BlurTransformation(40),new RoundedCornersTransformation(5,0)
        );

        Glide.with(mcontext)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(data.getCoverur()).transform(multi)
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
        if(this.data!=null){
            Intent intent = new Intent(mcontext, packageView.class);
            intent.putExtra("package",this.data.getPack());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mcontext.startActivity(intent);
            Log.e("CLICK:",this.data.getPack());
        }

    }

}
