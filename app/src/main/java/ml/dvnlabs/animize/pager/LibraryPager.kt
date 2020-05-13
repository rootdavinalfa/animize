/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.ui.fragment.library_n.Download
import ml.dvnlabs.animize.ui.fragment.library_n.Recent
import ml.dvnlabs.animize.ui.fragment.library_n.Star

class LibraryPager(fm: FragmentManager?, private val numOfTabss: Int, private val mContext: Context) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return numOfTabss
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Star()
            1 -> Recent()
            2 -> Download()
            else -> Star()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return mContext.getString(R.string.tab_star)
            1 -> return mContext.getString(R.string.tab_recent)
            2 -> return mContext.getString(R.string.tab_download)
        }
        return super.getPageTitle(position)
    }

}