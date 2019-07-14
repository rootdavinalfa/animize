package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.comment.mainComment;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.model.videoplay_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class details extends Fragment {
    private CardView video_det_compartment;
    private ExpandableLayout video_dets;
    private TextView synop_play,detailname,idanime,genreer,title_pack;
    private ImageView drop;
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
        title_pack = view.findViewById(R.id.fragment_details_title);
        drop = (ImageView)view.findViewById(R.id.titlename_drop);
        genreer = (TextView)view.findViewById(R.id.aplay_details_genres);

        init_layout();
        return view;
    }



    private void init_layout(){
        video_det_compartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_dets.toggle();
                float deg = (drop.getRotation() == 180F) ? 0F : 180F;
                drop.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });
        showcomment();
    }
    public void receivedata(ArrayList<videoplay_model>data,String idan){

        if(data!=null){
            models = data;
            synop_play.setText(models.get(0).getSysnop());
            title_pack.setText(models.get(0).getName_anim());
            detailname.setText(models.get(0).getName_anim());
            idanime.setText(idan);
            List<String> genree = new ArrayList<>();
            genree = models.get(0).getGenres();
            StringBuilder sb = new StringBuilder();
            int counter;
            for (counter=0;counter < genree.size();counter++) {
                sb.append(genree.get(counter));
                if(genree.size()-1> counter){
                    sb.append(",");
                }
                //counter++;
            }
            String gen =sb.toString();
            genreer.setText(gen);
            senddata2mainComment(idan);
        }

    }

    private void showcomment(){
        mainComment se = mainComment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.aplay_fragment_comment,se,"COMMENT_MAIN")
                .addToBackStack("COMMENT_THREAD").commit();
        //global.addFragment(getChildFragmentManager(),se,R.id.aplay_fragment_comment,"FRAGMENT_OTHER","SLIDE");
    }
    private void senddata2mainComment(String idanim){
        mainComment mc = (mainComment) getChildFragmentManager().findFragmentByTag("COMMENT_MAIN");
        mc.receivedata(idanim);

    }

}
