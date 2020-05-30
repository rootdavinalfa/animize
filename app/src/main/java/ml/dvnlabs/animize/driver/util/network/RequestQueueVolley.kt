/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.driver.util.network

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class RequestQueueVolley(private val mContext: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(mContext.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>,TAG : String) {
        req.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        getRequestQueue()!!.add(req).tag = TAG
    }

    fun clearCache() {
        requestQueue!!.cache.clear()
    }

    fun removeCache(key: String?) {
        requestQueue!!.cache.remove(key)
    }

    fun cancelRequest() {
        Log.i("RequestQueue:: ","Clearing request")
        requestQueue!!.cancelAll("NewReq")
    }

    /**
     * cancelRequestByTag is canceller for any request with specific TAG you entered
     * when making request on APINetworkRequest
     *
     * [TAG] This parameter can take form in List or String. Considerate using listOf
     * if you're using kotlin
     */
    fun cancelRequestByTAG(TAG : Any){
        if (TAG is List<*>){
            for (tags in TAG){
                Log.i("RequestQueue:Canceler: ","Clearing request with TAG: $tags")
                requestQueue!!.cancelAll(tags)
            }
        }else if (TAG is String){
            Log.i("RequestQueue:Canceler: ","Clearing request with TAG: $TAG")
            requestQueue!!.cancelAll(TAG)
        }

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