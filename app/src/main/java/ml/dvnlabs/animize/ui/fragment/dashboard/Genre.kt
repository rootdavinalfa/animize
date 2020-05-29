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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentGenreBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.RequestQueueVolley
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.MetaGenreModel
import ml.dvnlabs.animize.ui.pager.MultiTabPager
import ml.dvnlabs.animize.ui.recyclerview.staggered.MetaGenreAdapter
import ml.dvnlabs.animize.ui.recyclerview.staggered.MetaGenreHolder.GotoPageGenre
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Genre : Fragment() {
    private var binding : FragmentGenreBinding? = null
    private var metagenre_models: ArrayList<MetaGenreModel>? = null
    private var metagenre_adapter: MetaGenreAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater.inflate(R.layout.fragment_genre, container, false)
        binding = FragmentGenreBinding.inflate(inflater)
        binding!!.genreTabshow.setOnClickListener {
            binding!!.genreMetaContainer.toggle()
            val deg = if (binding!!.genreTabshow.rotation == 180f) 0f else 180f
            binding!!.genreTabshow.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
        }
        getPageTitle()
        return binding!!.root
    }

    override fun onPause() {
        val queue = RequestQueueVolley(requireContext())
        queue.clearRequest()
        super.onPause()
    }

    private fun gotoPagers(page: Int) {
        binding!!.genreViewpager.setCurrentItem(page, true)
        binding!!.genreMetaContainer.toggle()
        val deg = if (binding!!.genreTabshow.rotation == 180f) 0f else 180f
        binding!!.genreTabshow.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
    }

    private fun getPageTitle() {
        metagenre_models = ArrayList()
        val url = Api.url_genremeta
        APINetworkRequest(requireContext(), fetchGenre, url, CODE_GET_REQUEST, null)
    }

    private fun initializeTab() {
        Log.e("INITIALIZE", "CHECK!")
        binding!!.genreTablayout.setupWithViewPager(binding!!.genreViewpager)
        //num is number of tabs,pagetitle is List<>;
        val adapter = MultiTabPager(requireActivity().supportFragmentManager, metagenre_models!!.size, metagenre_models!!)
        binding!!.genreViewpager.adapter = adapter
        binding!!.genreViewpager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding!!.genreTablayout))
        val staggeredLayout = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        staggeredLayout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        metagenre_adapter = MetaGenreAdapter(metagenre_models, requireActivity(), R.layout.rv_staggered, gotoPageGenre)
        binding!!.genreRvMetaStaggered.layoutManager = staggeredLayout
        binding!!.genreRvMetaStaggered.adapter = metagenre_adapter!!
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
            binding!!.genreTitleLoad.visibility = View.GONE
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
            binding!!.genreTitleLoad.visibility = View.GONE
            Log.e("ERROR:", msg!!)
        }

        override fun onFetchStart() {
            binding!!.genreTitleLoad.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        fun newInstance(): Genre {
            return Genre()
        }
    }
}