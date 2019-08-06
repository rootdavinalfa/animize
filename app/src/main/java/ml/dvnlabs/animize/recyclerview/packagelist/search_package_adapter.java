package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.search_list_pack_model;

public class search_package_adapter extends RecyclerView.Adapter<search_package_holder> {
    private Context context;
    private ArrayList<search_list_pack_model> data;
    private int itemRes;
    public search_package_adapter(ArrayList<search_list_pack_model> models, Context context,int itemResor){
        this.data = models;
        this.context = context;
        this.itemRes = itemResor;
    }

    @Override
    public void onBindViewHolder(@NonNull search_package_holder holder, int position) {
        search_list_pack_model model = this.data.get(holder.getAdapterPosition());
        holder.bind_search_package(model);

    }

    @NonNull
    @Override
    public search_package_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemRes,parent,false);
        return new search_package_holder(this.context,view);
    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }else {
            return  data.size();
        }
    }
}
