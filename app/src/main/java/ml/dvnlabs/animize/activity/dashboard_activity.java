package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.fragment.home;
import ml.dvnlabs.animize.fragment.lastup_video_list;
import ml.dvnlabs.animize.fragment.search;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class dashboard_activity extends AppCompatActivity   {

    LoginInternalDBHelper loginInternalDBHelper;
    private String id_users,name_users,emails;
    String tokeen;
    private TextView dash_profile_username;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    static final String STATE_FRAGMENT = "state_of_fragment";

    private String TAG = dashboard_activity.class.getSimpleName();
    private LinearLayout header_layout;
    private LinearLayout dash_serach_btn;
    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginInternalDBHelper = new LoginInternalDBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        header_layout = (LinearLayout)findViewById(R.id.header);
        dash_serach_btn = (LinearLayout) findViewById(R.id.dash_search);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.DashnavigationView);
        dash_profile_username = (TextView)findViewById(R.id.dash_profile_text);
        initializes();
        SqliteReadUser sqliteReadUser = new SqliteReadUser();
        sqliteReadUser.execute("OK");
        bottomnavlogic();
        dash_serach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_search();
            }
        });
    }
    public void initializes(){
        display_home();

    }
    public void close_home(){
        header_layout.setVisibility(View.GONE);
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        home simpleFragment = (home) fragmentManager
                .findFragmentById(R.id.home_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        //header.setVisibility(View.VISIBLE);
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

            display_home();
        }
    }
    private void bottomnavlogic(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        display_home();
                        return true;

                    case R.id.nav_genre:
                        close_home();
                        return true;

                    case R.id.nav_package:
                        close_home();
                        return true;
                }
                return false;
            }
        });
    }

    public void display_home(){
        getSupportFragmentManager().popBackStack();
        close_search();
        close_lastup();
        home vl = home.newInstance();
        global.addFragment(getSupportFragmentManager(),vl,R.id.home_fragment,"FRAGMENT_HOME","ZOOM");
        header_layout.setVisibility(View.VISIBLE);
    }

    public void display_search(){
        close_home();
        close_lastup();
        search se = search.newInstance();
        global.addFragment(getSupportFragmentManager(),se,R.id.search_fragment,"FRAGMENT_OTHER","ZOOM");
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
    }

    public void display_lastup(){
        close_home();
        lastup_video_list se = lastup_video_list.newInstance();
        global.addFragment(getSupportFragmentManager(),se,R.id.video_list_fragment,"FRAGMENT_OTHER","ZOOM");
    }

    public void close_lastup(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        lastup_video_list simpleFragment = (lastup_video_list) fragmentManager
                .findFragmentById(R.id.video_list_fragment);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit();
        }
    }

    public class SqliteReadUser extends AsyncTask<String,Void,userland> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected userland doInBackground(String... params){

            return loginInternalDBHelper.getUser();

        }

        @Override
        protected void onPostExecute(userland usl){
            dash_profile_username.setText(usl.getNameUser());

        }
    }
}

