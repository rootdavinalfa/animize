package ml.dvnlabs.animize.recyclerview.banner;

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
import ml.dvnlabs.animize.activity.webview;
import ml.dvnlabs.animize.model.bannerlist_model;

public class banner_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView image;
    private TextView title;


    private bannerlist_model model;
    private Context context;
    public banner_holder(Context context, View view){
        super(view);
        this.context = context;
        this.image = view.findViewById(R.id.banner_image);
        this.title = view.findViewById(R.id.banner_title);
        itemView.setOnClickListener(this);

    }

    public void bindBanner(bannerlist_model bannerlist_model){
        this.model = bannerlist_model;
        Glide.with(itemView).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture))
                .load(model.getBanner_image())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)).fitCenter().into(image);
        this.title.setText(model.getBanner_title());

    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context.getApplicationContext(), webview.class);
        intent.putExtra("url",this.model.getBanner_url());
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Log.e("url",model.getBanner_url());
        context.startActivity(intent);
    }
}
