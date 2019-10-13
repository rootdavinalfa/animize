package ml.dvnlabs.animize.app;

import android.app.Application;
import android.content.Context;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;


import java.io.File;
import java.io.IOException;


import io.branch.referral.Branch;
import ml.dvnlabs.animize.checker.checkNetwork;

public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();
    private static SimpleCache sDownloadCache;
    public static long max_cache_size = 1024*1024*40;



    private static AppController mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // Initialize the Branch object
        Branch.getAutoInstance(this);
    }

    public static synchronized AppController getInstance() {
        if(mInstance == null){
            mInstance = new AppController();
        }
        return mInstance;
    }
    public static boolean isDebug(Context context){
        //Check is PACKAGE_NAME is debug or not,if not return false;otherwise return true if release version
        return context.getPackageName().equals("ml.dvnlabs.animize.ima.debug");
    }


    public static SimpleCache setVideoCache() {
        if (sDownloadCache == null){
            sDownloadCache = new SimpleCache(new File(mInstance.getCacheDir(), "anim"), new LeastRecentlyUsedCacheEvictor(max_cache_size));//17MB
        }
        return sDownloadCache;
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
