/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ml.dvnlabs.animize.ui.fragment.dashboard.Genre
import ml.dvnlabs.animize.ui.fragment.dashboard.Home
import ml.dvnlabs.animize.ui.fragment.navigation.Updates

class DashboardPager(fm: FragmentManager?, private val numOfTabss: Int, private val mContext: Context) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Home()
            1 -> Genre()
            2-> Updates()
            else -> Home()
        }
    }

    override fun getCount(): Int {
        return numOfTabss
    }

}