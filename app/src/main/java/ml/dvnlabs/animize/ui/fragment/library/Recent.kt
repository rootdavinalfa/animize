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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.RecentPlayDBHelper
import ml.dvnlabs.animize.ui.recyclerview.packagelist.RecentListAdapter

class Recent : Fragment() {
    private var refreshLayout: SwipeRefreshLayout? = null
    private var rvList: RecyclerView? = null
    private var voidedLayout: RelativeLayout? = null
    private var adapter: RecentListAdapter? = null
    private var recentPlayDBHelper: RecentPlayDBHelper? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recent, container, false)
        recentPlayDBHelper = RecentPlayDBHelper(activity)
        refreshLayout = view.findViewById(R.id.recent_refresh)
        rvList = view.findViewById(R.id.recent_list)
        voidedLayout = view.findViewById(R.id.recent_void)
        if (recentPlayDBHelper!!.isRecentAvailLis) {
            voidedLayout!!.visibility = View.GONE
            GlobalScope.launch {
                readListRecent()
            }
        } else {
            voidedLayout!!.visibility = View.VISIBLE
        }
        swipeRefresh()
        return view
    }

    private fun refreshingList() {
        if (recentPlayDBHelper!!.isRecentAvailLis) {
            voidedLayout!!.visibility = View.GONE
            GlobalScope.launch {
                readListRecent()
            }
        } else {
            voidedLayout!!.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        refreshingList()
    }

    private fun swipeRefresh() {
        refreshLayout!!.setOnRefreshListener {
            refreshingList()
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

    private suspend fun readListRecent(){
        withContext(Dispatchers.IO){
            val recentLands = recentPlayDBHelper!!.getRecentList()
            if (!recentLands.isNullOrEmpty()){
                withContext(Dispatchers.Main){
                    val layoutManager = LinearLayoutManager(activity)
                    adapter = RecentListAdapter(recentLands, requireActivity(), R.layout.rv_recentview)
                    rvList!!.layoutManager = layoutManager
                    rvList!!.adapter = adapter
                }
            }
        }
    }
}