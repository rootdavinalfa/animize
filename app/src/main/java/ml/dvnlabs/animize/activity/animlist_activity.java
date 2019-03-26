package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.fragment.animize;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.fragment.profile;
import ml.dvnlabs.animize.fragment.search;
import ml.dvnlabs.animize.fragment.video_list;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.michaldrabik.tapbarmenulib.TapBarMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static ml.dvnlabs.animize.activity.MainActivity.setWindowFlag;


public class animlist_activity extends AppCompatActivity   {

    LoginInternalDBHelper loginInternalDBHelper;
    private String id_users,name_users,emails;
    String tokeen;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    static final String STATE_FRAGMENT = "state_of_fragment";

    private String TAG = animlist_activity.class.getSimpleName();
    private boolean videolistisDisplayed = false;
    private boolean need_retry_firstuse = false;
    //TapBarMenu tapBarMenu;
    private LinearLayout header;
    private ImageView btn_profile;

    @BindView(R.id.tapBarMenu) TapBarMenu tapBarMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animlist_activity);
        header = (LinearLayout) findViewById(R.id.header_animize);
        btn_profile = (ImageView) findViewById(R.id.btn_profile);
        //SHOW header
        header.setVisibility(View.VISIBLE);
        ButterKnife.bind(this);
        display_video_list();
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_btn();
            }
        });



    }
    private void profile_btn(){
        close_video_list();
        close_search();
        header.setVisibility(View.GONE);
        profile vl = profile.newInstance();
        global.addFragment(getSupportFragmentManager(),vl,R.id.profile_fragment,"FRAGMENT_OTHER");
    }



    @Override
    public void onBackPressed()
    {
        header.setVisibility(View.VISIBLE);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.e("COUNTED-:",String.valueOf(count));
        if (count == 0) {
            super.onBackPressed();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }


    public void display_video_list(){
        header.setVisibility(View.VISIBLE);
        close_profile();
        //int count = getSupportFragmentManager().getBackStackEntryCount();
        //Log.e("COUNTED+:",String.valueOf(count));
        video_list vl = video_list.newInstance();
        global.addFragment(getSupportFragmentManager(),vl,R.id.video_list_fragment,"FRAGMENT_HOME");
        videolistisDisplayed = true;

    }
    public void close_video_list(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        video_list simpleFragment = (video_list) fragmentManager
                .findFragmentById(R.id.video_list_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        videolistisDisplayed = false;
    }

    public void display_search(){
        header.setVisibility(View.VISIBLE);
        //int count = getSupportFragmentManager().getBackStackEntryCount();
        //Log.e("COUNTED+:",String.valueOf(count));
        search se = search.newInstance();

        global.addFragment(getSupportFragmentManager(),se,R.id.search_fragment,"FRAGMENT_OTHER");
        videolistisDisplayed = false;
    }

    public void close_search(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        search simpleFragment = (search) fragmentManager
                .findFragmentById(R.id.search_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        videolistisDisplayed = false;
    }
    public void display_more(){
        close_video_list();
        close_search();
        header.setVisibility(View.GONE);
        //int count = getSupportFragmentManager().getBackStackEntryCount();
        //Log.e("COUNTED+:",String.valueOf(count));
        animize se = animize.newInstance();
        global.addFragment(getSupportFragmentManager(),se,R.id.animize_fragment,"FRAGMENT_OTHER");
        videolistisDisplayed = false;
    }

    public void close_more(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        animize simpleFragment = (animize) fragmentManager
                .findFragmentById(R.id.animize_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        videolistisDisplayed = false;
    }
    public void close_profile(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        profile simpleFragment = (profile) fragmentManager
                .findFragmentById(R.id.profile_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
        videolistisDisplayed = false;
    }

    @OnClick(R.id.tapBarMenu)
    public void onMenuButtonClick(){
        tapBarMenu.toggle();
    }

    @OnClick({ R.id.menubar_play, R.id.menubar_user,R.id.menubar_search,R.id.menubar_more})
        public void onMenuItemClick(View view) {
        // Add the SimpleFragment.

            tapBarMenu.close();
            switch (view.getId()) {
                case R.id.menubar_play:
                    close_more();
                    close_search();
                    close_profile();
                    display_video_list();
                    header.setVisibility(View.VISIBLE);
                    //Log.i("TAG", "Item 1 selected");
                    break;
                case R.id.menubar_user:
                    close_more();
                    close_video_list();
                    close_profile();
                    header.setVisibility(View.VISIBLE);
                    //Log.i("TAG", "Item 2 selected");
                    break;
                case R.id.menubar_search:
                    close_more();
                    close_video_list();
                    close_profile();
                    header.setVisibility(View.VISIBLE);
                    display_search();
                    break;
                case R.id.menubar_more:
                    close_profile();
                    close_search();
                    close_video_list();
                    display_more();
                    header.setVisibility(View.GONE);
                    break;

            }
        }
}

