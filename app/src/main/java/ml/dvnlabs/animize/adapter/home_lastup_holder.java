package ml.dvnlabs.animize.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.model.home_lastup_model;

public class home_lastup_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView title,episode;
    ImageView img_src;
    lastup_listener listener;
    private home_lastup_model model;
    private Context context;

    public home_lastup_holder(Context context,View view){
        super(view);
        this.context = context;
        this.episode = (TextView)view.findViewById(R.id.episode_lastupload_home);
        this.title = (TextView)view.findViewById(R.id.title_lastupload_home);
        this.img_src = (ImageView)view.findViewById(R.id.img_lastupload_home);
        itemView.setOnClickListener(this);

    }
    public void bind_lastup(home_lastup_model plm){
        this.model = plm;
        this.title.setText(model.getTitle_nm());
        String ep = context.getString(R.string.episode_text)+": "+model.getEp_num();
        this.episode.setText(ep);
        Glide.with(itemView).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture))
                .load(model.getUrl_imagetitle())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(464,261).fitCenter()).into(img_src);

    }
    @Override
    public void onClick(View v){
        if(this.model!=null){
            Intent intent = new Intent(context, animplay_activity.class);
            intent.putExtra("id_anim",this.model.getIdn());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }

    }
}
