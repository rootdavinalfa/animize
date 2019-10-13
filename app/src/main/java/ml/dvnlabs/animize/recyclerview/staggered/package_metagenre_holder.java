package ml.dvnlabs.animize.recyclerview.staggered;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.tabs.multiview.multiview;
import ml.dvnlabs.animize.model.metagenre_model;
import org.greenrobot.eventbus.util.ErrorDialogManager;

public class package_metagenre_holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private Context context;

    private TextView texts;
    private CardView cards;
    private String genre;


    public package_metagenre_holder(Context context, View view){
        super(view);
        this.context = context;
        this.texts = view.findViewById(R.id.rv_staggered_text);
        this.cards = view.findViewById(R.id.rv_staggered_card);
        cards.setOnClickListener(this);
    }
    void bind_data(String genre){
        this.texts.setText(genre);
        this.genre = genre;
    }

    @Override
    public void onClick(View v) {
        AppCompatActivity activity = (AppCompatActivity)context;
        multiview multi;
        Bundle bundle = new Bundle();
        multi = multiview.newInstance();
        bundle.putString("genre",genre);
        multi.setArguments(bundle);
        multi.show(activity.getSupportFragmentManager(),"genreFragment");
        //activity.getSupportFragmentManager().beginTransaction().add(multi,"genre").commit();
    }
}
