package ml.dvnlabs.animize.recyclerview.staggered;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.metagenre_model;

public class package_metagenre_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private Context context;

    private TextView texts;
    private CardView cards;


    public package_metagenre_holder(Context context, View view){
        super(view);
        this.context = context;
        this.texts = view.findViewById(R.id.rv_staggered_text);
        this.cards = view.findViewById(R.id.rv_staggered_card);
        cards.setOnClickListener(this);
    }
    public void bind_data(String genre){
        this.texts.setText(genre);
    }

    @Override
    public void onClick(View v) {

    }
}
