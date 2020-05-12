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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class RequestQueueVolley(private val mContex: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(mContex.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        getRequestQueue()!!.add(req).tag = "NewReq"
    }

    fun clearCache() {
        requestQueue!!.cache.clear()
    }

    fun removeCache(key: String?) {
        requestQueue!!.cache.remove(key)
    }

    fun clearRequest() {
        requestQueue!!.cancelAll("NewReq")
    }

    companion object {
        private var mInstance: RequestQueueVolley? = null

        @Synchronized
        fun getInstance(context: Context): RequestQueueVolley? {
            if (mInstance == null) {
                mInstance = RequestQueueVolley(context)
            }
            return mInstance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}