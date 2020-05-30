/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.wang.avi.AVLoadingIndicatorView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.network.APINetworkRequest
import ml.dvnlabs.animize.driver.util.network.listener.FetchDataListener
import ml.dvnlabs.animize.ui.activity.MainActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Register : DialogFragment() {
    private var reg_name: TextInputEditText? = null
    private var reg_email: TextInputEditText? = null
    private var reg_password: TextInputEditText? = null
    private var tos_btn: TextView? = null
    private var reg_tos_ok: CheckBox? = null
    private var btn_regist: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_inter_register, container, false)
        reg_name = view.findViewById(R.id.regist_name_tf)
        reg_email = view.findViewById(R.id.regist_email_tf)
        reg_password = view.findViewById(R.id.regist_password_tf)
        reg_tos_ok = view.findViewById(R.id.checkbox_regist_tos_ok)
        btn_regist = view.findViewById(R.id.btn_register)
        tos_btn = view.findViewById(R.id.tos_btn)
        btn_regist!!.setOnClickListener { registerAction() }
        tos_btn!!.setOnClickListener { tosPopUp() }
        return view
    }

    @SuppressLint("InflateParams", "SetJavaScriptEnabled")
    private fun tosPopUp() {
        val loading: AVLoadingIndicatorView
        val close: ImageView
        val header: TextView
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_webview, null)
        val webView = dialogView.findViewById<WebView>(R.id.animize_webview)
        webView.loadUrl("https://dvnlabs.xyz/animize/tos.php")
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        loading = dialogView.findViewById(R.id.animize_webview_loading)
        header = dialogView.findViewById(R.id.webview_title_head)
        header.text = "Term Of Service"
        close = dialogView.findViewById(R.id.informa_close)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                loading.show()
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loading.hide()
                Log.e("URL FINISH:", url)
            }
        }
        dialogBuilder.setView(dialogView)
        val markerPopUpDialog: Dialog = dialogBuilder.create()
        markerPopUpDialog.show()
        close.setOnClickListener { markerPopUpDialog.dismiss() }
    }

    private fun registerAction() {
        if (reg_tos_ok!!.isChecked) {
            if (reg_name!!.text.toString().isEmpty()) {
                val toast = Toast.makeText(activity, "Please enter Name!", Toast.LENGTH_SHORT)
                toast.show()
            }
            if (reg_password!!.text.toString().isEmpty() || reg_password!!.text!!.length < 8) {
                val toast = Toast.makeText(context, "Please enter password,and at least have 8 digit!", Toast.LENGTH_LONG)
                toast.show()
            }
            if (reg_email!!.text.toString().isEmpty()) {
                val toast = Toast.makeText(context, "Please enter email!", Toast.LENGTH_SHORT)
                toast.show()
            }
            if (reg_email!!.text.toString().isEmpty() && reg_password!!.text.toString().isEmpty() && reg_name!!.text.toString().isEmpty()) {
                val toast = Toast.makeText(context, "Please enter all", Toast.LENGTH_SHORT)
                toast.show()
            }
            if (!reg_email!!.text.toString().isEmpty() && reg_password!!.text!!.length >= 8 && !reg_password!!.text.toString().isEmpty() && !reg_name!!.text.toString().isEmpty()) {
                registeruser()
            }
        } else {
            val toast = Toast.makeText(activity, "Please read T.O.S carefully,and then check the box!", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    private fun registeruser() {
        val params = HashMap<String, String>()
        params["name_user"] = reg_name!!.text.toString().trim { it <= ' ' }
        params["email"] = reg_email!!.text.toString().trim { it <= ' ' }
        params["password"] = reg_password!!.text.toString().trim { it <= ' ' }
        val url = Api.url_createuser
        APINetworkRequest(requireContext(), registeruser, url, CODE_POST_REQUEST, params)
    }

    var registeruser: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    val email = reg_email!!.text.toString()
                    val password = reg_password!!.text.toString()
                    (Objects.requireNonNull(activity) as MainActivity).autoFillFromRegister(email, password)
                    reg_name!!.text = null
                    reg_email!!.text = null
                    reg_password!!.text = null
                    reg_tos_ok!!.isChecked = false
                    bottomInfo(false, "Registered,click anywhere then login!")
                    dismiss()
                } else {
                    val toast = Toast.makeText(activity, `object`.getString("message"), Toast.LENGTH_LONG)
                    toast.show()
                    bottomInfo(true, `object`.getString("message"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            Log.e("ERROR:", msg!!)
            bottomInfo(true, msg)
        }

        override fun onFetchStart() {
            bottomInfo(false, "Registering...")
        }
    }

    @SuppressLint("InflateParams")
    private fun bottomInfo(error: Boolean, msg: String) {
        val dialog = BottomSheetDialog(Objects.requireNonNull(requireActivity()))
        val sheetView = requireActivity().layoutInflater.inflate(R.layout.bottom_info, null)
        dialog.setContentView(sheetView)
        dialog.show()
        val errors = sheetView.findViewById<ImageView>(R.id.bottom_info_error)
        val loading: AVLoadingIndicatorView = sheetView.findViewById(R.id.bottom_info_loading)
        val message = sheetView.findViewById<TextView>(R.id.bottom_info_message)
        if (error) {
            errors.visibility = View.VISIBLE
            loading.visibility = View.GONE
            loading.hide()
        } else {
            errors.visibility = View.GONE
            loading.visibility = View.VISIBLE
            loading.show()
        }
        message.text = msg
        val handler = Handler()
        handler.postDelayed({ dialog.dismiss() }, 3000)
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val CODE_POST_REQUEST = 1025
    }
}