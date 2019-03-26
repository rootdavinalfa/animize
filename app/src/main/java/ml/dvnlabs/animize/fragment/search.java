package ml.dvnlabs.animize.fragment;


import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.adapter.search_list_adapter;
import ml.dvnlabs.animize.adapter.video_list_adapter;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.loader.animlist_loader;
import ml.dvnlabs.animize.model.search_list_model;
import ml.dvnlabs.animize.model.video_list_model;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener {

    EditText search_fi;
    Button srcbtnret;
    TextView txt_error;
    ImageView erro_v;
    ImageView info_img;
    TextView overview;
    ProgressBar src_pb;

    ImageButton search_btn;
    private String name;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private RecyclerView listView;
    private ArrayList<search_list_model> modeldata;
    private search_list_adapter adapter;
    private boolean isFirst = true;
    //private SwipeRefreshLayout swipe_list;

    public search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_search, container, false);
        search_fi = (EditText)view.findViewById(R.id.search_field);
        search_btn = (ImageButton)view.findViewById(R.id.search_button);
        listView = (RecyclerView) view.findViewById(R.id.search_listview);
        srcbtnret = (Button)view.findViewById(R.id.src_btn_retry);
        txt_error = (TextView)view.findViewById(R.id.srcerror_txt);
        erro_v = (ImageView)view.findViewById(R.id.iv_src_error);
        src_pb = (ProgressBar)view.findViewById(R.id.src_loadingbar);
        overview = (TextView)view.findViewById(R.id.srcoverview);
        info_img = (ImageView)view.findViewById(R.id.src_imageinfo);
        search_btn.setOnClickListener(this);
        srcbtnret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action_btn();
            }
        });
        search_fi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    action_btn();
                    return true;
                }
                return false;
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened

                    info_img.setVisibility(View.GONE);
                    overview.setVisibility(View.GONE);
                }
                else {
                    if(isFirst){
                        info_img.setVisibility(View.VISIBLE);
                        overview.setVisibility(View.VISIBLE);
                    }
                    if (!isFirst){
                        info_img.setVisibility(View.GONE);
                        overview.setVisibility(View.GONE);
                    }

                    // keyboard is closed
                }
            }
        });


        // Inflate the layout for this fragment
        return view;

    }
    private void action_btn(){
        isFirst=false;
        overview.setVisibility(View.GONE);
        info_img.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        src_pb.setVisibility(View.VISIBLE);
        txt_error.setVisibility(View.GONE);
        src_pb.setVisibility(View.GONE);
        erro_v.setVisibility(View.GONE);
        srcbtnret.setVisibility(View.GONE);
        String getname = search_fi.getText().toString();
        //name = Uri.parse(Api.url_search).buildUpon().appendQueryParameter("sa","aa").build().toString();
        name = Uri.encode(getname);
        //Log.e("INF",getname+"::::"+name);
        getLoaderManager().restartLoader(3,null,search.this);
    }
    @Override
    public void onClick(View view){
        if(search_fi.getText().toString().isEmpty()){
            isFirst = true;
            txt_error.setVisibility(View.GONE);
            erro_v.setVisibility(View.GONE);
            info_img.setVisibility(View.VISIBLE);
            overview.setVisibility(View.VISIBLE);
            overview.setText(getString(R.string.no_title_provided));
        }else{
            action_btn();
        }

    }

    public static search newInstance(){
        return new search();

    }



    private void show_video(JSONArray video){
        listView.setVisibility(View.VISIBLE);
        modeldata = new ArrayList<>();
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


        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        adapter = new search_list_adapter(modeldata,getActivity(),R.layout.search_list_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(layoutManager);
        //listView.addItemDecoration(itemDecorator);
        listView.setAdapter(adapter);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        String url = Api.url_search+name;
        src_pb.setVisibility(View.VISIBLE);
        //Log.e("INFO3282",url);
        //progressBar.setVisibility(View.VISIBLE);
        //textload.setVisibility(View.VISIBLE);
        return new animlist_loader(getContext(),url,null,CODE_GET_REQUEST);
    }

    @Override
    public void onLoadFinished(Loader<String> loader,String data){
        try{
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                show_video(object.getJSONArray("anim"));
                adapter.notifyDataSetChanged();
                src_pb.setVisibility(View.GONE);
                //Log.e("INFAA",String.valueOf(adapter.getItemCount()));
                /*
                progressBar.setVisibility(View.GONE);
                textload.setVisibility(View.GONE);
                iv_error.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
                texterrorload.setVisibility(View.GONE);*/

                // Scheme colors for animation

            }else{
                src_pb.setVisibility(View.GONE);
                erro_v.setVisibility(View.VISIBLE);
                txt_error.setVisibility(View.VISIBLE);
                txt_error.setText(object.getString("message"));

            }

        }catch (JSONException e){
            e.printStackTrace();
            erro_v.setVisibility(View.VISIBLE);
            txt_error.setVisibility(View.VISIBLE);
            txt_error.setText(getString(R.string.datalinkerror));
            /*
            iv_error.setVisibility(View.VISIBLE);
            texterrorload.setVisibility(View.VISIBLE);
            btn_retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            textload.setVisibility(View.GONE);*/
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {}

}
