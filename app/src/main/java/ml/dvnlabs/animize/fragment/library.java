package ml.dvnlabs.animize.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.pager.library_pager;

public class library extends Fragment {
    private TabLayout tabLayout;
    private ViewPager pager;
    public library(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_library,container,false);
        tabLayout = view.findViewById(R.id.library_tablayout);
        pager = view.findViewById(R.id.library_pager);
        initialize();

        return view;
    }
    private void initialize(){
        tabLayout.setupWithViewPager(pager);
        library_pager adapter = new library_pager(getChildFragmentManager(),tabLayout.getTabCount(),getContext());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

}
