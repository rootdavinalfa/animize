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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.data.notification.StarredNotificationWorker
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.databinding.FragmentUpdatesBinding
import ml.dvnlabs.animize.ui.recyclerview.list.update.NotificationUpdateAdapter

class Updates : Fragment() {
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
        GlobalScope.launch { refreshUpdate() }
        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolRefresh -> {
                GlobalScope.launch {
                    StarredNotificationWorker.setupTaskImmediately(requireContext())
                    refreshUpdate()
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

    private suspend fun refreshUpdate() {
        withContext(Dispatchers.IO) {
            val notificationList = starredRoom!!.starredNotificationDAO().getStarredNotificationListNotOpened()
            withContext(Dispatchers.Main) {
                adapter!!.setNotification(notificationList)
            }
        }
    }
}