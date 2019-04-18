package ml.dvnlabs.animize.fragment.tabs.animplay;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ml.dvnlabs.animize.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class more extends Fragment {


    public more() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.ap_fragment_more_tabs, container, false);
        return view;
    }

}
