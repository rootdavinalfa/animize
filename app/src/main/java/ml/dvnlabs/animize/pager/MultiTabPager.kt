/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.pager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ml.dvnlabs.animize.model.MetaGenreModel
import ml.dvnlabs.animize.ui.fragment.tabs.multiview.MultiView
import java.util.*

class MultiTabPager(fm: FragmentManager, private val numOfTabs: Int, meta: ArrayList<MetaGenreModel>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fragment: Fragment? = null
    private var meta = ArrayList<MetaGenreModel>()
    private var bundle: Bundle? = null
    private val fragmentManager: FragmentManager
    override fun getItem(position: Int): Fragment {
        for (i in 0 until numOfTabs) {
            if (i == position) {
                bundle = Bundle()
                fragment = MultiView.newInstance()
                bundle!!.putString("genre", meta[i].title)
                fragment!!.arguments = bundle
                break
            }
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return numOfTabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val title: String? = "Default"
        if (meta.isNotEmpty())
            return meta[position].title
        return title
    }

    init {
        this.meta = meta
        fragmentManager = fm
    }
}