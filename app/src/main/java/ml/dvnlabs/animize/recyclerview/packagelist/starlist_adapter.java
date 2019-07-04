package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.starmodel;

public class starlist_adapter extends RecyclerView.Adapter<starlist_holder> {
    private Context mcontext;
    private ArrayList<starmodel> packagelists;
    private int itemResor;

    public starlist_adapter(ArrayList<starmodel>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.packagelists = data;

    }
    @Override
    public starlist_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new starlist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(starlist_holder holder,int position){
        starmodel slm = this.packagelists.get(position);
        holder.bind_playlist(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(packagelists == null){
            return 0;
        }else{
            return packagelists.size();
        }

    }

}
