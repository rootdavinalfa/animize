package ml.dvnlabs.animize.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ml.dvnlabs.animize.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class animize extends Fragment {


    public animize() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_animize,container,false);
        return view;
    }

    public static animize newInstance(){
        return new animize();
    }

}
