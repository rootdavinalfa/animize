package ml.dvnlabs.animize.pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import ml.dvnlabs.animize.fragment.dashboard.genre;
import ml.dvnlabs.animize.fragment.dashboard.home;

public class dashboard_pager extends FragmentPagerAdapter {

    private int numOfTabss;
    private Context mContext;
    public dashboard_pager(FragmentManager fm, int numOfTabs, Context context){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabss = numOfTabs;
        this.mContext = context;

    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new home();
            case 1:
                return new genre();
            default:
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "For U";
            case 1:
                return "Genre";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return numOfTabss;
    }
}
