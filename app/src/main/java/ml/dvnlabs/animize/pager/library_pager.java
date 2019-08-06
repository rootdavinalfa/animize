package ml.dvnlabs.animize.pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.library_n.download;
import ml.dvnlabs.animize.fragment.library_n.recent;
import ml.dvnlabs.animize.fragment.library_n.star;

public class library_pager extends FragmentPagerAdapter {
    private int numOfTabss;
    private Context mContext;

    public library_pager(FragmentManager fm, int count, Context context){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mContext =context;
        this.numOfTabss = count;

    }
    @Override
    public int getCount() {
        return numOfTabss;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new star();
            case 1:
                return new recent();
            case 2:
                return new download();
                default:
                    return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.tab_star);
            case 1:
                return mContext.getString(R.string.tab_recent);
            case 2:
                return mContext.getString(R.string.tab_download);

        }
        return super.getPageTitle(position);
    }
}
