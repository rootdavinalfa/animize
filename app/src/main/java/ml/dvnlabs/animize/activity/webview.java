package ml.dvnlabs.animize.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wang.avi.AVLoadingIndicatorView;

import ml.dvnlabs.animize.R;

public class webview extends AppCompatActivity {
    private String bannerurl;
    private AVLoadingIndicatorView loading;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initalize();
        Intent intent = getIntent();
        if(getIntent().getStringExtra("url") != null){
            bannerurl = intent.getStringExtra("url");
            Log.e("URL:",bannerurl);
            //intent.removeExtra("id_anim");
        }



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
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
            }
        });
        webView.loadUrl(bannerurl);
    }
    private void initalize(){
        webView = findViewById(R.id.animize_webview);
        loading = findViewById(R.id.animize_webview_loading);
        loading.setElevation(10);
    }
}
