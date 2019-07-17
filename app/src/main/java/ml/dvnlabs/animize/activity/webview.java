package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import ml.dvnlabs.animize.R;

public class webview extends AppCompatActivity {
    private String bannerurl;
    private AVLoadingIndicatorView loading;
    private WebView webView;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(getColor(R.color.colorAccent));
        }
        initalize();
        Intent intent = getIntent();
        if(getIntent().getStringExtra("url") != null){
            bannerurl = intent.getStringExtra("url");
            Log.e("URL:",bannerurl);
            //intent.removeExtra("id_anim");
        }



        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //progDailog.show();
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
        if(!bannerurl.startsWith("http://")){
            bannerurl = "http://"+bannerurl;
        }
        Log.e("URL WEB:",bannerurl);
        webView.loadUrl(bannerurl);
    }
    private void initalize(){
        webView = findViewById(R.id.animize_webview);
        loading = findViewById(R.id.animize_webview_loading);
        close = findViewById(R.id.informa_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loading.setElevation(10);
    }
}
