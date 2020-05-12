/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.pager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.ui.fragment.library_n.Download;
import ml.dvnlabs.animize.ui.fragment.library_n.Recent;
import ml.dvnlabs.animize.ui.fragment.library_n.Star;

public class LibraryPager extends FragmentPagerAdapter {
    private int numOfTabss;
    private Context mContext;

    public LibraryPager(FragmentManager fm, int count, Context context) {
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
                return new Star();
            case 1:
                return new Recent();
            case 2:
                return new Download();
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
