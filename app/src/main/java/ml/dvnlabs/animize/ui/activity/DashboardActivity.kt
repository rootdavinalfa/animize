/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.activity

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.app.AppController
import ml.dvnlabs.animize.base.BaseActivity
import ml.dvnlabs.animize.checker.CheckNetwork
import ml.dvnlabs.animize.checker.CheckNetwork.ConnectivityReceiverListener
import ml.dvnlabs.animize.database.InitInternalDBHelper
import ml.dvnlabs.animize.pager.DashboardPager
import ml.dvnlabs.animize.ui.fragment.LastUpAnime
import ml.dvnlabs.animize.ui.fragment.Library
import ml.dvnlabs.animize.ui.fragment.Search
import ml.dvnlabs.animize.ui.fragment.global.Companion.addFragment
import ml.dvnlabs.animize.ui.fragment.popup.ProfilePop

class DashboardActivity : BaseActivity(), ConnectivityReceiverListener {
    private val receiver: BroadcastReceiver? = null
    var initInternalDBHelper: InitInternalDBHelper? = null
    private var dash_profile_username: TextView? = null
    private var header_layout: LinearLayout? = null
    private var dash_serach_btn: ImageView? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var dash_profile: RelativeLayout? = null
    private var dash_tabs: TabLayout? = null
    private var dash_pager: ViewPager? = null
    private var NetworkChecker: CheckNetwork? = null
    private var isHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        initInternalDBHelper = InitInternalDBHelper(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags
            this.window.statusBarColor = getColor(R.color.colorPrimaryDark)
        }
        initializes()
        GlobalScope.launch {
            readUser()
        }
        NetworkChecker = CheckNetwork()
        registerReceiver(NetworkChecker, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        bottomNavigationLogic()
        dash_serach_btn!!.setOnClickListener { display_search() }
    }

    //inetCheck for check on fragment
    fun inetCheck() {
        val isConnect = CheckNetwork.isConnected
        showSnack(isConnect)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showSnack(isConnected)
    }

    private fun showSnack(isConnected: Boolean) {
        val message: String
        val color: Int
        val snackbar: Snackbar
        if (!isConnected) {
            message = resources.getString(R.string.NO_NETWORK)
            color = Color.WHITE
            snackbar = Snackbar
                    .make(findViewById(R.id.DashnavigationView), message, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Retry") {
                //home hm = (home) getSupportFragmentManager().findFragmentById(R.id.home_fragment);
                //hm.getLast_Up();
                //hm.getLastPackage();
            }.setActionTextColor(color)
            val sbView = snackbar.view
            sbView.setBackgroundColor(resources.getColor(R.color.design_default_color_error))
            val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(color)
            snackbar.show()
        }
    }

    fun snackError(error: String?, id: Int) {
        val color: Int = Color.WHITE
        val snackBar: Snackbar = Snackbar
                .make(findViewById(R.id.DashnavigationView), error!!, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            when (id) {
                1 -> {
                    val hm = supportFragmentManager.findFragmentById(R.id.video_list_fragment) as LastUpAnime?
                    if (hm != null && hm.isVisible) {
                        hm.getList()
                    } else {
                        snackBar.dismiss()
                    }
                }
            }
        }.setActionTextColor(color)
        val sbView = snackBar.view
        sbView.setBackgroundColor(resources.getColor(R.color.design_default_color_error))
        val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(color)
        snackBar.show()
    }

    fun initializes() {
        header_layout = findViewById<View>(R.id.header) as LinearLayout

        //Set pager and tab
        dash_tabs = findViewById(R.id.dash_tab)
        dash_pager = findViewById(R.id.dash_viewpager)
        dash_tabs!!.setupWithViewPager(dash_pager)
        val adapter = DashboardPager(supportFragmentManager, dash_tabs!!.tabCount, this)
        dash_pager!!.adapter = adapter
        dash_pager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(dash_tabs))
        isHome = true
        dash_serach_btn = findViewById(R.id.dash_btn_search)
        bottomNavigationView = findViewById<View>(R.id.DashnavigationView) as BottomNavigationView
        dash_profile_username = findViewById<View>(R.id.dash_profile_text) as TextView
        dash_profile = findViewById<View>(R.id.dash_profile) as RelativeLayout
        dash_profile!!.setOnClickListener {
            val pop = ProfilePop()
            pop.show(supportFragmentManager, "profilepop")
        }
        //display_home();
    }

    fun broadcastIntent() {
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onResume() {
        super.onResume()
        AppController.instance?.setConnectivityListener(this)
        if (bottomNavigationView!!.menu.getItem(1).isChecked) {
            //display_library();
            close_home()
        } else {
            display_home()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(NetworkChecker)
        super.onDestroy()
    }

    override fun onBackPressed() {
        //header.setVisibility(View.VISIBLE);
        //int count = getSupportFragmentManager().getBackStackEntryCount()-1;
        //Log.e("COUNTED-:",String.valueOf(count));
        if (isHome) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(startMain)
            //super.onBackPressed();
            //additional code
        } else {
            bottomNavigationView!!.menu.getItem(0).isChecked = true
            display_home()
        }
    }

    private fun bottomNavigationLogic() {
        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    display_home()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_library -> {
                    close_home()
                    display_library()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_feed -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }

    fun display_home() {
        isHome = true
        close_search()
        close_library()
        close_lastup()
        dash_pager!!.visibility = View.VISIBLE
        header_layout!!.visibility = View.VISIBLE
    }

    fun close_home() {
        isHome = false
        dash_pager!!.visibility = View.GONE
        header_layout!!.visibility = View.GONE
    }

    fun display_search() {
        close_home()
        close_library()
        val se = Search.newInstance()
        addFragment(supportFragmentManager, se, R.id.search_fragment, "FRAGMENT_OTHER", "SLIDE")
    }

    fun close_search() {
        // Get the FragmentManager.
        val fragmentManager = supportFragmentManager
        // Check to see if the fragment is already showing.
        val simpleFragment = fragmentManager
                .findFragmentById(R.id.search_fragment) as Search?
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(simpleFragment).commit()
        }
    }

    fun display_lastup() {
        close_home()
        val se = LastUpAnime.newInstance()
        addFragment(supportFragmentManager, se, R.id.video_list_fragment, "FRAGMENT_OTHER", "SLIDE")
    }

    fun close_lastup() {
        // Get the FragmentManager.
        val fragmentManager = supportFragmentManager
        // Check to see if the fragment is already showing.
        val simpleFragment = fragmentManager
                .findFragmentById(R.id.video_list_fragment) as LastUpAnime?
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(simpleFragment).commit()
        }
    }

    fun display_library() {
        close_home()
        close_search()
        close_lastup()
        val se = Library()
        addFragment(supportFragmentManager, se, R.id.library_fragment, "FRAGMENT_OTHER", "NULL")
    }

    fun close_library() {
        // Get the FragmentManager.
        val fragmentManager = supportFragmentManager
        // Check to see if the fragment is already showing.
        val simpleFragment = fragmentManager
                .findFragmentById(R.id.library_fragment) as Library?
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(simpleFragment).commit()
        }
    }

    private suspend fun readUser() {
        withContext(Dispatchers.IO) {
            val user = initInternalDBHelper!!.user
            withContext(Dispatchers.Main) {
                dash_profile_username!!.text = user!!.nameUser
            }
        }
    }
}