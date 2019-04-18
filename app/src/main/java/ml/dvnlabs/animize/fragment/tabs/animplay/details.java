package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.videoplay_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class details extends Fragment {
    private CardView video_det_compartment;
    private ExpandableLayout video_dets;
    private TextView synop_play,detailname,idanime;
    private AVLoadingIndicatorView loadbar;
    private ArrayList<videoplay_model> models;

    public details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.ap_fragment_details_tabs, container, false);
        video_det_compartment = (CardView) view.findViewById(R.id.video_det_comp);
        video_dets = (ExpandableLayout)view.findViewById(R.id.video_details);
        synop_play = (TextView)view.findViewById(R.id.synop_play);
        detailname = (TextView)view.findViewById(R.id.details_name);
        idanime = (TextView)view.findViewById(R.id.aplay_txt_idanime);

        init_layout();
        return view;
    }



    private void init_layout(){
        video_det_compartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_dets.toggle();
            }
        });
    }
    public void receivedata(ArrayList<videoplay_model>data,String idan){

        if(data!=null){
            models = data;
            synop_play.setText(models.get(0).getSysnop());
            detailname.setText(models.get(0).getName_anim());
            idanime.setText(idan);
        }

    }

}
