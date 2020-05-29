/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.animize_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.base.BaseActivity
import ml.dvnlabs.animize.database.InitInternalDBHelper
import ml.dvnlabs.animize.ui.pager.MainNavPager
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel

class AnimizeActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {
    private lateinit var commonVM: CommonViewModel
    private lateinit var initInternalDBHelper: InitInternalDBHelper
    private val indexMain = mapOf(0 to R.id.nav_home, 1 to R.id.nav_library)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.animize_activity)
        initInternalDBHelper = InitInternalDBHelper(this)
        changeStatusBar(this, R.color.colorPrimaryDark, false)
        commonVM = ViewModelProvider(this).get(CommonViewModel::class.java)


        runBlocking { readUser() }
        initLayout()
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        if (item.itemId == R.id.nav_home){
            val navController = findNavController(R.id.dashboardNavHost)
            navController.popBackStack(navController.graph.startDestination, false)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val position = indexMain.values.indexOf(item.itemId)
        if (mainPager.currentItem != position) commonVM.changeSelectedPageMainNavigation(position)
        return true
    }

    private fun initLayout() {
        mainPager.adapter = MainNavPager(supportFragmentManager, 2)
        mainBottomNavigation.setOnNavigationItemSelectedListener(this)
        mainBottomNavigation.setOnNavigationItemReselectedListener(this)

        commonVM.selectedPageMainNavigation.observe(this, Observer {
            mainPager.currentItem = it
        })
    }

    override fun onBackPressed() {
        when (mainPager.currentItem) {
            0 -> {
                val navControl = findNavController(R.id.dashboardNavHost)
                if (navControl.currentDestination!!.id == R.id.dashboard) {
                    val startMain = Intent(Intent.ACTION_MAIN)
                    startMain.addCategory(Intent.CATEGORY_HOME)
                    startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(startMain)
                } else {
                    navControl.popBackStack()
                }
            }
            1 -> {
                mainBottomNavigation.selectedItemId = R.id.nav_home
            }
            else -> {
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(startMain)
            }
        }
    }


    private suspend fun readUser() {
        withContext(Dispatchers.IO) {
            commonVM.userLand = initInternalDBHelper.user
        }
    }
}