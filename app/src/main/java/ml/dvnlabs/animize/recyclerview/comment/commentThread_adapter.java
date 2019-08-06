package ml.dvnlabs.animize.recyclerview.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.commentMainModel;

public class commentThread_adapter extends RecyclerView.Adapter<commentThread_holder> {

    private Context mcontext;
    private ArrayList<commentMainModel> commentMainModels;
    private int itemResor;

    public commentThread_adapter(ArrayList<commentMainModel> data, Context context, int itemResource){
        this.mcontext = context;
        this.itemResor = itemResource;
        this.commentMainModels = data;

    }
    @Override
    public void onBindViewHolder(@NonNull commentThread_holder holder, int position) {
        commentMainModel slm = this.commentMainModels.get(position);
        holder.bindComment(slm);
    }

    @NonNull
    @Override
    public commentThread_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new commentThread_holder(this.mcontext,view);
    }

    @Override
    public int getItemCount() {
        if (commentMainModels == null){
            return 0;
        }else {
            return commentMainModels.size();
        }

    }

}
