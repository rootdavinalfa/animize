/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.fragment.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.MetaGenreModel
import ml.dvnlabs.animize.pager.MultiTabPager
import ml.dvnlabs.animize.ui.recyclerview.staggered.MetaGenreAdapter
import ml.dvnlabs.animize.ui.recyclerview.staggered.MetaGenreHolder.GotoPageGenre
import net.cachapa.expandablelayout.ExpandableLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Genre : Fragment() {
    private var tabLayout: TabLayout? = null
    private var pager: ViewPager? = null
    private var metagenre_models: ArrayList<MetaGenreModel>? = null
    private var loading: LinearLayout? = null
    private var expand_meta: ExpandableLayout? = null
    private var rv_meta: RecyclerView? = null
    private var metagenre_adapter: MetaGenreAdapter? = null
    private var staggeredLayout: StaggeredGridLayoutManager? = null
    private var btn_expand: ImageView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_genre, container, false)
        tabLayout = view.findViewById(R.id.genre_tablayout)
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        pager = view.findViewById(R.id.genre_viewpager)
        loading = view.findViewById(R.id.genre_title_load)
        rv_meta = view.findViewById(R.id.genre_rv_meta_staggered)
        expand_meta = view.findViewById(R.id.genre_meta_container)
        btn_expand = view.findViewById(R.id.genre_tabshow)
        btn_expand!!.setOnClickListener {
            expand_meta!!.toggle()
            val deg = if (btn_expand!!.rotation == 180f) 0f else 180f
            btn_expand!!.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
        }
        getPageTitle()
        return view
    }

    private fun gotoPagers(page: Int) {
        pager!!.setCurrentItem(page, true)
        expand_meta!!.toggle()
        val deg = if (btn_expand!!.rotation == 180f) 0f else 180f
        btn_expand!!.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
    }

    private fun getPageTitle() {
        metagenre_models = ArrayList()
        val url = Api.url_genremeta
        APINetworkRequest(requireContext(), fetchGenre, url, CODE_GET_REQUEST, null)
    }

    private fun initializeTab() {
        Log.e("INITIALIZE", "CHECK!")
        tabLayout!!.setupWithViewPager(pager)
        //num is number of tabs,pagetitle is List<>;
        val adapter = MultiTabPager(childFragmentManager, metagenre_models!!.size, metagenre_models!!)
        pager!!.adapter = adapter
        pager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        staggeredLayout = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        metagenre_adapter = MetaGenreAdapter(metagenre_models, requireActivity(), R.layout.rv_staggered, gotoPageGenre)
        rv_meta!!.layoutManager = staggeredLayout
        rv_meta!!.adapter = metagenre_adapter
    }

    private fun setTabTitle(titles: JSONArray) {
        try {
            for (i in 0 until titles.length()) {
                val `object` = titles.getJSONObject(i)
                val title = `object`.getString("name_genre")
                val count = `object`.getString("sum_genre")
                metagenre_models!!.add(MetaGenreModel(title, count))
            }
            initializeTab()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    var gotoPageGenre = object : GotoPageGenre {
        override fun gotoPager(page: Int) {
            gotoPagers(page)
        }
    }
    private var fetchGenre: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            loading!!.visibility = View.GONE
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    setTabTitle(`object`.getJSONArray("genre"))
                }
            } catch (e: JSONException) {
                Log.e("ERROR:", e.toString())
            }
        }

        override fun onFetchFailure(msg: String?) {
            loading!!.visibility = View.GONE
            Log.e("ERROR:", msg!!)
        }

        override fun onFetchStart() {
            loading!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        fun newInstance(): Genre {
            return Genre()
        }
    }
}