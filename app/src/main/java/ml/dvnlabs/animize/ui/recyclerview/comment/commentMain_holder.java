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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.APIUserModel;
import ml.dvnlabs.animize.model.CommentMainModel;
import ml.dvnlabs.animize.ui.activity.StreamActivity;

public class commentMain_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView user,dateadded,contents;
    private ImageView replys;


    private CommentMainModel model;
    private Context context;

    public commentMain_holder(Context context, View view){
        super(view);
        this.context = context;
        this.contents = view.findViewById(R.id.comments_content);
        this.dateadded = view.findViewById(R.id.comments_added);
        this.user = view.findViewById(R.id.comments_users);
        this.replys = view.findViewById(R.id.comments_replys);
        itemView.setOnClickListener(this);

    }

    void bindComment(CommentMainModel commentMainModel){
        this.model = commentMainModel;
        ArrayList<APIUserModel> usermodels = model.getUserModels();
        this.user.setText(usermodels.get(0).getName_user());
        this.contents.setText(model.getContents());

    }

    @Override
    public void onClick(View v) {
        if(this.model!=null){
            ArrayList<CommentMainModel> data = new ArrayList<>();
            data.add(model);
            //String idcomment = model.getIdcomment();
            //String status = model.getStatus();
            //String content = model.getContents();
            //ArrayList<api_usermodel>usermodels = model.getUsermodels();
            //System.out.println(idcomment+status+content);

            //data.add(new commentMainModel(idcomment,status,content,
              //      usermodels));
            ((StreamActivity)context).showReplyFragment(data);
        }
    }

}
