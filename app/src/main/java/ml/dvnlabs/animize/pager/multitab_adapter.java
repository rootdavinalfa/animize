package ml.dvnlabs.animize.pager;


import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.tabs.multiview.multiview;

public class multitab_adapter extends FragmentStatePagerAdapter{
    private int numOfTabs;
    private Fragment fragment = null;
    private List<String> pagetitle = new ArrayList<>();
    private Bundle bundle;
    private FragmentManager fragmentManager;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();



    public multitab_adapter(FragmentManager fm, int num,List<String>pagetitl){
        super(fm);
        this.numOfTabs = num;
        this.pagetitle = pagetitl;
        this.fragmentManager = fm;

    }
    @Override
    public Fragment getItem(int position) {
        //System.out.println(" TEST POSIT: "+position+" NUMTABS: "+numOfTabs+" DATA:"+pagetitle.get(position-1));
        for (int i = 0; i < numOfTabs ; i++) {
            //System.out.println("TEST1: "+i+" TEST POSIT: "+position+" NUMTABS: "+numOfTabs);
            if (i == position) {
                bundle = new Bundle();
                fragment = multiview.newInstance();
                bundle.putString("genre",pagetitle.get(i));
                fragment.setArguments(bundle);
                break;
            }

        }


        return fragment;

    }
    @Override
    public int getCount() {
        return numOfTabs;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title = "Default";
        //return super.getPageTitle(position);
        if (!pagetitle.isEmpty()){
            title = pagetitle.get(position);
        }
        //Log.e("TITLE:",title);
        //title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
        return title;
    }
}
