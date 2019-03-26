package ml.dvnlabs.animize.driver.util;

import android.content.Context;
import android.webkit.HttpAuthHandler;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import ml.dvnlabs.animize.driver.util.listener.FetchDataListener;

public class APINetworkRequest {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    int REQ_CODE;


    public APINetworkRequest(final Context context,final FetchDataListener listen,final String urli,int REQUEST_CODE,
                             HashMap<String,String> params){

        if(REQUEST_CODE == CODE_GET_REQUEST){
            GET_REQUEST(context,listen,urli);
        }
        if(REQUEST_CODE == CODE_POST_REQUEST){
            POST_REQUEST(context,listen,urli,params);
        }
    }

    private void GET_REQUEST(final Context context,final FetchDataListener listen,final String url){
        if(listen !=null){
            listen.onFetchStart();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listen != null) {
                            listen.onFetchComplete(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    listen.onFetchFailure("Network Connectivity Problem!");

                }
            }
        });
        RequestQueueVolley.getInstance(context).addToRequestQueue(stringRequest.setShouldCache(false));
    }
    private void POST_REQUEST(final Context context, final FetchDataListener listen, final String url, HashMap<String,String> param){
        if(listen !=null){
            listen.onFetchStart();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (listen != null) {
                            listen.onFetchComplete(response);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    listen.onFetchFailure("Network Connectivity Problem!");

                }else if (error.networkResponse!=null && error.networkResponse.statusCode == 401){
                    listen.onFetchFailure("BAD AUTH");
                }
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Map<String, String> params = new HashMap<>();
                //params.put("token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9kdm5sYWJzLm1sIiwiYXVkIjoiZGJhc3VkZXdhQGdtYWlsLmNvbSIsImlhdCI6MTU1MjAyOTM0NCwibmJmIjoxNTUyMDI5MzU0LCJkYXRhIjp7ImlkX3VzZXIiOiJVU1IxIiwibmFtZV91c2VyIjoiRGF2aW4iLCJlbWFpbCI6ImRiYXN1ZGV3YUBnbWFpbC5jb20ifX0.-C7rmoi7z_tMePlBFPLVUtObC3OcG9y_0qRQIx1_xrY");
                return param;
            }
        };
        RequestQueueVolley.getInstance(context).addToRequestQueue(stringRequest.setShouldCache(false));

    }


}
