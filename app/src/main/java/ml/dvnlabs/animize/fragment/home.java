package ml.dvnlabs.animize.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;

import ml.dvnlabs.animize.R;

public class home extends Fragment {
    public home(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        //getLoaderManager().initLoader(0,null,video_list.this);
    }
    public static home newInstance(){
        return new home();

    }
}
