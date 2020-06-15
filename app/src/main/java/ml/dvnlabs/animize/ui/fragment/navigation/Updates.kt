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
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.data.notification.StarredNotificationWorker
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.databinding.FragmentUpdatesBinding
import ml.dvnlabs.animize.ui.recyclerview.list.update.NotificationUpdateAdapter
import ml.dvnlabs.animize.ui.viewmodel.ListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class Updates : Fragment() {
    private val listVM : ListViewModel by sharedViewModel()
    private var binding: FragmentUpdatesBinding? = null
    private var starredRoom: StarredNotificationDatabase? = null
    private var adapter: NotificationUpdateAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        starredRoom = StarredNotificationDatabase.getDatabase(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater.inflate(R.layout.fragment_updates, container, false)
        binding = FragmentUpdatesBinding.inflate(inflater)
        (activity as AppCompatActivity).setSupportActionBar(binding!!.updateToolbar)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationUpdateAdapter(R.layout.rv_update)
        binding!!.updateList.layoutManager = layoutManager
        binding!!.updateList.adapter = adapter
        binding!!.updateToolbar.title = "Updates"

        listVM.listNotification.observe(viewLifecycleOwner, Observer {
            println("SIZE DATA: ${it.size}")
            adapter?.setNotification(it)
        })

        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolRefresh -> {
                GlobalScope.launch {
                    StarredNotificationWorker.setupTaskImmediately(requireContext())
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_standard, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}