/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.packagelist;

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
import ml.dvnlabs.animize.database.model.recentland;
import ml.dvnlabs.animize.ui.activity.StreamActivity;

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
        SimpleDateFormat format_month = new SimpleDateFormat("M");
        format_month.setTimeZone(tz);
        String played = "Played On: "+format_date.format(date_modified);

        //Day for today,2 day ago
        String day = format_date_friendly.format(date_modified);
        int today = Integer.valueOf(format_date_friendly.format(System.currentTimeMillis()));
        String friendly;
        long diff = System.currentTimeMillis() - rec.getModified();
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        //System.out.println("NAME: "+rec.getPackage_name()+" Episode:"+rec.getEpisode()+" Times:"+ diffDays+"/"+diffHours+"/"+diffMinutes);
        if (Integer.valueOf(day) == today && Integer.valueOf(format_month.format(date_modified)).equals( Integer.valueOf(format_month.format(System.currentTimeMillis())))){
            if (diffMinutes <=58 && diffHours == 0){
                friendly ="Today, "+ diffMinutes + " Minutes ago";
            }else {
                friendly = "Today, "+diffHours+" Hours ago";
            }
        }
        else {
            if (diffDays < 1){
                friendly = "Yesterday, "+diffHours +" Hours ago";
            }else {
                if (today - 1 == Integer.valueOf(day)){
                    //friendly = "Yesterday, "+ diffHours +" Hours ago";
                    friendly = "Yesterday"+ (diffHours==0?"":", "+diffHours+" Hours ago");
                }else{
                    friendly = diffDays+" Days " + (diffHours==0?" Ago":", "+diffHours+" Hours ago");
                    //friendly = "Yesterday"+ (diffHours==0?"":", "+diffHours+" Hours ago");
                }
            }
        }


        String format_playtime = "Last Time: "+format_timeplay.format(time_player);

        this.friendlytime.setText(friendly);
        this.playedon.setText(played);
        this.name.setText(rec.getPackage_name());
        this.lasttime.setText(format_playtime);

        String episodee = "Episode: "+rec.getEpisode();
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
            Intent intent = new Intent(context.getApplicationContext(), StreamActivity.class);
            intent.putExtra("id_anim",this.recs.getAnmid());
            context.startActivity(intent);

        }
    }
}
