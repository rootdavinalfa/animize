/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview.comment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.api_usermodel;
import ml.dvnlabs.animize.model.commentMainModel;

public class commentThread_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView user,dateadded,contents;


    private commentMainModel model;
    private Context context;

    public commentThread_holder(Context context, View view){
        super(view);
        this.context = context;
        this.contents = view.findViewById(R.id.threads_comment_content);
        this.dateadded = view.findViewById(R.id.threads_comments_added);
        this.user = view.findViewById(R.id.threads_comments_users);
        itemView.setOnClickListener(this);

    }

    public void bindComment(commentMainModel commentMainModel){
        this.model = commentMainModel;
        ArrayList<api_usermodel> usermodels = model.getUsermodels();
        this.user.setText(usermodels.get(0).getName_user());
        this.contents.setText(model.getContents());

    }

    @Override
    public void onClick(View v) {
        if(this.model!=null){
            //ArrayList<commentMainModel> data = new ArrayList<>();
            //data.add(model);
            //String idcomment = model.getIdcomment();
            //String status = model.getStatus();
            //String content = model.getContents();
            //ArrayList<api_usermodel>usermodels = model.getUsermodels();
            //System.out.println(idcomment+status+content);

            //data.add(new commentMainModel(idcomment,status,content,
              //      usermodels));
            //((animplay_activity)context).showreplyfragment(data);
        }
    }

}
