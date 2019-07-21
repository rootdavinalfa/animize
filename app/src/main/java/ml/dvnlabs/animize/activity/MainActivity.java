package ml.dvnlabs.animize.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.InitInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;
import ml.dvnlabs.animize.pager.inter_pager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    private String id_users,name_users,emails;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    RelativeLayout container;
    private EditText email_tf;
    private EditText password_tf;
    private TabLayout tabs;
    private ViewPager pager;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Handler handler;

    InitInternalDBHelper initInternalDBHelper;
    String tokeen;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializa();
        Log.e("INITIALIZE:","dvnlabs.ml 2019,Animize Loader.");
        Log.e("MESSAGE:","このプログラムは自己資金によるプログラムです。" +
                "\nこのプログラムでは、どちらのパッチ広告もクラックしないでください。" +
                "\nご愛顧いただければ、サービスの拡大につながる可能性があります。");
        Log.e("ENGLISH:","this program is self funded program." +
                "\nPlease not to crack either patching ads in this program." +
                "\nYour patronage may lead us to expanding the service");


    }

    private void initializa(){
        initInternalDBHelper = new InitInternalDBHelper(this);

        //Check is internal DB user exist or not,if exist dont initialize view for mainActivity
        if(!initInternalDBHelper.getUserCount()){
            System.out.println("OnCREATE NULL DB");
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setWindowFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );


            setContentView(R.layout.user_init);
            tabs = findViewById(R.id.inter_tablayout);
            pager = findViewById(R.id.user_inter_pager);
            tabs.setupWithViewPager(pager);
            inter_pager adapter = new inter_pager(getSupportFragmentManager(),tabs.getTabCount(),this);
            pager.setAdapter(adapter);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        } else{
            System.out.println("OnCREATE DB");
            Intent intent = new Intent(MainActivity.this, dashboard_activity.class);
            startActivity(intent);
        }
    }

    public static void setWindowFlag(Activity activity,final int bits,boolean on){
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if(on){
            winParams.flags |= bits;
        }else{
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
