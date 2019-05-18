package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.player.PlayerManager;

public class playlist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView episode;
    private TextView id_anim;
    private TextView title;
    private ImageView thumbnail;

    private playlist_model playlist_model;
    private Context context;

    public playlist_holder(Context context,View view){
        super(view);
        this.context = context;
        this.episode = (TextView)view.findViewById(R.id.playlist_episode);
        this.id_anim = (TextView)view.findViewById(R.id.playlist_id);
        this.title = (TextView)view.findViewById(R.id.playlist_title);
        this.thumbnail = (ImageView)view.findViewById(R.id.playlist_imgthumb);
        itemView.setOnClickListener(this);
    }

    public void bind_playlist(playlist_model plm){
        this.playlist_model = plm;
        this.title.setText(playlist_model.getTitle());
        this.id_anim.setText(playlist_model.getId_anim());
        String ep = context.getString(R.string.episode_text)+": "+playlist_model.getEpisode();
        this.episode.setText(ep);
        Glide.with(itemView).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture)).load(playlist_model.getUrl_image()).transition(new DrawableTransitionOptions().crossFade()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600,200).fitCenter()).into(thumbnail);

    }
    @Override
    public void onClick(View v){
        if(this.playlist_model!=null){
            ((animplay_activity)context).setIdanim(this.playlist_model.getId_anim());
            ((animplay_activity)context).getVideo();


        }

    }


}
