package ml.dvnlabs.animize.recyclerview.staggered;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.dashboard.genre;
import ml.dvnlabs.animize.model.metagenre_model;

public class metagenre_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private metagenre_model data;
    private Context context;

    private TextView texts;
    private CardView cards;
    private int pos;
    metagenre_adapter adapter;
    private gotopage_genre gotopage_genre;


    public metagenre_holder(Context context, View view,gotopage_genre cal){
        super(view);
        this.context = context;
        this.texts = view.findViewById(R.id.rv_staggered_text);
        this.cards = view.findViewById(R.id.rv_staggered_card);
        this.gotopage_genre = cal;
        cards.setOnClickListener(this);
    }
    public void bind_data(metagenre_model dat,int poss){
        this.data = dat;
        String genre_count = data.getTitle()+" ("+data.getCount()+")";
        this.texts.setText(genre_count);
        //this.pos = position;
        this.pos = poss;
    }

    @Override
    public void onClick(View v) {
        /*if (data!=null){
            gotopage_genre.gotopager(pos);
        }*/
        gotopage_genre.gotopager(this.pos);
    }
    public interface gotopage_genre{
        void gotopager(int page);
    }
}
