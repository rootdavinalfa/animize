/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.fragment.dashboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.yarolegovich.discretescrollview.DiscreteScrollView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.BannerListMdl
import ml.dvnlabs.animize.model.HomeLastUploadModel
import ml.dvnlabs.animize.model.PackageList
import ml.dvnlabs.animize.ui.activity.DashboardActivity
import ml.dvnlabs.animize.ui.recyclerview.banner.BannerAdapter
import ml.dvnlabs.animize.ui.recyclerview.list.HomeLastUpAdapter
import ml.dvnlabs.animize.ui.recyclerview.packagelist.LastPackageAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Home : Fragment() {
    private var pageLastUp = 0
    private var lastUpLoading: ShimmerFrameLayout? = null
    private var packageLoading: ShimmerFrameLayout? = null
    private var bannerLoading: ShimmerFrameLayout? = null
    private var dashButtonLastUpMore: RelativeLayout? = null
    private var listViewLastUp: DiscreteScrollView? = null
    private var rvBannerList: DiscreteScrollView? = null
    private var rvLastPackage: DiscreteScrollView? = null
    private var modelDataLastUp: ArrayList<HomeLastUploadModel>? = null
    private var modeldatapackage: ArrayList<PackageList>? = null
    private var bannerListModels: ArrayList<BannerListMdl>? = null
    private var bannerScrolling: Handler? = null
    private var bannerRunnable: Runnable? = null
    private var adapterLastUp: HomeLastUpAdapter? = null
    private var adapterlastpackage: LastPackageAdapter? = null
    private var adapterBanner: BannerAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var refreshHome: SwipeRefreshLayout? = null
    private var mContext: Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        listViewLastUp = view.findViewById(R.id.lastup_list)

        linearLayoutManager = LinearLayoutManager(activity)
        dashButtonLastUpMore = view.findViewById(R.id.dash_lastup_more)
        lastUpLoading = view.findViewById(R.id.loading_lastup)
        bannerLoading = view.findViewById(R.id.shimmer_banner)
        packageLoading = view.findViewById(R.id.shimmer_package)
        rvLastPackage = view.findViewById(R.id.rv_lastpackage)
        rvBannerList = view.findViewById(R.id.rv_banner)
        refreshHome = view.findViewById(R.id.dash_refresh_home)
        initialSetup()
        swipeRefresh()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun swipeRefresh() {
        refreshHome!!.setOnRefreshListener {
            banner
            lastUp
            lastPackage
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                refreshHome!!.isRefreshing = false
            }, 2000) // Delay in millis
        }
        refreshHome!!.setColorSchemeColors(
                ContextCompat.getColor(requireContext(),android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(),android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_red_light)
        )
    }

    private fun initialSetup() {
        modelDataLastUp = ArrayList()
        modeldatapackage = ArrayList()
        bannerListModels = ArrayList()
        actionDashLastUPButton()
        banner
        lastUp
        lastPackage
    }

    private fun actionDashLastUPButton() {
        dashButtonLastUpMore!!.setOnClickListener { (activity as DashboardActivity?)!!.displayLastup() }
    }

    private val banner: Unit
        get() {
            val url = Api.url_banner
            APINetworkRequest(requireActivity(), bannerList, url, CODE_GET_REQUEST, null)
        }

    private val lastUp: Unit
        get() {
            val url = Api.url_page + "1"
            APINetworkRequest(requireActivity(), lastup, url, CODE_GET_REQUEST, null)
        }

    private val lastPackage: Unit
        get() {
            val url = Api.url_packagepage + "1"
            APINetworkRequest(requireActivity(), lastpackage, url, CODE_GET_REQUEST, null)
        }

    private fun parseJsonLastUp(data: String?) {
        try {
            val `object` = JSONObject(data!!)
            if (!`object`.getBoolean("error")) {
                showVideo(`object`.getJSONArray("anim"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private var bannerList: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            rvBannerList!!.visibility = View.VISIBLE
            bannerLoading!!.stopShimmer()
            bannerLoading!!.visibility = View.GONE
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    bannerList(`object`.getJSONArray("banner"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            bannerLoading!!.stopShimmer()
            bannerLoading!!.startShimmer()
            bannerLoading!!.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
            rvBannerList!!.visibility = View.GONE
            bannerLoading!!.startShimmer()
            bannerLoading!!.visibility = View.VISIBLE
        }
    }
    var lastup: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            listViewLastUp!!.visibility = View.VISIBLE
            lastUpLoading!!.stopShimmer()
            lastUpLoading!!.visibility = View.GONE
            parseJsonLastUp(data)
        }

        override fun onFetchFailure(msg: String?) {
            lastUpLoading!!.startShimmer()
            lastUpLoading!!.visibility = View.VISIBLE
            Log.e("ERROR", msg!!)
        }

        override fun onFetchStart() {
            lastUpLoading!!.startShimmer()
            lastUpLoading!!.visibility = View.VISIBLE
            listViewLastUp!!.visibility = View.GONE
        }
    }
    var lastpackage: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            rvLastPackage!!.visibility = View.VISIBLE
            packageLoading!!.stopShimmer()
            packageLoading!!.visibility = View.GONE
            parseJSONPackage(data)
        }

        override fun onFetchFailure(msg: String?) {
            packageLoading!!.startShimmer()
            packageLoading!!.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
            packageLoading!!.startShimmer()
            packageLoading!!.visibility = View.VISIBLE
            rvLastPackage!!.visibility = View.GONE
        }
    }

    private fun parseJSONPackage(data: String?) {
        try {
            val `object` = JSONObject(data!!)
            if (!`object`.getBoolean("error")) {
                packageList(`object`.getJSONArray("anim"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun bannerList(banner: JSONArray) {
        try {
            bannerListModels!!.clear()
            for (i in 0 until banner.length()) {
                val `object` = banner.getJSONObject(i)
                val bannerImage = `object`.getString("banner_image")
                val bannerUrl = `object`.getString("banner_url")
                val bannerTitle = `object`.getString("banner_title")
                bannerListModels!!.add(BannerListMdl(bannerImage, bannerTitle, bannerUrl))
            }
            adapterBanner = BannerAdapter(bannerListModels, requireActivity(), R.layout.rv_banner)
            rvBannerList!!.adapter = adapterBanner

            bannerScrolling = Handler()
            bannerRunnable = object : Runnable {
                var currentBanner = 0
                override fun run() {
                    currentBanner = rvBannerList!!.currentItem
                    //System.out.println("Old:"+currentBanner+" To New:"+(currentBanner+1));
                    if (adapterBanner!!.itemCount != 0) {
                        if (bannerListModels!!.size - 1 > currentBanner) {
                            rvBannerList!!.smoothScrollToPosition(currentBanner + 1)
                            rvBannerList!!.setItemTransitionTimeMillis(250)
                        } else {
                            rvBannerList!!.smoothScrollToPosition(0)
                            rvBannerList!!.setItemTransitionTimeMillis(250)
                        }
                    }
                    bannerScrolling!!.postDelayed(bannerRunnable!!, 5000)
                }
            }
            bannerScrolling!!.postDelayed(bannerRunnable!!, 5000)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun packageList(anim: JSONArray) {
        try {
            modeldatapackage!!.clear()
            for (i in 0 until anim.length()) {
                val `object` = anim.getJSONObject(i)
                val packages = `object`.getString("package_anim")
                val nameanim = `object`.getString("name_anim")
                val nowep = `object`.getString("now_ep_anim")
                val totep = `object`.getString("total_ep_anim")
                val rate = `object`.getString("rating")
                val mal = `object`.getString("mal_id")
                val cover = `object`.getString("cover")
                val genre_json = `object`.getJSONArray("genre")
                val genres: MutableList<String> = ArrayList()
                for (j in 0 until genre_json.length()) {
                    genres.add(genre_json.getString(j))
                }
                modeldatapackage!!.add(PackageList(packages, nameanim, nowep, totep, rate, mal, genres, cover))
            }
            adapterlastpackage = LastPackageAdapter(modeldatapackage, requireActivity(), R.layout.rv_newanime)
            rvLastPackage!!.adapter = adapterlastpackage
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    private fun showVideo(video: JSONArray) {
        try {
            modelDataLastUp!!.clear()
            listViewLastUp!!.visibility = View.VISIBLE
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val url_tb = jsonObject.getString(Api.JSON_episode_thumb)
                val id = jsonObject.getString(Api.JSON_id_anim)
                val title_name = jsonObject.getString(Api.JSON_name_anim)
                val episode = jsonObject.getString(Api.JSON_episode_anim)
                modelDataLastUp!!.add(HomeLastUploadModel(url_tb, id, title_name, episode))
            }
            adapterLastUp = HomeLastUpAdapter(modelDataLastUp, requireActivity(), R.layout.rv_newepisode)
            listViewLastUp!!.adapter = adapterLastUp
            listViewLastUp!!.setSlideOnFling(true)
            pageLastUp = listViewLastUp!!.currentItem
        } catch (e: JSONException) {
            Log.e("ERROR JSON:", e.toString())
        }
    }


    override fun onDestroy() {
        if (modelDataLastUp!!.isNotEmpty()) {
            modelDataLastUp!!.clear()
        }
        super.onDestroy()
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        fun newInstance(): Home {
            return Home()
        }
    }
}