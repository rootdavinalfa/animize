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

import java.lang.reflect.Array;
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
    private ArrayList<starland> checkerdata;
    private ArrayList<index_model> indexer;
    private ArrayList<String> index_queue;
    private Context context;
    private int poss,maxItem;
    private index2_adapter listen;

    private String tempid;
    private ShimmerFrameLayout loading;
    private LinearLayout recent_layout;

    public starlist_holder(Context context, View view,index2_adapter listern){
        super(view);
        this.context = context;
        this.episode = view.findViewById(R.id.star_episode);
        this.title = view.findViewById(R.id.star_name);
        this.thumbnail = view.findViewById(R.id.star_cover);
        this.rate =view.findViewById(R.id.star_rate);
        this.mal =view.findViewById(R.id.star_mal);
        this.loading = view.findViewById(R.id.rv_shimmer_recent);
        this.recent_layout = view.findViewById(R.id.rv_item_recent);
        this.listen = listern;
        ////System.out.println("CDATA:INIT");
        checkerdata = new ArrayList<>();
        indexer = new ArrayList<>();
        index_queue = new ArrayList<>();
        itemView.setOnClickListener(this);

    }
    public void bind_playlist(starland plm,int position,int maxItem,ArrayList<starland> starlands){
        this.data = plm;
        this.poss = position;
        this.maxItem = maxItem;
        this.checkerdata = starlands;
        if (listen != null){
            indexer = listen.getIndexer();
            index_queue = listen.getQueue();
            if (index_queue.isEmpty() && indexer.size() == 0){
                for (int a=0;a<checkerdata.size();a++){
                    listen.add_queue(checkerdata.get(a).getPackageid());
                    //index_queue.add(checkerdata.get(a).getPackageid());
                }
            }

            ////System.out.println("CDATA:SIZEQUEUE:"+index_queue.size());

            for (int a=0;a<index_queue.size();a++){
                if (index_queue.get(a).equals(this.data.getPackageid())){
                    //Log.e("PACKAGES:",this.data.getPackageid());
                    getRestPackage(this.data.getPackageid());
                }else {
                    ////System.out.println("CDATA:MAXITEM: "+maxItem);
                    for (int i = 0; i<indexer.size();i++){
                        if (indexer.get(i).getPkgid().equals(this.data.getPackageid())){
                            //Log.e("CDATA:PACKAGES CPY:",this.data.getPackageid());
                            copydata();
                        }
                    }

                }
            }
            if (index_queue.size() == 0){
                for (int i = 0; i<indexer.size();i++){
                    if (indexer.get(i).getPkgid().equals(this.data.getPackageid())){
                        //Log.e("CDATA:PACKAGES CPY:",this.data.getPackageid());
                        copydata();
                    }
                }
            }
        }
    }
    @Override
    public void onClick(View v){
        if(this.data!=null && !this.indexer.isEmpty()){
            for (int indx=0;indx<indexer.size();indx++){
                if (this.data.getPackageid().equals(indexer.get(indx).getPkgid())){
                    starmodel starred = indexer.get(indx).getStars();

                    Intent intent = new Intent(context, packageView.class);
                    intent.putExtra("package",starred.getPackageid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ////Log.e("CLICK:",this.data.getPackageid());
                }
            }

        }
    }

    private void getRestPackage(String id){
        this.tempid = id;
        String url = Api.url_packageinfo+id;
        //Log.e("CDATA:REQ: ",url);
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
                //checkerdata.add(new starmodel(packages,nameanim,totep,rate,mal,cover));
                starmodel starmodel =new starmodel(packages,nameanim,totep,rate,mal,cover);
                //indexer.add(new index_model(starmodel,packages));
                if (listen !=null && indexer.size() < maxItem){
                    listen.addmodel(new index_model(starmodel,packages));
                    for (int a=0;a<index_queue.size();a++){
                        if (index_queue.get(a).equals(packages)){
                            //index_queue.remove(a);
                            listen.rem_queue(a);
                        }
                    }
                }
            }
            copydata();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void copydata(){
        if (listen != null){
            indexer = listen.getIndexer();
            for (int indx=0;indx<indexer.size();indx++){
                /*if (this.data.getPackageid().equals(indexer.get(indx).pkgid)){*/
                if (indexer.get(indx).getPkgid().equals(this.data.getPackageid())){
                    starmodel starred = indexer.get(indx).getStars();
                    //System.out.println("CDATA:CPY:COUNT: "+indexer.size()+" POSITION: "+getAdapterPosition()+" PKGHOLD: "+starred.getPackageid()+" PKGADPT: "+this.data.getPackageid());
                    this.title.setText(starred.getName());
                    String ep_string = context.getString(R.string.list_view_episode)+starred.getTotal_ep();
                    this.episode.setText(ep_string);
                    this.rate.setText(starred.getRating());
                    String mals = "MAL: "+ starred.getMal();
                    this.mal.setText(mals);
                    //System.out.println("CDATA:COVR:"+starred.getCover());
                    Glide.with(context)
                            .applyDefaultRequestOptions(new RequestOptions()
                                    .placeholder(R.drawable.ic_picture)
                                    .error(R.drawable.ic_picture))
                            .load(starred.getCover())
                            .transition(new DrawableTransitionOptions()
                                    .crossFade()).apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424,600)).into(thumbnail);
                }
            }
        }
    }
    public class index_model{
        private starmodel stars;
        private String pkgid;
        private index_model(starmodel starred, String pkgid){
            this.stars = starred;
            this.pkgid = pkgid;
        }

        public starmodel getStars() {
            return stars;
        }
        public String getPkgid() {
            return pkgid;
        }
    }
    public interface index2_adapter{
        public void addmodel(index_model models);
        public void add_queue(String pkg);
        public ArrayList<index_model> getIndexer();
        public void rem_queue(int posss);
        public ArrayList<String> getQueue();
    }
}
