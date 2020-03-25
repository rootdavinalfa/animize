package ml.dvnlabs.animize.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.fragment.tabs.animplay.details
import ml.dvnlabs.animize.fragment.tabs.animplay.PlaylistFragment

class aplayViewPagerAdapter(fm : FragmentManager, numOfTabs : Int,context : Context) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var numOfTabss : Int = numOfTabs
    var mContext : Context = context

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return details()
            1 -> return PlaylistFragment()
        }
        return null!!
    }

    override fun getCount(): Int {
        return numOfTabss
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return mContext.getString(R.string.pager_title_details)
            1 -> return mContext.getString(R.string.pager_title_more)
            else -> return null
        }
    }
}