package ml.dvnlabs.animize.pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.tabs.animplay.details;
import ml.dvnlabs.animize.fragment.tabs.animplay.more;

public class aplay_viewpageradapter extends FragmentPagerAdapter {

    private int numOfTabss;
    private Context mContext;
    public aplay_viewpageradapter(FragmentManager fm, int numOfTabs,Context context){
        super(fm);
        this.numOfTabss = numOfTabs;
        this.mContext = context;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new details();
            case 1:
                return new more();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfTabss;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.pager_title_details);
            case 1:
                return mContext.getString(R.string.pager_title_more);
            default:
                return null;
        }
    }
}
