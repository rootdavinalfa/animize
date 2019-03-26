package ml.dvnlabs.animize.loader;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import androidx.loader.content.AsyncTaskLoader;
import ml.dvnlabs.animize.driver.RequestHandler;

public class animlist_loader extends AsyncTaskLoader<String> {

    String url;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    HashMap<String, String> params;
    String a;
    int requestCode;
    public animlist_loader(Context context, String url, HashMap<String, String> params, int reqcode){
        super(context);
        this.url = url;
        this.params = params;
        this.requestCode = reqcode;

    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public String loadInBackground(){
        RequestHandler requestHandler = new RequestHandler();
        if (requestCode == CODE_POST_REQUEST){
            return requestHandler.sendPostRequest(url, params);
        }


        if (requestCode == CODE_GET_REQUEST){
            //a = requestHandler.sendGetRequest(url);
            //Log.e("INFO:",a);
            return requestHandler.sendGetRequest(url);
        }

        return null;
    }

}
