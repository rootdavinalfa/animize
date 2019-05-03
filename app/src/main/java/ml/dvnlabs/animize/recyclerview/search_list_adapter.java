package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.model.search_list_model;

public class search_list_adapter extends RecyclerView.Adapter<search_list_holder> {


    private Context mcontext;
    private ArrayList<search_list_model> video_data;
    private int itemResor;

    public search_list_adapter(ArrayList<search_list_model>data,Context context,int itemResource){
        //super(context, R.layout.video_list_view,data);
        this.video_data = data;
        this.mcontext = context;
        this.itemResor = itemResource;

    }

    @Override
    public search_list_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new search_list_holder(this.mcontext,view);
    }

    @Override
    public void onBindViewHolder(search_list_holder holder,int position){
        search_list_model slm = this.video_data.get(position);
        holder.bindsearch_list(slm);

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
}
