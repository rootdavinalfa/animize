package ml.dvnlabs.animize.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wang.avi.AVLoadingIndicatorView
import ml.dvnlabs.animize.R

public class webview : AppCompatActivity(){
    private var bannerurl : String = ""
    private var loading: AVLoadingIndicatorView? = null
    private var webView: WebView? = null
    private var close: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
            this.window.statusBarColor = getColor(R.color.colorAccent)
        }
        initialize()
        val intent = intent
        if (intent.getStringExtra("url") != null) {
            bannerurl = intent.getStringExtra("url")
            Log.e("URL:", bannerurl)
            //intent.removeExtra("id_anim");
        }
        webView?.settings?.domStorageEnabled=true
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.loadWithOverviewMode = true
        webView?.settings?.useWideViewPort = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView?.setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //progDailog.show();
                loading?.show()

                view.loadUrl(url)

                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loading?.hide()
                Log.e("URL FINISH:", url)
            }
        })
        if (!bannerurl.startsWith("http://")) {
            bannerurl = "http://$bannerurl"
        }
        Log.e("URL WEB:", bannerurl)
        webView?.loadUrl(bannerurl)


    }
    fun initialize(){
        webView = findViewById(R.id.animize_webview)
        loading = findViewById(R.id.animize_webview_loading)
        close = findViewById(R.id.informa_close)
        close?.setOnClickListener(View.OnClickListener { onBackPressed() })
        loading?.setElevation(10f)

    }

}