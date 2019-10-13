package ml.dvnlabs.animize.fragment.tabs.multiview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.genre_packagelist;
import ml.dvnlabs.animize.recyclerview.packagelist.genre_packagelist_adapter;

import ml.dvnlabs.animize.view.AutoGridLayoutManager;

public class multiview extends DialogFragment {
    private static final int CODE_GET_REQUEST = 1024;
    private ArrayList<genre_packagelist> modeldatapackage;
    private genre_packagelist_adapter adapterlastpackage;
    private AutoGridLayoutManager LayoutManager;
    private RecyclerView rv_listgenre;
    private RelativeLayout genreloading;

    String genre;
    public multiview(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_multiview,container,false);
        rv_listgenre = view.findViewById(R.id.genre_list_package);
        genreloading = view.findViewById(R.id.genre_packagelist_loads);
        rv_listgenre.setVisibility(View.GONE);

        if (getArguments() != null) {
            genre = getArguments().getString("genre");
            Log.e("REQUEST:",genre);
            modeldatapackage = new ArrayList<>();
            getgenrepackage();
        }
        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static multiview newInstance(){
        return new multiview();
    }
    public void requestgenre(String genree){
        Log.e("REQUEST:",genree);
    }

    public void getgenrepackage(){
        String url = Api.url_genrelist+genre;
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(getActivity(),genrepackage,url,CODE_GET_REQUEST,null);
    }


    FetchDataListener genrepackage = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            rv_listgenre.setVisibility(View.VISIBLE);
            genreloading.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    genrepackage_list(object.getJSONArray("anim"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {
            rv_listgenre.setVisibility(View.GONE);
            genreloading.setVisibility(View.VISIBLE);

        }
    };

    private void genrepackage_list(JSONArray anim){
        try {
            modeldatapackage.clear();
            for (int i= 0;i<anim.length();i++){
                JSONObject object = anim.getJSONObject(i);
                String packages = object.getString("package_anim");
                String nameanim = object.getString("name_catalogue");
                String nowep = object.getString("now_ep_anim");
                String totep = object.getString("total_ep_anim");
                String rate = object.getString("rating");
                String mal = object.getString("mal_id");
                String cover = object.getString("cover");
                modeldatapackage.add(new genre_packagelist(packages,nameanim,nowep,totep,rate,mal,cover));
            }
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            LayoutManager = new AutoGridLayoutManager(getContext(),500);
            adapterlastpackage = new genre_packagelist_adapter(modeldatapackage,getActivity(),R.layout.rv_genrepackage);
            rv_listgenre.setLayoutManager(LayoutManager);
            rv_listgenre.setAdapter(adapterlastpackage);
        }catch (JSONException e){
            Log.e("JSON ERROR:",e.toString());
        }

    }
}
