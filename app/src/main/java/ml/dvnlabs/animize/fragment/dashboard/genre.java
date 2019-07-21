package ml.dvnlabs.animize.fragment.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.pager.multitab_adapter;


public class genre extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private TabLayout tabLayout;
    private ViewPager pager;
    private List<String>pagetitle;
    private LinearLayout loading;
    private AdView mAdView;

    public genre(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_genre,container,false);
        tabLayout = view.findViewById(R.id.genre_tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager = view.findViewById(R.id.genre_viewpager);
        loading = view.findViewById(R.id.genre_title_load);
        Runnable runnableAdView = new Runnable() {
            @Override
            public void run() {
                ads_starter(view);
            }
        };
        new Handler().postDelayed(runnableAdView,3000);
        getpagetitle();
        return view;
    }
    private void ads_starter(View view){
        AppController.initialize_ads(getActivity());
        mAdView = view.findViewById(R.id.genre_adView);
        if (AppController.isDebug(getActivity())){
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("48D9BD5E389E13283355412BC6A229A2").build();
            mAdView.loadAd(adRequest);
        }else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        //IF TESTING PLEASE UNCOMMENT testmode


    }
    private void getpagetitle(){
        pagetitle = new ArrayList<>();
        String url = Api.url_genremeta;
        Log.e("URL GENRE:",url);
        APINetworkRequest apiNetworkRequest = new APINetworkRequest(getContext(),fetchgenre,url,CODE_GET_REQUEST,null);
    }

    private void initializetab(){
        Log.e("INITIALIZE","CHECK!");
        tabLayout.setupWithViewPager(pager);
        //num is number of tabs,pagetitle is List<>;
        multitab_adapter adapter = new multitab_adapter(getChildFragmentManager(),pagetitle.size(),pagetitle);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public static genre newInstance(){
        return new genre();
    }


    private void setTabTitle(JSONArray titles){
        try {
            for (int i = 0;i<titles.length();i++){
                JSONObject object = titles.getJSONObject(i);
                pagetitle.add(object.getString("name_genre"));
                System.out.println(object.getString("name_genre"));
            }
            initializetab();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    FetchDataListener fetchgenre = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            loading.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    setTabTitle(object.getJSONArray("genre"));
                }
            }catch (JSONException e){
                Log.e("ERROR:",e.toString());
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            loading.setVisibility(View.GONE);
            Log.e("ERROR:",msg);

        }

        @Override
        public void onFetchStart() {
            loading.setVisibility(View.VISIBLE);

        }
    };
}
