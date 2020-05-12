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
import ml.dvnlabs.animize.ui.fragment.tabs.animplay.PlaylistFragment
import ml.dvnlabs.animize.ui.fragment.tabs.animplay.details

class AplayViewPagerAdapter(fm: FragmentManager, numOfTabs: Int, context: Context) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var numOfTabss : Int = numOfTabs
    var mContext : Context = context

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> details()
            1 -> PlaylistFragment()
            else -> null!!
        }
    }

    override fun getCount(): Int {
        return numOfTabss
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> mContext.getString(R.string.pager_title_details)
            1 -> mContext.getString(R.string.pager_title_more)
            else -> null
        }
    }
}