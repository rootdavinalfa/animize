package ml.dvnlabs.animize.fragment.popup;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.MainActivity;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;

public class ProfilePop extends BottomSheetDialogFragment {
    private LoginInternalDBHelper loginInternalDBHelper;
    private TextView textname,textemail;
    private Button btn_logout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.profilepop,container,false);
        view.setClipToOutline(true);
        btn_logout = (Button)view.findViewById(R.id.dash_profile_button_logout);
        textemail = (TextView)view.findViewById(R.id.dash_profile_tv_email);
        textname = (TextView)view.findViewById(R.id.dash_profile_tv_name);


        loginInternalDBHelper = new LoginInternalDBHelper(getContext());
        SqliteReadUser sqliteReadUser = new SqliteReadUser();
        sqliteReadUser.execute("OK");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        return view;
    }


    public void signout(){
        SqliteLoginBackground sqliteLoginBackground = new SqliteLoginBackground();
        sqliteLoginBackground.execute("SIGNOUT");
    }

    public class SqliteLoginBackground extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected String doInBackground(String... params){

            return loginInternalDBHelper.sign_out();

        }

        @Override
        protected void onPostExecute(String usl){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    public class SqliteReadUser extends AsyncTask<String,Void, userland>{
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected userland doInBackground(String... params){

            return loginInternalDBHelper.getUser();

        }

        @Override
        protected void onPostExecute(userland usl){
            textemail.setText(usl.getEmail());
            textname.setText(usl.getNameUser());

        }
    }

}
