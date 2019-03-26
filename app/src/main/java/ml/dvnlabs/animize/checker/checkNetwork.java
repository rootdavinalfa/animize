package ml.dvnlabs.animize.checker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class checkNetwork {
    Context context;
    public void userInit(Context mcontext){
        this.context = mcontext;
    }
    public boolean haveNetwork(){
        boolean WIFI = false;
        boolean MOBILE = false;
        ConnectivityManager connectivityManager =(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    WIFI = true;
                }
            }
            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if(info.isConnected()){
                    MOBILE = true;
                }
            }

        }
        return  WIFI || MOBILE;


    }
}
