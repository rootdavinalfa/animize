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
import ml.dvnlabs.animize.ui.fragment.dashboard.Genre
import ml.dvnlabs.animize.ui.fragment.dashboard.Home

class DashboardPager(fm: FragmentManager?, private val numOfTabss: Int, private val mContext: Context) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Home()
            1 -> Genre()
            else -> Home()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "For U"
            1 -> return "Genre"
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return numOfTabss
    }

}