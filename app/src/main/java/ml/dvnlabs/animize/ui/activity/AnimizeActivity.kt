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
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlinx.android.synthetic.main.animize_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.base.BaseActivity
import ml.dvnlabs.animize.database.legacy.InitInternalDBHelper
import ml.dvnlabs.animize.ui.pager.MainNavPager
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnimizeActivity : BaseActivity(){
    private val commonVM: CommonViewModel by viewModel()
    private lateinit var initInternalDBHelper: InitInternalDBHelper
    private val indexMain = mapOf(0 to R.id.nav_home, 1 to R.id.nav_library, 2 to R.id.nav_update)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.animize_activity)
        initInternalDBHelper = InitInternalDBHelper(this)
        changeStatusBar(this, R.color.colorPrimaryDark, false)
        runBlocking { readUser() }
        initLayout()
    }

    private fun onNavigationItemReselected(item: MeowBottomNavigation.Model) {
        when (item.id) {
            R.id.nav_home -> {
                val navController = findNavController(R.id.dashboardNavHost)
                navController.popBackStack(navController.graph.startDestination, false)
                commonVM.changeDashboardScrolledToTop()
            }
            R.id.nav_library -> {
                commonVM.changeLibraryScrolledToTop()
            }
        }
    }

    private fun onNavigationItemSelected(item: MeowBottomNavigation.Model): Boolean {
        val position = indexMain.values.indexOf(item.id)
        if (mainPager.currentItem != position) commonVM.changeSelectedPageMainNavigation(position)
        return true
    }

    private fun initLayout() {
        mainPager.adapter = MainNavPager(supportFragmentManager, 3)
        mainPager.offscreenPageLimit = 2
        mainBottomNavigation.add(MeowBottomNavigation.Model(R.id.nav_home,R.drawable.ic_home))
        mainBottomNavigation.add(MeowBottomNavigation.Model(R.id.nav_library,R.drawable.ic_library))
        mainBottomNavigation.add(MeowBottomNavigation.Model(R.id.nav_update,R.drawable.ic_featured))
        mainBottomNavigation.setOnClickMenuListener {
            onNavigationItemSelected(it)
        }
        mainBottomNavigation.setOnReselectListener {
            onNavigationItemReselected(it)
        }
        mainBottomNavigation.setOnShowListener {
            onNavigationItemSelected(it)
        }

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
                mainBottomNavigation.show(R.id.nav_home)
            }
            2 -> {
                mainBottomNavigation.show(R.id.nav_home)
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