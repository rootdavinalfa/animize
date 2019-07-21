package ml.dvnlabs.animize.fragment.inter;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.driver.Api;
import ml.dvnlabs.animize.driver.util.APINetworkRequest;
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;

public class register extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private TextInputEditText reg_name;
    private  TextInputEditText reg_email;
    private  TextInputEditText reg_password;
    private TextView tos_btn;
    private CheckBox reg_tos_ok;
    private Button btn_regist;
    public register(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_inter_register,container,false);
        reg_name = view.findViewById(R.id.regist_name_tf);
        reg_email = view.findViewById(R.id.regist_email_tf);
        reg_password = view.findViewById(R.id.regist_password_tf);
        reg_tos_ok = view.findViewById(R.id.checkbox_regist_tos_ok);
        btn_regist = view.findViewById(R.id.btn_register);
        tos_btn = view.findViewById(R.id.tos_btn);
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_register_action();
            }
        });
        tos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tos_popup();
            }
        });
        return view;
    }
    private void tos_popup(){
        AVLoadingIndicatorView loading;
        ImageView close;
        TextView header;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_webview, null);
        WebView webView = dialogView.findViewById(R.id.animize_webview);
        webView.loadUrl("http://dvnlabs.ml/animize/tos.html");
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        loading = dialogView.findViewById(R.id.animize_webview_loading);
        header = dialogView.findViewById(R.id.webview_title_head);
        header.setText("Term Of Service");
        close = dialogView.findViewById(R.id.informa_close);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loading.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                loading.hide();
                Log.e("URL FINISH:",url);
            }
        });

        dialogBuilder.setView(dialogView);
        Dialog markerPopUpDialog = dialogBuilder.create();
        markerPopUpDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerPopUpDialog.dismiss();
            }
        });
    }
    public void btn_register_action(){
        if(reg_tos_ok.isChecked()){
            if(reg_name.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getActivity(),"Please enter Name!",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(reg_password.getText().toString().isEmpty() || reg_password.getText().length() < 8){
                Toast toast = Toast.makeText(getContext(),"Please enter password,and at least have 8 digit!",Toast.LENGTH_LONG);
                toast.show();
            }
            if(reg_email.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getContext(),"Please enter email!",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(reg_email.getText().toString().isEmpty() && reg_password.getText().toString().isEmpty() && reg_name.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getContext(),"Please enter all",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(!reg_email.getText().toString().isEmpty()&&reg_password.getText().length()>=8 && !reg_password.getText().toString().isEmpty() && !reg_name.getText().toString().isEmpty()){
                registeruser();
            }

        }else{
            Toast toast = Toast.makeText(getActivity(),"Please read T.O.S carefully,and then check the box!",Toast.LENGTH_LONG);
            toast.show();
        }

    }
    private void registeruser(){
        HashMap<String,String> params = new HashMap<>();
        params.put("name_user",reg_name.getText().toString().trim());
        params.put("email",reg_email.getText().toString().trim());
        params.put("password",reg_password.getText().toString().trim());
        String url = Api.url_createuser;
        APINetworkRequest networkRequest = new APINetworkRequest(getContext(),registeruser,url,CODE_POST_REQUEST,params);
    }

    FetchDataListener registeruser = new FetchDataListener() {
        @Override
        public void onFetchComplete(String data) {
            try{
                JSONObject object = new JSONObject(data);
                if(!object.getBoolean("error")){
                    reg_name.setText(null);
                    reg_email.setText(null);
                    reg_password.setText(null);
                    reg_tos_ok.setChecked(false);
                    Toast toast = Toast.makeText(getActivity(),"Register OK ,Swipe Left to login!",Toast.LENGTH_LONG);
                    toast.show();
                    bottom_info(false,"Registered,click anywhere then login!");
                }
                else{
                    Toast toast = Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_LONG);
                    toast.show();
                    bottom_info(true,object.getString("message"));
                }
            }catch (JSONException e){
                e.printStackTrace();

            }
        }

        @Override
        public void onFetchFailure(String msg) {
            Log.e("ERROR:",msg);
            bottom_info(true,msg);

        }

        @Override
        public void onFetchStart() {
            bottom_info(false,"Registering...");

            //commentedit.setEnabled(false);


        }
    };
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
