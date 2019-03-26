package ml.dvnlabs.animize.driver.util;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueVolley {
    private static RequestQueueVolley mInstance;
    private RequestQueue requestQueue;
    private static Context mContex;

    private RequestQueueVolley(Context context){
        mContex = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized RequestQueueVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueueVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(mContex.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }
    public void clearCache() {
        requestQueue.getCache().clear();
    }

    public void removeCache(String key) {
        requestQueue.getCache().remove(key);
    }
}
