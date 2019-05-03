package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.checker.checkNetwork;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.fragment.home;
import ml.dvnlabs.animize.fragment.lastup_video_list;
import ml.dvnlabs.animize.fragment.search;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;


public class dashboard_activity extends AppCompatActivity implements checkNetwork.ConnectivityReceiverListener{
    private BroadcastReceiver receiver;
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
    private RelativeLayout dashboard;
    private checkNetwork NetworkChecker = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginInternalDBHelper = new LoginInternalDBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        initializes();
        SqliteReadUser sqliteReadUser = new SqliteReadUser();
        sqliteReadUser.execute("OK");
        NetworkChecker = new checkNetwork();
        registerReceiver(NetworkChecker,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        bottomnavlogic();
        dash_serach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_search();
            }
        });
    }
    //inetCheck for check on fragment
    public void inetCheck(){
        boolean isConnect = checkNetwork.isConnected();
        showSnack(isConnect);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        //Log.e("INET:","OK");
        showSnack(isConnected);
    }
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        Snackbar snackbar;
        if(!isConnected){
            message = "Not connected";
            color = Color.RED;
            snackbar = Snackbar
                    .make(findViewById(R.id.DashnavigationView), message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    home hm = (home) getSupportFragmentManager().findFragmentById(R.id.home_fragment);
                    hm.getLast_Up();
                }
            });
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }
    public void initializes(){
        dashboard = (RelativeLayout)findViewById(R.id.dashboard);
        header_layout = (LinearLayout)findViewById(R.id.header);
        dash_serach_btn = (LinearLayout) findViewById(R.id.dash_search);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.DashnavigationView);
        dash_profile_username = (TextView)findViewById(R.id.dash_profile_text);
        display_home();

    }
    public void broadcastIntent() {
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume(){
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        //Intent startServiceIntent = new Intent(this, checkNetwork.class);
        //startService(startServiceIntent);
    }

    @Override
    protected void onStop() {
        //stopService(new Intent(this, checkNetwork.class));
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(NetworkChecker);
        super.onDestroy();
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
        global.addFragment(getSupportFragmentManager(),vl,R.id.home_fragment,"FRAGMENT_HOME","SLIDE");
        header_layout.setVisibility(View.VISIBLE);
    }

    public void display_search(){
        close_home();
        close_lastup();
        search se = search.newInstance();
        global.addFragment(getSupportFragmentManager(),se,R.id.search_fragment,"FRAGMENT_OTHER","SLIDE");
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
        global.addFragment(getSupportFragmentManager(),se,R.id.video_list_fragment,"FRAGMENT_OTHER","SLIDE");
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
