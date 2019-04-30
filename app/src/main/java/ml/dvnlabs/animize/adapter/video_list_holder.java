package ml.dvnlabs.animize.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.Random;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animplay_activity;
import ml.dvnlabs.animize.model.video_list_model;

public class video_list_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //private final String url_imagetitle;
    private final TextView title_nm;
    private final TextView ep_num;
    private final TextView idn;
    private final ImageView title_image;
    private final CardView cardView;
    //private final ProgressBar progressBar;

    private video_list_model vl_model;
    private Context context;
    public video_list_holder(Context context,View view){
        super(view);
        this.context = context;
        this.title_nm = (TextView) view.findViewById(R.id.title_video_list);
        this.ep_num = (TextView)view.findViewById(R.id.episode_view);
        this.idn = (TextView)view.findViewById(R.id.id_anime);
        this.title_image = (ImageView)view.findViewById(R.id.title_image);
        this.cardView = (CardView)view.findViewById(R.id.root_video_card);
        //this.progressBar = (ProgressBar)view.findViewById(R.id.loadmore_progressbar);

        itemView.setOnClickListener(this);
    }

    public void bindvideo_list(video_list_model vlm){
        this.vl_model = vlm;
      //  Log.e("DATAAA:",vl_model.getTitle_nm());
        this.title_nm.setText(vl_model.getTitle_nm());
        //Log.e("INFOEW",vl_model.getTitle_nm());
        Glide.with(itemView).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture)).load(vl_model.getUrl_imagetitle()).transition(new DrawableTransitionOptions().crossFade()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(title_image);
        this.idn.setText(vl_model.getIdn());
        String ep = context.getString(R.string.list_view_episode)+vl_model.getEp_num();
        this.ep_num.setText(ep);
        //String clr_bg[] = {"#C62828","#4A148C","#304FFE","#00838F","#5D4037","#827717","#0097A7","#039BE5","#3F51B5","#AA00FF",
        //        "#F50057","#F44336","#37474F","#388E3C"};
        //Random rnd = new Random();
        //MAX must minus 1 from all array
        int max = 13;
        int min = 0;
        //this.cardView.setCardBackgroundColor(Color.parseColor(clr_bg[rnd.nextInt(((max-min)+1))+min]));

    }
    @Override
    public void onClick(View v){
        if(this.vl_model!=null){
            Intent intent = new Intent(context, animplay_activity.class);
            intent.putExtra("id_anim",this.vl_model.getIdn());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("CLICKED",this.vl_model.getIdn());
            context.startActivity(intent);

        }

    }

}
