package ml.dvnlabs.animize.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.MainActivity;
import ml.dvnlabs.animize.database.LoginInternalDBHelper;
import ml.dvnlabs.animize.database.model.userland;

/**
 * A simple {@link Fragment} subclass.
 */
public class profile extends Fragment {
    private TextView textid,textname,textemail;
    private LoginInternalDBHelper loginInternalDBHelper;
    private Button btn_so;
    public profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile,container,false);
        btn_so = (Button)view.findViewById(R.id.btn_logout);
        textemail = (TextView)view.findViewById(R.id.text_prof_email);
        textid = (TextView)view.findViewById(R.id.text_prof_id);
        textname = (TextView)view.findViewById(R.id.text_prof_name);
        loginInternalDBHelper = new LoginInternalDBHelper(getContext());
        SqliteReadUser sqliteReadUser = new SqliteReadUser();
        sqliteReadUser.execute("OK");
        btn_so.setOnClickListener(new View.OnClickListener() {
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
    public static profile newInstance(){
        return new profile();
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
    public class SqliteReadUser extends AsyncTask<String,Void,userland>{
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
            textid.setText(usl.getIdUser());
            textname.setText(usl.getNameUser());

        }
    }
}
