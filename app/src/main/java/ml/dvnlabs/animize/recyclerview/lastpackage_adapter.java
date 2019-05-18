package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.model.packagelist;

public class lastpackage_adapter extends RecyclerView.Adapter<lastpackage_holder> {


    private ArrayList<packagelist> data;
    private Context mcontext;
    private int itemRes;
    public lastpackage_adapter(ArrayList<packagelist> data, Context context, int itemResource){
        this.data = data;
        this.mcontext = context;
        this.itemRes = itemResource;
    }

    @Override
    public lastpackage_holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemRes,parent,false);
        return new lastpackage_holder(this.mcontext,view);
    }


    @Override
    public void onBindViewHolder(@NonNull lastpackage_holder holder, int position) {
        packagelist slm = this.data.get(position);
        holder.binding(slm);

    }

    @Override
    public int getItemCount() {
        if(data == null){
            return 0;
        }else{
            return data.size();
        }
    }
}
