/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.wang.avi.AVLoadingIndicatorView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.base.BaseActivity

class WebView : BaseActivity() {
    private var bannerURL : String = ""
    private var loading: AVLoadingIndicatorView? = null
    private var webView: WebView? = null
    private var close: ImageView? = null


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        changeStatusBar(this,R.color.colorPrimaryDark,false)
        initialize()
        val intent = intent
        if (intent.getStringExtra("url") != null) {
            bannerURL = intent.getStringExtra("url")!!
            //intent.removeExtra("id_anim");
        }
        webView?.settings?.domStorageEnabled=true
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.loadWithOverviewMode = true
        webView?.settings?.useWideViewPort = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView?.webViewClient = object : WebViewClient() {

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
        }
        if (!bannerURL.startsWith("http://")) {
            bannerURL = "http://$bannerURL"
        }
        Log.e("URL WEB:", bannerURL)
        webView?.loadUrl(bannerURL)


    }
    fun initialize(){
        webView = findViewById(R.id.animize_webview)
        loading = findViewById(R.id.animize_webview_loading)
        close = findViewById(R.id.informa_close)
        close?.setOnClickListener(View.OnClickListener { onBackPressed() })
        loading?.elevation = 10f

    }

}