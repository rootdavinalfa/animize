package ml.dvnlabs.animize.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import androidx.appcompat.app.AppCompatActivity;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;

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

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    private String id_users,name_users,emails;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    RelativeLayout container;
    AnimationDrawable anim;
    private EditText email_tf;
    private EditText password_tf;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Handler handler;

    LoginInternalDBHelper loginInternalDBHelper;
    String tokeen;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializa();


    }

private void initializa(){
    loginInternalDBHelper = new LoginInternalDBHelper(this);
    if(!loginInternalDBHelper.getUserCount()){
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


        setContentView(R.layout.activity_main);

        container = (RelativeLayout) findViewById(R.id.mainScreen);
        avLoadingIndicatorView = (AVLoadingIndicatorView)findViewById(R.id.loading_login);
        avLoadingIndicatorView.hide();
        email_tf = (EditText)findViewById(R.id.login_userfield);
        password_tf = (EditText)findViewById(R.id.login_passwordfield);
    }
    if(loginInternalDBHelper.getUserCount()){
        System.out.println("OnCREATE DB");
        SqliteLoginBackground sqliteLoginBackground = new SqliteLoginBackground();
        //Params 0 = login_user/add_user
        //params 1 - 4 = id_user,name_user,email,token
        sqliteLoginBackground.execute("login_user",null,null,null,null);
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
    public void btn_login_action(View view){
            System.out.println("BTN clicked");
            login_step1();
    }
    private void login_step1(){
        try{
            String url = Api.url_loginuser;
            HashMap<String,String> params = new HashMap<>();
            params.put("email",email_tf.getText().toString().trim());
            params.put("password",password_tf.getText().toString().trim());
            APINetworkRequest apiNetworkRequest = new APINetworkRequest(this,fetchLoginListenerStep1,url,CODE_POST_REQUEST,params);

        }catch (Exception e){
            Log.e(TAG,String.valueOf(e));
        }
    }

    FetchDataListener fetchLoginListenerStep1 = new FetchDataListener() {
        String tok;
        @Override
        public void onFetchComplete(String data) {
            avLoadingIndicatorView.hide();
            Log.e(TAG,data);
            json_login(data);

        }

        @Override
        public void onFetchFailure(String msg) {
            avLoadingIndicatorView.hide();
            error_auth();
            Log.e(TAG,msg);
        }

        @Override
        public void onFetchStart() {
            avLoadingIndicatorView.show();
        }
    };
private void error_auth(){
    Toast toast = Toast.makeText(this,"Email/Password wrong!",Toast.LENGTH_SHORT);
    toast.show();

}


    private void json_login(String data){
        try{
            String token;
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                token = object.getString("jwt");
                tokeen = token;
                login_step2();
                //Intent intent = new Intent(MainActivity.this,dashboard_activity.class);
                //intent.putExtra("token",token);
                //startActivity(intent);
            }
            else{
                Toast toast = Toast.makeText(this,"Please Enter required field!",Toast.LENGTH_SHORT);
                toast.show();

            }
        }catch (JSONException e){
            Log.e(TAG,e.getMessage());
        }

    }



    public void btn_regist_action(View view){
        Intent intent = new Intent(this,registerActivity.class);
        startActivity(intent);
    }
    private void nest_stepcaller(){

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
        initializa();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }
    private void login_step2(){
        try{
            String url = Api.url_decode_login;
            HashMap<String,String> params = new HashMap<>();
            params.put("token",tokeen);
            APINetworkRequest apiNetworkRequest = new APINetworkRequest(this,fetchLoginListenerStep2,url,CODE_POST_REQUEST,params);

        }catch (Exception e){
            Log.e(TAG,String.valueOf(e));
        }
    }
    FetchDataListener fetchLoginListenerStep2 = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            avLoadingIndicatorView.hide();
            Log.e(TAG,data);
            login_decode(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            avLoadingIndicatorView.hide();
            Log.e(TAG,msg);
            error_auth();
            //need_retry_firstuse = true;
            //first_use();
        }

        @Override
        public void onFetchStart() {

        }
    };

    private void login_decode(String st){
        try{
            System.out.println("LOGIN DECODE:"+st);
            JSONObject str = new JSONObject(st);
            if(!str.getBoolean("error")){
                JSONObject parse = str.getJSONObject("parse");
                JSONObject data = parse.getJSONObject("data");
                id_users = data.getString("id_user");
                name_users = data.getString("name_user");
                emails = data.getString("email");
                System.out.println("IU:"+id_users+" NU:"+name_users+" EM:"+emails+"TOKEN:"+tokeen);

                MainActivity.SqliteLoginBackground sqliteLoginBackground = new MainActivity.SqliteLoginBackground();
                sqliteLoginBackground.execute("add_user",id_users,name_users,emails,tokeen);
            }


        }catch (JSONException e){
            Log.e("EXCEPTION JSON:",String.valueOf(e));
        }
    }


    public class SqliteLoginBackground extends AsyncTask<String,Void,userland> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected userland doInBackground(String... params){
            String method = params[0];
            System.out.println("Load DB");
            if(method.equals("login_user")){
                System.out.println("DB login_user");
                //userland usl = new userland();
                //usl = loginInternalDBHelper.getUser();
                //id_users = usl.getIdUser();
                //name_users = usl.getNameUser();
                //emails = usl.getEmail();
                //tokeen = usl.getToken();
                return loginInternalDBHelper.getUser();
            }
            if(method.equals("add_user")){
                System.out.println("DB add_user");
                String ids = params[1];
                String nm = params[2];
                String em = params[3];
                String tok = params[4];
                System.out.println(ids+nm+em+tok);

                loginInternalDBHelper.insertuser(tok,ids,em,nm);
                userland usl = new userland();
                usl = loginInternalDBHelper.getUser();
                //id_users = usl.getIdUser();
                //name_users = usl.getNameUser();
                //emails = usl.getEmail();
                //tokeen = usl.getToken();
                return loginInternalDBHelper.getUser();
            }
            return null;

        }
        @Override
        protected void onPostExecute(userland usl){
            /*
            id_users = usl.getIdUser();
            name_users = usl.getNameUser();
            emails = usl.getEmail();
            tokeen = usl.getToken();

            intent.putExtra("id_user",id_users);
            intent.putExtra("name_user",name_users);
            intent.putExtra("email",emails);
            intent.putExtra("token",tokeen);
            startActivity(intent);*/
            Intent intent = new Intent(MainActivity.this, dashboard_activity.class);
            //intent.putExtra("token",tokeen);
            startActivity(intent);

        }
    }

}
