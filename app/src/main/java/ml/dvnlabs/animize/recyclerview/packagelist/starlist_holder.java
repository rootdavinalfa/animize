package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.model.starmodel;

public class starlist_holder extends RecyclerView.ViewHolder implements View.OnClickListener { private TextView episode;
    private TextView rate,mal;
    private TextView title;
    private ImageView thumbnail;

    private starmodel data;
    private Context context;

    public starlist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.episode = (TextView)view.findViewById(R.id.star_episode);
        this.title = (TextView)view.findViewById(R.id.star_name);
        this.thumbnail = (ImageView)view.findViewById(R.id.star_cover);
        this.rate = (TextView)view.findViewById(R.id.star_rate);
        this.mal = (TextView)view.findViewById(R.id.star_mal);
        itemView.setOnClickListener(this);

    }
    public void bind_playlist(starmodel plm){
        this.data = plm;
        this.title.setText(data.getName());
        String ep_string = context.getString(R.string.list_view_episode)+data.getTotal_ep();
        this.episode.setText(ep_string);
        this.rate.setText(data.getRating());
        String mals = "MAL: "+ data.getMal();
        this.mal.setText(mals);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(data.getCover())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(thumbnail);

    }
    @Override
    public void onClick(View v){
        if(this.data!=null){
            Intent intent = new Intent(context, packageView.class);
            intent.putExtra("package",this.data.getPackageid());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.e("CLICK:",this.data.getPackageid());

        }

    }
}
