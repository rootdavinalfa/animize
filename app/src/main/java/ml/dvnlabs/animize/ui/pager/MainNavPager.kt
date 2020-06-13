/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ml.dvnlabs.animize.ui.fragment.navigation.DashboardNavHost
import ml.dvnlabs.animize.ui.fragment.navigation.Library
import ml.dvnlabs.animize.ui.fragment.navigation.Updates

class MainNavPager(fm: FragmentManager, var numOfTabs: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    lateinit var currentFragment: Fragment
    override fun getItem(position: Int): Fragment {
        currentFragment = when (position) {
            0 -> DashboardNavHost()
            1 -> Library()
            2-> Updates()
            else -> DashboardNavHost()
        }
        return currentFragment
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}