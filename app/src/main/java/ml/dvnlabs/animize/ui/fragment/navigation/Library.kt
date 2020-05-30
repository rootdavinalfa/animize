/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.ui.pager.LibraryPager
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel

class Library : Fragment() {
    private var tabLayout: TabLayout? = null
    private var pager: ViewPager? = null
    private lateinit var commonViewModel : CommonViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        tabLayout = view.findViewById(R.id.library_tablayout)
        pager = view.findViewById(R.id.library_pager)
        initialize()
        return view
    }
    private fun initialize() {
        tabLayout?.setupWithViewPager(pager)
        val adapter = LibraryPager(childFragmentManager, tabLayout?.tabCount!!, requireContext())
        pager?.adapter = adapter
        pager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        commonViewModel = activity?.run {
            ViewModelProvider(this)[CommonViewModel::class.java]
        } ?: throw Exception("Invalid")

        commonViewModel.libraryScrolledToTop.observe(viewLifecycleOwner, Observer {
            if (it){
                scrollToTop()
            }
        })
    }

    private fun scrollToTop(){
        pager!!.currentItem = 0
    }

}