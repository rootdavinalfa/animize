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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.ui.recyclerview.packagelist.StarListAdapter
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel
import ml.dvnlabs.animize.view.AutoGridLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class Star : Fragment(), KoinComponent {
    private val animizeDB: AnimizeDatabase by inject { parametersOf(context) }
    private var counter = 0
    private var rvStarred: RecyclerView? = null
    private var adapter: StarListAdapter? = null
    private var autoGridLayoutManager: AutoGridLayoutManager? = null
    private var voided: RelativeLayout? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private val commonViewModel: CommonViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_star, container, false)
        rvStarred = view.findViewById(R.id.star_list)
        voided = view.findViewById(R.id.starred_void)
        refreshLayout = view.findViewById(R.id.starred_refresh)
        refreshList()
        swipeRefresh()

        commonViewModel.libraryScrolledToTop.observe(viewLifecycleOwner, Observer {
            if (it) {
                scrollToTop()
            }
        })
        return view
    }

    private fun refreshList() = CoroutineScope(Dispatchers.Main).launch {
        if (animizeDB.animeDAO().getAllStarredAnime().isNotEmpty()) {
            voided!!.visibility = View.GONE
            if (adapter != null) {
                adapter!!.notifyDataSetChanged()
            }
            getStarredPackage()
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
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        )
    }

    private suspend fun getStarredPackage() {
        val pa = animizeDB.animeDAO().getAllStarredAnime()
        autoGridLayoutManager = AutoGridLayoutManager(requireContext(), 500)
        adapter = StarListAdapter(pa, requireContext(), R.layout.rv_starredpackage)
        rvStarred!!.adapter = adapter
        rvStarred!!.layoutManager = autoGridLayoutManager
    }

    private fun scrollToTop() {
        rvStarred!!.smoothScrollToPosition(0)
        commonViewModel.changeLibraryScrolledToTop()
    }
}