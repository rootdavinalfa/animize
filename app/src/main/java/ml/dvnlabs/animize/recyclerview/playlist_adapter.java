package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.model.playlist_model;

public class playlist_adapter extends RecyclerView.Adapter<playlist_holder> {
    private Context mcontext;
    private ArrayList<playlist_model> playlistdata;
    private int itemResor;

    public playlist_adapter(ArrayList<playlist_model>data,Context context,int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.playlistdata = data;

    }
    @Override
    public playlist_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new playlist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(playlist_holder holder,int position){
        playlist_model slm = this.playlistdata.get(position);
        holder.bind_playlist(slm);

    }
    @Override
    public int getItemCount(){
        //Log.e("SIZE:",String.valueOf(video_data.size()));
        if(playlistdata == null){
            return 0;
        }else{
            return playlistdata.size();
        }

    }

}