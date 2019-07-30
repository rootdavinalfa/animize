package ml.dvnlabs.animize.recyclerview.packagelist;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.database.model.recentland;

public class recentlist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView friendlytime,name,episode,lasttime,playedon;
    private ImageView cover;

    private recentland recs;
    private Context context;

    public  recentlist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.cover = view.findViewById(R.id.recentlist_cover);
        this.friendlytime = view.findViewById(R.id.recentlist_friendlyday);
        this.episode = view.findViewById(R.id.recentlist_episode);
        this.name = view.findViewById(R.id.recentlist_name);
        this.lasttime = view.findViewById(R.id.recentlist_lasttime);
        this.playedon = view.findViewById(R.id.recentlist_playedon);
        itemView.setOnClickListener(this);
    }
    public void bind_recent(recentland rec){
        this.recs = rec;
        Date date_modified = new Date(rec.getModified());
        Date time_player = new Date(rec.getTimestamp());
        Calendar cal_date = Calendar.getInstance();
        //Calendar cal_player = Calendar.getInstance();
        TimeZone tz =cal_date.getTimeZone();
        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        format_date.setTimeZone(tz);

        SimpleDateFormat format_date_friendly = new SimpleDateFormat("dd");
        format_date_friendly.setTimeZone(tz);

        SimpleDateFormat format_timeplay = new SimpleDateFormat("mm:ss");

        String played = "Played On: "+format_date.format(date_modified);

        //Day for today,2 day ago
        String day = format_date_friendly.format(date_modified);

        String friendly;

        if (Integer.valueOf(day).equals(Integer.valueOf(format_date_friendly.format(System.currentTimeMillis())))){
            friendly = "Today";
        }else{
            friendly = format_date_friendly.format(date_modified)+" Days Ago";
        }

        String format_playtime = "Last Time: "+format_timeplay.format(time_player);

        this.friendlytime.setText(friendly);
        this.playedon.setText(played);
        this.name.setText(rec.getPackage_name());
        this.lasttime.setText(format_playtime);

        String episodee = "Episode :"+rec.getEpisode();
        this.episode.setText(episodee);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(recs.getUrl_cover())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(cover);
    }

    @Override
    public void onClick(View v) {
        if(this.recs!=null){
            Intent intent = new Intent(context.getApplicationContext(), animplay_activity.class);
            intent.putExtra("id_anim",this.recs.getAnmid());
            context.startActivity(intent);

        }
    }
}
