package ml.dvnlabs.animize.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.video_list_model;


public class video_list_adapter extends RecyclerView.Adapter<video_list_holder> {

    private Context mcontext;
    private ArrayList<video_list_model> video_data;
    private int itemResor;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public video_list_adapter(ArrayList<video_list_model>data,Context context,int itemResource){
        //super(context, R.layout.video_list_view,data);
        this.video_data = data;
        this.mcontext = context;
        this.itemResor = itemResource;


    }
    @Override
    public video_list_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new video_list_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(video_list_holder holder,int position){
        video_list_model vlm = this.video_data.get(position);
        holder.bindvideo_list(vlm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(video_data == null){
            return 0;
        }else{
            return video_data.size();
        }

    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }


}
