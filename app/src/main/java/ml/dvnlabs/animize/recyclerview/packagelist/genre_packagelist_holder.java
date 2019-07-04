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
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.model.genre_packagelist;
import ml.dvnlabs.animize.model.playlist_model;

public class genre_packagelist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView episode;
    private TextView rate,mal;
    private TextView title;
    private ImageView thumbnail;

    private genre_packagelist data;
    private Context context;

    public genre_packagelist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.episode = (TextView)view.findViewById(R.id.genrepackage_episode);
        this.title = (TextView)view.findViewById(R.id.genrepackage_name);
        this.thumbnail = (ImageView)view.findViewById(R.id.genrepackage_cover);
        this.rate = (TextView)view.findViewById(R.id.genrepackages_rate);
        this.mal = (TextView)view.findViewById(R.id.genrepackage_mal);
        itemView.setOnClickListener(this);
    }

    public void bind_playlist(genre_packagelist plm){
        this.data = plm;
        this.title.setText(data.getname());
        String ep_string = data.getNow()+" "+context.getString(R.string.string_of)+" "+data.getTot();
        this.episode.setText(ep_string);
        this.rate.setText(data.getRate());
        String mals = "MAL: "+ data.getMal();
        this.mal.setText(mals);

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


}
