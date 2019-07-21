package ml.dvnlabs.animize.pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.fragment.inter.login;
import ml.dvnlabs.animize.fragment.inter.register;

public class inter_pager  extends FragmentPagerAdapter {
    private int numOfTabss;
    private Context mContext;

    public inter_pager(FragmentManager fm, int count, Context context){
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
                return new login();
            case 1:
                return new register();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.btn_login);
            case 1:
                return mContext.getString(R.string.btn_register);
        }
        return super.getPageTitle(position);
    }
}