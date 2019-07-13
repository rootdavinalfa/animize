package ml.dvnlabs.animize.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.io.IOException;


import ml.dvnlabs.animize.checker.checkNetwork;

public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static SimpleCache sDownloadCache;




    private static AppController mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        if(mInstance == null){
            mInstance = new AppController();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }

    public static SimpleCache setCache() {
        if (sDownloadCache == null){
            sDownloadCache = new SimpleCache(new File(mInstance.getCacheDir(), "anim"), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 17));//17MB
        }
        return sDownloadCache;
    }


    public static void cleanVideoCacheDir(Context context) throws IOException {
        File videoCacheDir = getVideoCacheDir(context);
        cleanDirectory(videoCacheDir);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            cleanDirectory(file);
            deleteOrThrow(file);
        }
    }

    private static void deleteOrThrow(File file) throws IOException {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }
    public void setConnectivityListener(checkNetwork.ConnectivityReceiverListener listener) {
        checkNetwork.connectivityReceiverListener = listener;
    }
}
