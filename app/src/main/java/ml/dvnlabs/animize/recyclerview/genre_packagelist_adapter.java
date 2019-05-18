package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.genre_packagelist;
import ml.dvnlabs.animize.model.playlist_model;

public class genre_packagelist_adapter extends RecyclerView.Adapter<genre_packagelist_holder> {
    private Context mcontext;
    private ArrayList<genre_packagelist> packagelists;
    private int itemResor;

    public genre_packagelist_adapter(ArrayList<genre_packagelist>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.packagelists = data;

    }
    @Override
    public genre_packagelist_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new genre_packagelist_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(genre_packagelist_holder holder,int position){
        genre_packagelist slm = this.packagelists.get(position);
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
