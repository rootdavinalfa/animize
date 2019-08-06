package ml.dvnlabs.animize.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.model.search_list_pack_model;
import ml.dvnlabs.animize.recyclerview.list.search_list_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.model.search_list_model;
import ml.dvnlabs.animize.recyclerview.packagelist.search_package_adapter;
import ml.dvnlabs.animize.view.AutoGridLayoutManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private RecyclerView listView;
    private DiscreteScrollView packview;
    private ArrayList<search_list_model> modeldata;
    private ArrayList<search_list_pack_model>pack_models;
    private search_list_adapter adapter;
    private search_package_adapter package_adapter;
    private EditText srch_txt;
    private TextView src_error,title_pack,title_ep;
    private String srch_txt_get;
    private ImageView srch_iv;
    private boolean isReadyForNextReq = false;

    private Runnable search_runnable;
    private Handler search_handler;

    public search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_search, container, false);
        listView = view.findViewById(R.id.search_listview);
        packview = view.findViewById(R.id.search_package_listview);
        srch_txt = view.findViewById(R.id.srch_txt_edit);
        src_error = view.findViewById(R.id.src_error_txt);
        srch_iv = view.findViewById(R.id.error_v_srch);
        title_ep = view.findViewById(R.id.search_ep_title);
        title_pack = view.findViewById(R.id.search_pack_title);
        title_pack.setVisibility(View.GONE);
        title_ep.setVisibility(View.GONE);

        modeldata = new ArrayList<>();
        pack_models = new ArrayList<>();
        /*modeldata.add(new search_list_model(null,null,null,null));*/

        init_handler();
        srch_text_logiclistener();
        // Inflate the layout for this fragment
        return view;

    }
    private void init_handler(){
        search_runnable = new Runnable() {
            @Override
            public void run() {
                getSearch();
            }
        };
        search_handler = new Handler();
    }

    private void srch_text_logiclistener() {
        srch_txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    srch_txt.clearFocus();
                    hideKeyboard(v);
                    return true; //this is required to stop sending key event to parent
                }
                return false;
            }
        });
        srch_txt.addTextChangedListener(new TextWatcher() {
            int timing_text = 0;
            Handler handler = new Handler();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_handler.removeCallbacks(search_runnable);
                src_error.setVisibility(View.GONE);
                srch_txt_get = s.toString();
                int search_text_count = srch_txt.getText().length();
                if(!modeldata.isEmpty() && !pack_models.isEmpty() && search_text_count == 0){
                    modeldata.clear();
                    pack_models.clear();
                    adapter.notifyDataSetChanged();
                    package_adapter.notifyDataSetChanged();
                    //System.out.println("TRIGGER");
                }
                if (search_text_count > 0){
                    if(!modeldata.isEmpty() && !pack_models.isEmpty()){
                        modeldata.clear();
                        pack_models.clear();
                        adapter.notifyDataSetChanged();
                        package_adapter.notifyDataSetChanged();
                        //System.out.println("TRIGGER2");
                    }
                    /*search_text_count = search_text_count % 2;
                    if(search_text_count == 0 || isReadyForNextReq){
                        getSearch();
                    }*/
                    search_handler.postDelayed(search_runnable,2000);

                }else{
                    title_pack.setVisibility(View.GONE);
                    title_ep.setVisibility(View.GONE);
                    src_error.setVisibility(View.VISIBLE);
                    String errormsg = getResources().getString(R.string.search_not_provided);
                    src_error.setText(errormsg);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getSearch(){
        if (!srch_txt_get.equals("")){
            if (!modeldata.isEmpty()){
                modeldata.clear();
            }
            if (!pack_models.isEmpty()){
                pack_models.clear();
            }

            String url = Api.url_search+srch_txt_get;
            System.out.println("TXT:"+url);
            new APINetworkRequest(getActivity(),search,url,CODE_GET_REQUEST,null);
            String url_searchpack = Api.url_search_pack+srch_txt_get;
            new APINetworkRequest(getActivity(),search_pack,url_searchpack,CODE_GET_REQUEST,null);
        }else {
            src_error.setVisibility(View.VISIBLE);
            String errormsg = getResources().getString(R.string.search_not_provided);
            src_error.setText(errormsg);
        }
    }
    FetchDataListener search = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            isReadyForNextReq = true;
            title_ep.setVisibility(View.VISIBLE);
            srch_iv.setVisibility(View.GONE);
            src_error.setVisibility(View.GONE);
            jsonparser_search(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            isReadyForNextReq = false;
            title_ep.setVisibility(View.GONE);
            srch_iv.setVisibility(View.VISIBLE);
            src_error.setVisibility(View.VISIBLE);
            src_error.setText(msg);
        }

        @Override
        public void onFetchStart() {
            title_ep.setVisibility(View.GONE);
            isReadyForNextReq = false;

        }
    };
    FetchDataListener search_pack = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            isReadyForNextReq = true;
            title_pack.setVisibility(View.VISIBLE);
            /*srch_iv.setVisibility(View.GONE);
            src_error.setVisibility(View.GONE);*/
            jsonparser_search_pack(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            isReadyForNextReq = false;
            title_pack.setVisibility(View.GONE);
            /*srch_iv.setVisibility(View.VISIBLE);
            src_error.setVisibility(View.VISIBLE);
            src_error.setText(msg);*/
        }

        @Override
        public void onFetchStart() {
            isReadyForNextReq = false;
            title_pack.setVisibility(View.GONE);

        }
    };


    private void jsonparser_search(String data){
        try {
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                show_video(object.getJSONArray("anim"));
            }
            else{
                src_error.setVisibility(View.VISIBLE);
                src_error.setText("Your keyword seem wrong,please try another keyword\n");
                src_error.setText(object.getString("message"));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void jsonparser_search_pack(String data){
        try {
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                show_pack(object.getJSONArray("package"));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void show_pack(JSONArray pack){
        try {
            for (int i=0;i<pack.length();i++){
                JSONObject jsonObject = pack.getJSONObject(i);
                String pkgid = jsonObject.getString("package_anim");
                String title = jsonObject.getString("name_catalogue");
                String now = jsonObject.getString("now_ep_anim");
                String total = jsonObject.getString("total_ep_anim");
                String rating = jsonObject.getString("rating");
                String cover = jsonObject.getString("cover");
                pack_models.add(new search_list_pack_model(pkgid,title,now,total,rating,cover));
            }
            package_adapter = new search_package_adapter(pack_models,getActivity(),R.layout.rv_package_minimal);
            //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            //AutoGridLayoutManager layoutManager = new AutoGridLayoutManager(getActivity(),2);
            /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            packview.setLayoutManager(layoutManager);*/
            System.out.println("ADAPATER:"+package_adapter.getItemCount());
            packview.setAdapter(package_adapter);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    private void show_video(JSONArray video){
        listView.setVisibility(View.VISIBLE);
        //modeldata.clear();
        try{
            for(int i = 0;i<video.length();i++){
                JSONObject jsonObject = video.getJSONObject(i);
                String url_tb = jsonObject.getString(Api.JSON_episode_thumb);
                String id = jsonObject.getString(Api.JSON_id_anim);
                String title_name = jsonObject.getString(Api.JSON_name_anim);
                String episode = jsonObject.getString(Api.JSON_episode_anim);
                //Log.e("INF::",title_name);
                modeldata.add(new search_list_model(url_tb,id,title_name,episode));

            }
            adapter = new search_list_adapter(modeldata,getActivity(),R.layout.search_list_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(adapter);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        //listView.addItemDecoration(itemDecorator);

    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static search newInstance(){
        return new search();

    }


}
