package ml.dvnlabs.animize.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.pager.library_pager

class library : Fragment(){
    private var tabLayout: TabLayout? = null
    private var pager: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        tabLayout = view.findViewById<TabLayout>(R.id.library_tablayout)
        pager = view.findViewById<ViewPager>(R.id.library_pager)
        initialize()

        return view
    }
    private fun initialize() {
        tabLayout?.setupWithViewPager(pager)
        val adapter = library_pager(childFragmentManager, tabLayout?.tabCount!!, context)
        pager?.adapter = adapter
        pager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

}