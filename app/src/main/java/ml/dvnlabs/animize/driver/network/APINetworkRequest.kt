/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.driver.network

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import java.util.*

class APINetworkRequest(val context: Context, listen: FetchDataListener?, uri: String, REQUEST_CODE: Int, params: HashMap<String, String>?, TAG: String = DEFAULT_TAG) {
    init {
        if (REQUEST_CODE == CODE_GET_REQUEST) {
            getRequest(listen, uri).addStringRequestToQueue(TAG)
        }
        if (REQUEST_CODE == CODE_POST_REQUEST) {
            postRequest(listen, uri, params!!).addStringRequestToQueue(TAG)
        }
    }

    private fun getRequest(listen: FetchDataListener?, url: String): StringRequest {
        listen?.onFetchStart()
        return StringRequest(Request.Method.GET, url,
                Response.Listener { response -> listen?.onFetchComplete(response) }, Response.ErrorListener { error ->
            if (error is NoConnectionError) {
                listen!!.onFetchFailure("Network Connectivity Problem!")
            }
        })
    }

    private fun postRequest(listen: FetchDataListener?, url: String, param: HashMap<String, String>): StringRequest {
        listen?.onFetchStart()
        return object : StringRequest(Method.POST, url,
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
    }

    /**
     * This Method just extension, provide one with method which is returning StringRequest <Volley>
     *
     * [TAG] Provide TAG for new REQUEST,if not provided will use default one
     */
    private fun StringRequest.addStringRequestToQueue(TAG: String) {
        RequestQueueVolley.getInstance(context)!!.addToRequestQueue(this.setShouldCache(false), TAG)
    }

    companion object {
        const val CODE_GET_REQUEST = 1024
        const val CODE_POST_REQUEST = 1025
        const val DEFAULT_TAG = "NewReq"
    }
}