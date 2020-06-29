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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentRecentBinding
import ml.dvnlabs.animize.ui.recyclerview.packagelist.RecentListAdapter
import ml.dvnlabs.animize.ui.viewmodel.ListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class Recent : Fragment() {
    private var binding : FragmentRecentBinding? = null
    private var adapter: RecentListAdapter? = null
    private val listVM : ListViewModel by sharedViewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_recent, container, false)
        binding = FragmentRecentBinding.inflate(inflater)

        val layoutManager = LinearLayoutManager(requireActivity())
        adapter = RecentListAdapter(R.layout.rv_recentview)
        binding?.recentList?.layoutManager = layoutManager
        binding?.recentList?.adapter = adapter
        listVM.listRecent.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding?.recentVoid?.visibility = View.GONE
                adapter?.setRecentLand(it)
            }else{
                binding?.recentVoid?.visibility = View.VISIBLE
            }
        })
        swipeRefresh()
        return binding?.root
    }


    private fun swipeRefresh() {
        binding?.recentRefresh?.setOnRefreshListener {
            // To keep animateView for 4 seconds
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                binding?.recentRefresh?.isRefreshing = false
            }, 2000) // Delay in millis
        }
        binding?.recentRefresh?.setColorSchemeColors(
                ContextCompat.getColor(requireContext(),android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(),android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_red_light)
        )
    }
}