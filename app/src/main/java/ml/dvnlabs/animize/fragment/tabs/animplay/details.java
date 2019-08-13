package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.PackageStarDBHelper;
import ml.dvnlabs.animize.fragment.comment.mainComment;
import ml.dvnlabs.animize.model.videoplay_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class details extends Fragment {
    private PackageStarDBHelper packageStarDBHelper;
    private CardView video_det_compartment;
    private ExpandableLayout video_dets;
    private TextView synop_play,detailname,idanime,genreer,title_pack,status_star;
    private RelativeLayout add_star_lay;
    private ImageView drop,add_btn,covers;
    private AVLoadingIndicatorView loadbar;
    private ArrayList<videoplay_model> models;
    private String pkganim;

    public details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.ap_fragment_details_tabs, container, false);
        video_det_compartment = view.findViewById(R.id.video_det_comp);
        video_dets = view.findViewById(R.id.video_details);
        synop_play = view.findViewById(R.id.synop_play);
        detailname = view.findViewById(R.id.details_name);
        idanime = view.findViewById(R.id.aplay_txt_idanime);
        title_pack = view.findViewById(R.id.fragment_details_title);
        covers = view.findViewById(R.id.details_img_cover);
        drop = view.findViewById(R.id.titlename_drop);
        genreer = view.findViewById(R.id.aplay_details_genres);
        add_btn = view.findViewById(R.id.aplay_add_star);
        status_star = view.findViewById(R.id.aplay_add_star_status);
        add_star_lay = view.findViewById(R.id.aplay_add_star_layout);

        init_layout();
        initchangestatus();

        return view;
    }


    private void changestatus(){
        ChangeStar star = new ChangeStar();
        if(packageStarDBHelper.isStarred(pkganim)){
            Log.e("CLICKED","UNSTAR");
            star.execute("UNSTAR");
        }else {
            Log.e("CLICKED","STAR");
            star.execute("STAR");
        }
    }

    private void initchangestatus(){
        add_star_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changestatus();
            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changestatus();
            }
        });
        status_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changestatus();
            }
        });
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
    public void receivestring(String pkg,String cover_url){
        this.pkganim = pkg;
        if(!pkganim.isEmpty() && !cover_url.isEmpty()){

            System.out.println(pkganim);
            packageStarDBHelper = new PackageStarDBHelper(getActivity());
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_picture_light)
                            .error(R.drawable.ic_picture_light))
                    .load(cover_url)
                    .transition(new DrawableTransitionOptions()
                            .crossFade()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(covers);
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_picture_light)
                            .error(R.drawable.ic_picture_light))
                    .load(cover_url).transform(new BlurTransformation(25,3))
                    .transition(new DrawableTransitionOptions()
                            .crossFade()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    add_star_lay.setBackground(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    add_star_lay.setBackground(placeholder);
                }
            });

        }
        if(packageStarDBHelper.isAvail()){
            System.out.println("ON Read DB star");
            readStarStatus read = new readStarStatus();
            read.execute();
        }
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
    private class readStarStatus extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Boolean doInBackground(String... params){

            return packageStarDBHelper.isStarred(pkganim);

        }

        @Override
        protected void onPostExecute(Boolean pa){
            if(pa){
                status_star.setText("This anime Starred!");
                add_btn.setImageResource(R.drawable.ic_star);
            }else {
                status_star.setText("This anime not Starred!");
                add_btn.setImageResource(R.drawable.ic_add);
            }

        }
    }
    private class ChangeStar extends AsyncTask<String,Void,Boolean>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Boolean doInBackground(String... params){
            String change = params[0];
            if(change.equals("UNSTAR")){
                System.out.println("UNSTAR");
                packageStarDBHelper.unStar(pkganim);
            }else if(change.equals("STAR")){
                System.out.println("STAR");
                packageStarDBHelper.add_star(pkganim);
            }
            return packageStarDBHelper.isStarred(pkganim);


        }

        @Override
        protected void onPostExecute(Boolean pa){
            System.out.println("OK");
            String status;
            if(pa){
                System.out.println("STAR");
                add_btn.setImageResource(R.drawable.ic_star);
                status_star.setText("This anime Starred!");
                status = "Add to Star Success";
            }else {
                System.out.println("UNSTAR");
                add_btn.setImageResource(R.drawable.ic_add);
                status_star.setText("This anime not Starred!");
                status = "Remove Star Success";
            }
            Toast.makeText(getActivity(),status,Toast.LENGTH_SHORT).show();
        }
    }

}
