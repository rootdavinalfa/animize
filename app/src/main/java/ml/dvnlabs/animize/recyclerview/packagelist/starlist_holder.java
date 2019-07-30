package ml.dvnlabs.animize.recyclerview.packagelist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.packageView;
import ml.dvnlabs.animize.database.model.starland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.starmodel;
import ml.dvnlabs.animize.view.AutoGridLayoutManager;

public class starlist_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView episode;
    private TextView rate,mal;
    private TextView title;
    private ImageView thumbnail;

    private starland data;
    private ArrayList<starmodel> checkerdata;
    private Context context;


    private String tempid;
    private ShimmerFrameLayout loading;
    private LinearLayout recent_layout;

    public starlist_holder(Context context, View view){
        super(view);
        this.context = context;
        this.episode = view.findViewById(R.id.star_episode);
        this.title = view.findViewById(R.id.star_name);
        this.thumbnail = view.findViewById(R.id.star_cover);
        this.rate =view.findViewById(R.id.star_rate);
        this.mal =view.findViewById(R.id.star_mal);
        this.loading = view.findViewById(R.id.rv_shimmer_recent);
        this.recent_layout = view.findViewById(R.id.rv_item_recent);
        checkerdata = new ArrayList<>();
        itemView.setOnClickListener(this);

    }
    public void bind_playlist(starland plm){
        this.data = plm;
        if (checkerdata.isEmpty()){
            getRestPackage(this.data.getPackageid());
        }else {
            copydata();
        }
        /*if (!checkerdata.isEmpty() && plm.getPackageid().equals(checkerdata.get(0).getPackageid())){
            copydata();
        }*/
    }
    @Override
    public void onClick(View v){
        if(this.data!=null && !this.checkerdata.isEmpty()){
            Intent intent = new Intent(context, packageView.class);
            intent.putExtra("package",this.checkerdata.get(0).getPackageid());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //Log.e("CLICK:",this.data.getPackageid());

        }
    }

    private void getRestPackage(String id){
        this.tempid = id;
        String url = Api.url_packageinfo+id;
        Log.e("CDATA:REQ: ",url);
        new APINetworkRequest(context,fetchPackage,url,1024,null);
    }

    FetchDataListener fetchPackage = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            loading.stopShimmer();
            loading.setVisibility(View.GONE);
            recent_layout.setVisibility(View.VISIBLE);
            try {
                JSONObject object = new JSONObject(data);
                if (!object.getBoolean("error")){
                    addToArrayPackage(object.getJSONArray("anim"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            getRestPackage(tempid);
        }

        @Override
        public void onFetchStart() {
            loading.startShimmer();
            loading.setVisibility(View.VISIBLE);
            recent_layout.setVisibility(View.GONE);
            /*voided.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);*/

        }
    };

    private void addToArrayPackage(JSONArray pack){
        try {
            for (int i=0;i<pack.length();i++){
                JSONObject object = pack.getJSONObject(i);
                String packages = object.getString("package_anim");
                String nameanim = object.getString("name_catalogue");
                String totep = object.getString("total_ep_anim");
                String rate = object.getString("rating");
                String mal = object.getString("mal_id");
                String cover = object.getString("cover");
                checkerdata.add(new starmodel(packages,nameanim,totep,rate,mal,cover));
            }
            copydata();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void copydata(){
        this.title.setText(checkerdata.get(0).getName());
        String ep_string = context.getString(R.string.list_view_episode)+checkerdata.get(0).getTotal_ep();
        this.episode.setText(ep_string);
        this.rate.setText(checkerdata.get(0).getRating());
        String mals = "MAL: "+ checkerdata.get(0).getMal();
        this.mal.setText(mals);
        System.out.println("CDATA:COVR:"+checkerdata.get(0).getCover());
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(checkerdata.get(0).getCover())
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(thumbnail);
    }
}
