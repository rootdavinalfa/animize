package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.recentland;

public class recentlist_adapter extends RecyclerView.Adapter<recentlist_holder> {
    private Context mcontext;
    private ArrayList<recentland> recentlands;
    private int itemResor;
    public recentlist_adapter(ArrayList<recentland> data, Context context,int itemResource){
        this.mcontext = context;
        this.recentlands = data;
        this.itemResor = itemResource;
    }
    @Override
    public recentlist_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new recentlist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(recentlist_holder holder,int position){
        recentland slm = this.recentlands.get(position);
        holder.bind_recent(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(recentlands == null){
            return 0;
        }else{
            return recentlands.size();
        }

    }
}
