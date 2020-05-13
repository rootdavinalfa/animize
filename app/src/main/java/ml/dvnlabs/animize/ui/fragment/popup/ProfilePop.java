/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.popup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.database.InitInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;
import ml.dvnlabs.animize.ui.activity.MainActivity;

public class ProfilePop extends BottomSheetDialogFragment {
    private InitInternalDBHelper initInternalDBHelper;
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


        initInternalDBHelper = new InitInternalDBHelper(getContext());
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

            return initInternalDBHelper.signOut();

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

            return initInternalDBHelper.getUser();

        }

        @Override
        protected void onPostExecute(userland usl){
            textemail.setText(usl.getEmail());
            textname.setText(usl.getNameUser());

        }
    }

}
