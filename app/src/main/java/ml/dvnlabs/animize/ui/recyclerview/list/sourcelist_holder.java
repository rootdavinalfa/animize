/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.sourcelist;
import ml.dvnlabs.animize.ui.activity.AnimPlayActivity;

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
            ((AnimPlayActivity) context).setSourceID(this.sources.getIds());
            ((AnimPlayActivity) context).newVideoWithNewSource();

        }

    }


}
