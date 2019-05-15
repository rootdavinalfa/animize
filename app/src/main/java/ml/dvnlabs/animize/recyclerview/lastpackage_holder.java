package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.model.packagelist;

public class lastpackage_holder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private List<String>genres = new ArrayList<>();
    private TextView rate,mal,ep,name,genre;
    private ImageView cover;
    private packagelist data;
    private Context mcontext;


    public lastpackage_holder(Context context,View view){
        super(view);
        this.mcontext = context;
        this.rate = (TextView)view.findViewById(R.id.package_rate);
        this.ep = (TextView)view.findViewById(R.id.package_episode);
        this.name = (TextView)view.findViewById(R.id.package_name);
        this.genre = (TextView)view.findViewById(R.id.package_genre);
        this.mal = (TextView)view.findViewById(R.id.package_mal);
        this.cover = (ImageView)view.findViewById(R.id.package_cover);
        itemView.setOnClickListener(this);
    }

    public void binding(packagelist data){
        this.data = data;
        rate.setText(data.getRate());
        String ep_string = data.getNow()+" "+mcontext.getString(R.string.string_of)+" "+data.getTot();
        ep.setText(ep_string);
         genres = data.getGenre();

         StringBuilder sb = new StringBuilder();
         int counter;
        for (counter=0;counter < genres.size();counter++) {
            sb.append(genres.get(counter));
            if(genres.size()-1> counter){
                sb.append(",");
            }
            //counter++;
        }
        String genree =sb.toString();
        
        name.setText(data.getname());
        genre.setText(genree);
        String mals = "MAL: "+ data.getMal();
        mal.setText(mals);
    }
    @Override
    public void onClick(View v){
        if(this.data!=null){
            Intent intent = new Intent(mcontext, packageView.class);
            intent.putExtra("package",this.data.getPack());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mcontext.startActivity(intent);
            Log.e("CLICK:",this.data.getPack());
        }

    }

}
