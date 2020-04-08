package ml.dvnlabs.animize.driver.util

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import java.util.*

class APINetworkRequest {
    var REQ_CODE = 0

    constructor() {}
    constructor(context: Context, listen: FetchDataListener?, urli: String, REQUEST_CODE: Int,
                params: HashMap<String, String>?) {
        if (REQUEST_CODE == CODE_GET_REQUEST) {
            getRequest(context, listen, urli)
        }
        if (REQUEST_CODE == CODE_POST_REQUEST) {
            postRequest(context, listen, urli, params!!)
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
                //Map<String, String> params = new HashMap<>();
                //params.put("token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9kdm5sYWJzLm1sIiwiYXVkIjoiZGJhc3VkZXdhQGdtYWlsLmNvbSIsImlhdCI6MTU1MjAyOTM0NCwibmJmIjoxNTUyMDI5MzU0LCJkYXRhIjp7ImlkX3VzZXIiOiJVU1IxIiwibmFtZV91c2VyIjoiRGF2aW4iLCJlbWFpbCI6ImRiYXN1ZGV3YUBnbWFpbC5jb20ifX0.-C7rmoi7z_tMePlBFPLVUtObC3OcG9y_0qRQIx1_xrY");
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