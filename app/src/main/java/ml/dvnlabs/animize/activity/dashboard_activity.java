package ml.dvnlabs.animize.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.app.AppController;
import ml.dvnlabs.animize.checker.checkNetwork;
import ml.dvnlabs.animize.database.InitInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.fragment.global;
import ml.dvnlabs.animize.fragment.lastup_video_list;
import ml.dvnlabs.animize.fragment.library;
import ml.dvnlabs.animize.fragment.popup.ProfilePop;
import ml.dvnlabs.animize.fragment.search;
import ml.dvnlabs.animize.pager.dashboard_pager;


import android.content.BroadcastReceiver;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


public class dashboard_activity extends AppCompatActivity implements checkNetwork.ConnectivityReceiverListener{
    private BroadcastReceiver receiver;
    InitInternalDBHelper initInternalDBHelper;
    private String id_users,name_users,emails;
    String tokeen;
    private TextView dash_profile_username;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    static final String STATE_FRAGMENT = "state_of_fragment";

    private String TAG = dashboard_activity.class.getSimpleName();
    private LinearLayout header_layout;
    private ImageView dash_serach_btn;
    private BottomNavigationView bottomNavigationView;
    private RelativeLayout dashboard,dash_profile;
    private TabLayout dash_tabs;
    private ViewPager dash_pager;

    private checkNetwork NetworkChecker = null;
    private boolean isHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initInternalDBHelper = new InitInternalDBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(getColor(R.color.colorAccent));
        }
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
            message = getResources().getString(R.string.NO_NETWORK);
            color = Color.WHITE;
            snackbar = Snackbar
                    .make(findViewById(R.id.DashnavigationView), message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //home hm = (home) getSupportFragmentManager().findFragmentById(R.id.home_fragment);
                    //hm.getLast_Up();
                    //hm.getLastPackage();
                }
            }).setActionTextColor(color);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }
    public void SnackError(String error,int id){
        int color;
        Snackbar snackbar;

        color = Color.WHITE;
        snackbar = Snackbar
                .make(findViewById(R.id.DashnavigationView), error, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (id){
                    case 1:
                        lastup_video_list hm = (lastup_video_list) getSupportFragmentManager().findFragmentById(R.id.video_list_fragment);
                        if (hm != null && hm.isVisible()){
                            hm.getlist_V();
                        }else {
                            snackbar.dismiss();
                        }

                }

            }
        }).setActionTextColor(color);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();

    }
    public void initializes(){
        //dashboard = (RelativeLayout)findViewById(R.id.dashboard);
        header_layout = (LinearLayout)findViewById(R.id.header);

        //Set pager and tab

        dash_tabs = findViewById(R.id.dash_tab);
        dash_pager = findViewById(R.id.dash_viewpager);
        dash_tabs.setupWithViewPager(dash_pager);
        dashboard_pager adapter = new dashboard_pager(getSupportFragmentManager(),dash_tabs.getTabCount(),this);
        dash_pager.setAdapter(adapter);
        dash_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dash_tabs));
        isHome = true;
        dash_serach_btn =  findViewById(R.id.dash_btn_search);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.DashnavigationView);
        dash_profile_username = (TextView)findViewById(R.id.dash_profile_text);
        dash_profile = (RelativeLayout)findViewById(R.id.dash_profile);
        dash_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePop pop = new ProfilePop();
                pop.show(getSupportFragmentManager(),"profilepop");
            }
        });
        //display_home();

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

    @Override
    public void onBackPressed()
    {
        //header.setVisibility(View.VISIBLE);
        //int count = getSupportFragmentManager().getBackStackEntryCount()-1;
        //Log.e("COUNTED-:",String.valueOf(count));
        if (isHome) {
            super.onBackPressed();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            //additional code
        } else {
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
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

                    case R.id.nav_library:
                        close_home();
                        display_library();
                        return true;

                    case R.id.nav_feed:

                        return true;
                }
                return false;
            }
        });
    }
    public void display_home(){
        isHome = true;
        close_search();
        close_library();
        close_lastup();
        dash_pager.setVisibility(View.VISIBLE);
        header_layout.setVisibility(View.VISIBLE);
    }
    public void close_home(){
        isHome = false;
        dash_pager.setVisibility(View.GONE);
        header_layout.setVisibility(View.GONE);

    }


    public void display_search(){
        close_home();
        close_library();
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
    public void display_library(){
        close_home();
        close_search();
        close_lastup();
        library se = new library();
        global.addFragment(getSupportFragmentManager(),se,R.id.library_fragment,"FRAGMENT_OTHER","NULL");

    }

    public void close_library(){
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        library simpleFragment = (library) fragmentManager
                .findFragmentById(R.id.library_fragment);
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

            return initInternalDBHelper.getUser();

        }

        @Override
        protected void onPostExecute(userland usl){
            dash_profile_username.setText(usl.getNameUser());

        }
    }
}

