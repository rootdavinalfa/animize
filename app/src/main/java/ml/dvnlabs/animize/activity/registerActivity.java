package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.loader.register_loader;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static ml.dvnlabs.animize.activity.MainActivity.setWindowFlag;

public class registerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private  EditText reg_name;
    private  EditText reg_email;
    private  EditText reg_password;
    private  CheckBox reg_tos_ok;
    private  Button btn_regist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_name = (EditText)findViewById(R.id.regist_name_tf);
        reg_email = (EditText)findViewById(R.id.regist_email_tf);
        reg_password = (EditText)findViewById(R.id.regist_password_tf);
        reg_tos_ok = (CheckBox)findViewById(R.id.checkbox_regist_tos_ok);
        btn_regist = (Button)findViewById(R.id.btn_register);

    }

    public void btn_register_action(View view){
        if(reg_tos_ok.isChecked()){
            if(reg_name.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(reg_password.getText().toString().isEmpty() || reg_password.getText().length() < 8){
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter password,and at least have 8 digit!",Toast.LENGTH_LONG);
                toast.show();
            }
            if(reg_email.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter email!",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(reg_email.getText().toString().isEmpty() && reg_password.getText().toString().isEmpty() && reg_name.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter all",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(!reg_email.getText().toString().isEmpty()&&reg_password.getText().length()>=8 && !reg_password.getText().toString().isEmpty() && !reg_name.getText().toString().isEmpty()){
                getSupportLoaderManager().restartLoader(0,null,this);
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"Please read T.O.S carefully,and then check the box!",Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    public Loader<String> onCreateLoader(int id,Bundle bundle){
        HashMap<String,String> params = new HashMap<>();
        params.put("name_user",reg_name.getText().toString().trim());
        params.put("email",reg_email.getText().toString().trim());
        params.put("password",reg_password.getText().toString().trim());
        String url = Api.url_createuser;
        return new register_loader(this,url,params,CODE_POST_REQUEST);

    }
    @Override
    public void onLoadFinished(Loader<String>loader,String data){
        try{
            JSONObject object = new JSONObject(data);
            if(!object.getBoolean("error")){
                reg_name.setText(null);
                reg_email.setText(null);
                reg_password.setText(null);
                reg_tos_ok.setChecked(false);
                Toast toast = Toast.makeText(getApplicationContext(),"Register OK!",Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (JSONException e){
            e.printStackTrace();

        }


    }
    @Override
    public void onLoaderReset(Loader<String> loader){

    }
}
