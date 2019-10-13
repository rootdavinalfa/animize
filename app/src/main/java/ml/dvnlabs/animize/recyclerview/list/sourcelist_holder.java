package ml.dvnlabs.animize.recyclerview.list;

import android.content.Context;
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
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.model.playlist_model;
import ml.dvnlabs.animize.model.sourcelist;

public class sourcelist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView ids,by;

    private sourcelist sources;
    private Context context;

    public sourcelist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.ids = view.findViewById(R.id.sourceslist_id);
        this.by = view.findViewById(R.id.sourceslist_byuser);
        itemView.setOnClickListener(this);
    }

    public void bind_playlist(sourcelist plm){
        this.sources = plm;
        this.ids.setText(sources.getIds());
        this.by.setText(sources.getBy_user());

    }
    @Override
    public void onClick(View v){
        if(this.sources!=null){
            ((animplay_activity)context).setSourceID(this.sources.getIds());
            ((animplay_activity)context).newVideoWithNewSource();

        }

    }


}
