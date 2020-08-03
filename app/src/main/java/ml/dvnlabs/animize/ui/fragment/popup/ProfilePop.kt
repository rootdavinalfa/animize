/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.fragment.popup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.ui.activity.MainActivity
import org.koin.android.ext.android.inject

class ProfilePop : BottomSheetDialogFragment() {
    private val animizeDB: AnimizeDatabase by inject()
    private val recentDB: StarredNotificationDatabase by inject()
    private var textname: TextView? = null
    private var textEmail: TextView? = null
    private var btnLogout: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.profilepop, container, false)
        view.clipToOutline = true
        btnLogout = view.findViewById<View>(R.id.dash_profile_button_logout) as Button
        textEmail = view.findViewById<View>(R.id.dash_profile_tv_email) as TextView
        textname = view.findViewById<View>(R.id.dash_profile_tv_name) as TextView

        GlobalScope.launch {
            readUser()
        }

        btnLogout!!.setOnClickListener { signOut() }
        return view
    }

    private fun signOut() {
        GlobalScope.launch {
            signOutDatabase()
        }
    }

    private suspend fun signOutDatabase() {
        withContext(Dispatchers.IO) {
            animizeDB.animeDAO().deleteAll()
            animizeDB.userDAO().deleteUser()
            recentDB.starredNotificationDAO().deleteALL()
            withContext(Dispatchers.Main) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun readUser() {
        withContext(Dispatchers.IO) {
            val usl = animizeDB.userDAO().getUser()
            withContext(Dispatchers.Main) {
                textEmail!!.text = usl.email
                textname!!.text = usl.nameUser
            }
        }
    }
}