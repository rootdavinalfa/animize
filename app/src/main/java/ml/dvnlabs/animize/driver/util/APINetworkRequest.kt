/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.driver.util

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import java.util.*

class APINetworkRequest(context: Context, listen: FetchDataListener?, uri: String, REQUEST_CODE: Int, params: HashMap<String, String>?) {
    init {
        if (REQUEST_CODE == CODE_GET_REQUEST) {
            getRequest(context, listen, uri)
        }
        if (REQUEST_CODE == CODE_POST_REQUEST) {
            postRequest(context, listen, uri, params!!)
        }
    }

    private fun getRequest(context: Context, listen: FetchDataListener?, url: String) {
        listen?.onFetchStart()
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response -> listen?.onFetchComplete(response) }, Response.ErrorListener { error ->
            if (error is NoConnectionError) {
                listen!!.onFetchFailure("Network Connectivity Problem!")
            }
        })
        RequestQueueVolley.getInstance(context)!!.addToRequestQueue(stringRequest.setShouldCache(false))
    }

    private fun postRequest(context: Context, listen: FetchDataListener?, url: String, param: HashMap<String, String>) {
        listen?.onFetchStart()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response -> listen?.onFetchComplete(response) }, Response.ErrorListener { error ->
            if (error is NoConnectionError) {
                listen!!.onFetchFailure("Network Connectivity Problem!")
            } else if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                listen!!.onFetchFailure("BAD AUTH")
            }
        }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return param
            }
        }
        RequestQueueVolley.getInstance(context)!!.addToRequestQueue(stringRequest.setShouldCache(false))
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val CODE_POST_REQUEST = 1025
    }
}