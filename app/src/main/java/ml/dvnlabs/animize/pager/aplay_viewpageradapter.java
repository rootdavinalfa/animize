package ml.dvnlabs.animize.pager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import ml.dvnlabs.animize.fragment.tabs.animplay.details;
import ml.dvnlabs.animize.fragment.tabs.animplay.more;

public class aplay_viewpageradapter extends FragmentPagerAdapter {

    private int numOfTabss;
    public aplay_viewpageradapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabss = numOfTabs;
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
                return "Details";
            case 1:
                return "More";
            default:
                return null;
        }
    }
}
