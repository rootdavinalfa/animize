package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.starland;
import ml.dvnlabs.animize.model.starmodel;

public class starlist_adapter extends RecyclerView.Adapter<starlist_holder>{
    private Context mcontext;
    //private ArrayList<starmodel> packagelists;
    private ArrayList<starland> packagelists;
    private ArrayList<starlist_holder.index_model> index_models;
    private ArrayList<String> queue;
    private int itemResor;

    public starlist_adapter(ArrayList<starland>data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.packagelists = data;
        index_models = new ArrayList<>();
        queue = new ArrayList<>();

    }
    @Override
    public starlist_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new starlist_holder(this.mcontext,view,listener);

    }

    @Override
    public void onBindViewHolder(starlist_holder holder,int position){
        holder.setIsRecyclable(false);
        //System.out.println("CDATA:POS:"+position+":PKG:"+this.packagelists.get(position).getPackageid());
        starland slm = this.packagelists.get(holder.getAdapterPosition());
        holder.bind_playlist(slm,holder.getAdapterPosition(),this.packagelists.size(),packagelists);

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

    /*@Override
    public void addmodel(starlist_holder.index_model models) {
        this.index_models.add(models);
    }

    @Override
    public ArrayList<starlist_holder.index_model> getIndexer() {
        return index_models;
    }*/
    starlist_holder.index2_adapter listener = new starlist_holder.index2_adapter() {
        @Override
        public void addmodel(starlist_holder.index_model models) {
            index_models.add(models);
        }

        @Override
        public ArrayList<starlist_holder.index_model> getIndexer() {
            return index_models;
        }

        @Override
        public void rem_queue(int posss) {
            queue.remove(posss);
        }

        @Override
        public ArrayList<String> getQueue() {
            return queue;
        }

        @Override
        public void add_queue(String pkg) {
            queue.add(pkg);
        }
    };
}
