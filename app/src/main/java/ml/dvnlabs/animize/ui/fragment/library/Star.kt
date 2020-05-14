/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.fragment.library

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.PackageStarDBHelper
import ml.dvnlabs.animize.ui.recyclerview.packagelist.starlist_adapter
import ml.dvnlabs.animize.view.AutoGridLayoutManager

class Star : Fragment() {
    private var packageStarDBHelper: PackageStarDBHelper? = null
    private var counter = 0
    private var rvStarred: RecyclerView? = null
    private var adapter: starlist_adapter? = null
    private var autoGridLayoutManager: AutoGridLayoutManager? = null
    private var voided: RelativeLayout? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_star, container, false)
        rvStarred = view.findViewById(R.id.star_list)
        voided = view.findViewById(R.id.starred_void)
        refreshLayout = view.findViewById(R.id.starred_refresh)
        packageStarDBHelper = PackageStarDBHelper(activity)
        refreshList()
        swipeRefresh()
        return view
    }

    private fun refreshList() {
        if (packageStarDBHelper!!.isAvail) {
            voided!!.visibility = View.GONE
            if (adapter != null) {
                adapter!!.notifyDataSetChanged()
            }
            GlobalScope.launch {
                getStarredPackage()
            }
        } else {
            voided!!.visibility = View.VISIBLE
        }
    }


    private fun swipeRefresh() {
        refreshLayout!!.setOnRefreshListener {
            refreshList()
            // To keep animateView for 4 seconds
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                refreshLayout!!.isRefreshing = false
            }, 2000) // Delay in millis
        }
        refreshLayout!!.setColorSchemeColors(
                ContextCompat.getColor(requireContext(),android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(),android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_red_light)
        )
    }

    private suspend fun getStarredPackage(){
        withContext(Dispatchers.IO){
            val pa = packageStarDBHelper!!.starredList
            if (!pa.isNullOrEmpty())
                if (pa.isNotEmpty()) {
                    autoGridLayoutManager = AutoGridLayoutManager(activity, 500)
                    adapter = starlist_adapter(pa, activity, R.layout.rv_starredpackage)
                    rvStarred!!.adapter = adapter
                    rvStarred!!.layoutManager = autoGridLayoutManager
                } else {
                    voided!!.visibility = View.VISIBLE
                }
        }
    }
}