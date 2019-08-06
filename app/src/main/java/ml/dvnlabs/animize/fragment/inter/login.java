package ml.dvnlabs.animize.fragment.inter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.MainActivity;
import ml.dvnlabs.animize.activity.dashboard_activity;
import ml.dvnlabs.animize.database.InitInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;

public class login extends Fragment {
    private String id_users,name_users,emails;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    //RelativeLayout container;
    private TextInputEditText email_tf;
    private TextInputEditText password_tf;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Handler handler;
    private Button btn_login;

    InitInternalDBHelper initInternalDBHelper;
    String tokeen;

    private String TAG = MainActivity.class.getSimpleName();


    public login(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_inter_login,container,false);
        initInternalDBHelper = new InitInternalDBHelper(getActivity());
        btn_login = view.findViewById(R.id.login_signin_btn);
        email_tf = view.findViewById(R.id.login_userfield);
        password_tf =view.findViewById(R.id.login_passwordfield);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login_action();
            }
        });
        return view;
    }

    public void btn_login_action(){
        login_step1();
    }
    private void login_step1(){
        try{
            String url = Api.url_loginuser;
            HashMap<String,String> params = new HashMap<>();
            params.put("email",email_tf.getText().toString().trim());
            params.put("password",password_tf.getText().toString().trim());
            new APINetworkRequest(getActivity(),fetchLoginListenerStep1,url,CODE_POST_REQUEST,params);

        }catch (Exception e){
            Log.e(TAG,String.valueOf(e));
        }
    }

    FetchDataListener fetchLoginListenerStep1 = new FetchDataListener() {
        String tok;
        @Override
        public void onFetchComplete(String data) {
            Log.e(TAG,data);
            json_login(data);

        }

        @Override
        public void onFetchFailure(String msg) {
            error_auth();
            Log.e(TAG,msg);
            bottom_info(true,msg);
        }

        @Override
        public void onFetchStart() {
            bottom_info(false,"Signing in....");
        }
    };
    private void error_auth(){
        Toast toast = Toast.makeText(getActivity(),"Email/Password wrong!",Toast.LENGTH_SHORT);
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
                Toast toast = Toast.makeText(getActivity(),"Please Enter required field!",Toast.LENGTH_SHORT);
                toast.show();

            }
        }catch (JSONException e){
            Log.e(TAG,e.getMessage());
        }

    }

    private void login_step2(){
        try{
            String url = Api.url_decode_login;
            HashMap<String,String> params = new HashMap<>();
            params.put("token",tokeen);
            new APINetworkRequest(getActivity(),fetchLoginListenerStep2,url,CODE_POST_REQUEST,params);

        }catch (Exception e){
            Log.e(TAG,String.valueOf(e));
        }
    }
    FetchDataListener fetchLoginListenerStep2 = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            Log.e(TAG,data);
            login_decode(data);
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e(TAG,msg);
            error_auth();
            bottom_info(true,msg);
            //need_retry_firstuse = true;
            //first_use();
        }

        @Override
        public void onFetchStart() {
            bottom_info(false,"Initializing...");

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
                SqliteLoginBackground sqliteLoginBackground = new SqliteLoginBackground();
                sqliteLoginBackground.execute("add_user",id_users,name_users,emails,tokeen);
            }


        }catch (JSONException e){
            Log.e("EXCEPTION JSON:",String.valueOf(e));
        }
    }


    public class SqliteLoginBackground extends AsyncTask<String,Void, userland> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected userland doInBackground(String... params){
            String method = params[0];
            System.out.println("Load DB");
            if(method.equals("login_user")){
                System.out.println("DB login_user");

                return initInternalDBHelper.getUser();
            }
            if(method.equals("add_user")){
                System.out.println("DB add_user");
                String ids = params[1];
                String nm = params[2];
                String em = params[3];
                String tok = params[4];
                System.out.println(ids+nm+em+tok);

                initInternalDBHelper.insertuser(tok,ids,em,nm);

                return initInternalDBHelper.getUser();
            }
            return null;

        }
        @Override
        protected void onPostExecute(userland usl){

            Intent intent = new Intent(getActivity(), dashboard_activity.class);
            startActivity(intent);

        }
    }
    private void bottom_info(boolean error,String msg){
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_info, null);
        dialog.setContentView(sheetView);
        dialog.show();
        ImageView errors = sheetView.findViewById(R.id.bottom_info_error);
        AVLoadingIndicatorView loading = sheetView.findViewById(R.id.bottom_info_loading);
        TextView message = sheetView.findViewById(R.id.bottom_info_message);
        if (error){
            errors.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            loading.hide();
        }else{
            errors.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            loading.show();
        }
        message.setText(msg);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },3000);

    }
}
