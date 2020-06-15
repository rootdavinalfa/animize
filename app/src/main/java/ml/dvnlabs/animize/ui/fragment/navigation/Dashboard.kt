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
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takusemba.multisnaprecyclerview.MultiSnapHelper
import kotlinx.android.synthetic.main.fragment_dashboard.*
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentDashboardBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.RequestQueueVolley
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.BannerListMdl
import ml.dvnlabs.animize.model.HomeLastUploadModel
import ml.dvnlabs.animize.model.PackageList
import ml.dvnlabs.animize.ui.fragment.popup.ProfilePop
import ml.dvnlabs.animize.ui.recyclerview.banner.BannerAdapter
import ml.dvnlabs.animize.ui.recyclerview.list.HomeLastUpAdapter
import ml.dvnlabs.animize.ui.recyclerview.packagelist.LastPackageAdapter
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel
import ml.dvnlabs.animize.ui.viewmodel.DataRequestViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class Dashboard : Fragment() {
    private var binding: FragmentDashboardBinding? = null
    private var bannerScrolling: Handler? = null
    private var bannerRunnable: Runnable? = null

    private lateinit var dataViewModel: DataRequestViewModel
    private val commonViewModel: CommonViewModel by sharedViewModel()

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val ID_BANNER_REQUEST = 900
        private const val ID_LAST_EPISODE_REQUEST = 901
        private const val ID_LAST_PACKAGE_REQUEST = 902
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding = FragmentDashboardBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataViewModel = activity?.run {
            ViewModelProvider(this)[DataRequestViewModel::class.java]
        } ?: throw Exception("Invalid")


        commonViewModel.dashboardScrolledToTop.observe(viewLifecycleOwner, Observer {
            if (it) {
                scrollToTop()
            }
        })

        initialize()
    }

    private fun initialize() {
        val multiSnapNewAnime = MultiSnapHelper()
        val multiSnapNewEpisode = MultiSnapHelper()
        multiSnapNewAnime.attachToRecyclerView(binding!!.rvNewAnime)
        multiSnapNewEpisode.attachToRecyclerView(binding!!.rvNewEpisode)
        binding!!.profileImage.setOnClickListener {
            val pop = ProfilePop()
            pop.show(requireActivity().supportFragmentManager, "profilepop")
        }

        if (bannerScrolling != null) {
            bannerScrolling!!.removeCallbacks(bannerRunnable!!)
        }

        binding!!.searchImage.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_search)
        }

        binding!!.genreContainer.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_genre)
        }

        binding!!.newEpisodeContainer.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_lastUpAnime)
        }

        binding!!.newAnimeContainer.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_lastPackageAnime)
        }

        swipeRefresh()
        initRequest()
    }

    private fun initRequest() {

        dataViewModel.requestResult.observe(viewLifecycleOwner, Observer {
            val error = ArrayList<Int>()
            //If size on request ViewModel = 0 then make new request for all data no matter what
            if (dataViewModel.requestResult.value!!.size == 0) {
                bannerRequest
                lastAnimeRequest
                lastEpisodeRequest
            } else {
                try {
                    binding!!.shimmerBanner.visibility = View.GONE
                    binding!!.shimmerNewAnime.visibility = View.GONE
                    binding!!.shimmerNewEpisode.visibility = View.GONE
                    for (x in it) {
                        when (x.id) {
                            ID_BANNER_REQUEST -> {

                                val `object` = JSONObject(x.result)
                                if (!`object`.getBoolean("error")) {
                                    bannerListParser(`object`.getJSONArray("banner"))
                                }
                            }
                            ID_LAST_EPISODE_REQUEST -> {
                                val `object` = JSONObject(x.result)
                                if (!`object`.getBoolean("error")) {
                                    lastEpisodeParser(`object`.getJSONArray("anim"))
                                }
                            }
                            ID_LAST_PACKAGE_REQUEST -> {
                                val `object` = JSONObject(x.result)
                                if (!`object`.getBoolean("error")) {
                                    lastAnimeParser(`object`.getJSONArray("anim"))
                                }
                            }
                            else -> {
                                error.add(x.id)
                            }
                        }
                    }

                    //Check error request then make new request
                    for (new in error) {
                        when (new) {
                            ID_BANNER_REQUEST -> {
                                bannerRequest
                            }
                            ID_LAST_EPISODE_REQUEST -> {
                                lastEpisodeRequest
                            }
                            ID_LAST_PACKAGE_REQUEST -> {
                                lastAnimeRequest
                            }
                            else -> {
                            }
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }

    private fun swipeRefresh() {
        refreshSwipe.setOnRefreshListener {
            bannerRequest
            lastAnimeRequest
            lastEpisodeRequest
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                refreshSwipe.isRefreshing = false
            }, 2000) // Delay in millis
        }
        refreshSwipe.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        )
    }

    /*Request and RequestListener Section*/
    private val bannerRequest: Unit
        get() {
            val url = Api.url_banner
            APINetworkRequest(requireActivity(), bannerRequestListener, url, CODE_GET_REQUEST, null, "BANNER")
        }
    private val bannerRequestListener: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            dataViewModel.pushRequestResult(ID_BANNER_REQUEST, data!!)
            binding!!.rvBanner.visibility = View.VISIBLE
            binding!!.shimmerBanner.stopShimmer()
            binding!!.shimmerBanner.visibility = View.GONE
            try {
                val `object` = JSONObject(data)
                if (!`object`.getBoolean("error")) {
                    bannerListParser(`object`.getJSONArray("banner"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            binding!!.shimmerBanner.stopShimmer()
            binding!!.shimmerBanner.startShimmer()
            binding!!.shimmerBanner.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
            binding!!.rvBanner.visibility = View.GONE
            binding!!.shimmerBanner.startShimmer()
            binding!!.shimmerBanner.visibility = View.VISIBLE
        }
    }

    private fun bannerListParser(banner: JSONArray) {
        try {
            val bannerListModels = ArrayList<BannerListMdl>()
            for (i in 0 until banner.length()) {
                val `object` = banner.getJSONObject(i)
                val bannerImage = `object`.getString("banner_image")
                val bannerUrl = `object`.getString("banner_url")
                val bannerTitle = `object`.getString("banner_title")
                bannerListModels.add(BannerListMdl(bannerImage, bannerTitle, bannerUrl))
            }
            val adapterBanner = BannerAdapter(bannerListModels, requireActivity(), R.layout.rv_banner)
            binding!!.rvBanner.adapter = adapterBanner
            bannerScrolling = Handler()
            bannerRunnable = Runnable {
                val currentBanner = binding!!.rvBanner.currentItem
                if (this.isVisible && bannerListModels.size == binding!!.rvBanner.adapter!!.itemCount) {
                    if (bannerListModels.size - 1 > currentBanner) {
                        binding!!.rvBanner.smoothScrollToPosition(currentBanner + 1)
                        binding!!.rvBanner.setItemTransitionTimeMillis(250)
                    } else {
                        binding!!.rvBanner.smoothScrollToPosition(0)
                        binding!!.rvBanner.setItemTransitionTimeMillis(250)
                    }
                    bannerScrolling!!.postDelayed(bannerRunnable!!, 5000)
                }
            }
            bannerScrolling!!.postDelayed(bannerRunnable!!, 5000)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val lastAnimeRequest: Unit
        get() {
            val url = Api.url_packagepage + "1"
            APINetworkRequest(requireActivity(), lastAnimeRequestListener, url, CODE_GET_REQUEST, null, "ANIMELAST")
        }

    private val lastAnimeRequestListener: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            dataViewModel.pushRequestResult(ID_LAST_PACKAGE_REQUEST, data!!)
            binding!!.rvNewAnime.visibility = View.VISIBLE
            binding!!.shimmerNewAnime.stopShimmer()
            binding!!.shimmerNewAnime.visibility = View.GONE
            try {
                val `object` = JSONObject(data)
                if (!`object`.getBoolean("error")) {
                    lastAnimeParser(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            binding!!.shimmerNewAnime.startShimmer()
            binding!!.shimmerNewAnime.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
            binding!!.shimmerNewAnime.startShimmer()
            binding!!.shimmerNewAnime.visibility = View.VISIBLE
            binding!!.rvNewAnime.visibility = View.GONE
        }
    }

    private fun lastAnimeParser(anim: JSONArray) {
        try {
            val modelDataPackage = ArrayList<PackageList>()
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
                val genres: MutableList<String> = java.util.ArrayList()
                for (j in 0 until genre_json.length()) {
                    genres.add(genre_json.getString(j))
                }
                modelDataPackage.add(PackageList(packages, nameanim, nowep, totep, rate, mal, genres, cover))
            }
            val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val adapterLastAnime = LastPackageAdapter(modelDataPackage, requireActivity(), R.layout.rv_newanime)
            binding!!.rvNewAnime.layoutManager = linearLayoutManager
            binding!!.rvNewAnime.adapter = adapterLastAnime
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    private val lastEpisodeRequest: Unit
        get() {
            val url = Api.url_page + "1"
            APINetworkRequest(requireActivity(), lastEpisodeRequestListener, url, CODE_GET_REQUEST, null, "EPISODELAST")
        }

    private val lastEpisodeRequestListener: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            dataViewModel.pushRequestResult(ID_LAST_EPISODE_REQUEST, data!!)
            binding!!.rvNewEpisode.visibility = View.VISIBLE
            binding!!.shimmerNewEpisode.stopShimmer()
            binding!!.shimmerNewEpisode.visibility = View.GONE
            try {
                val `object` = JSONObject(data)
                if (!`object`.getBoolean("error")) {
                    lastEpisodeParser(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            binding!!.shimmerNewEpisode.startShimmer()
            binding!!.shimmerNewEpisode.visibility = View.VISIBLE
            Log.e("ERROR", msg!!)
        }

        override fun onFetchStart() {
            binding!!.shimmerNewEpisode.startShimmer()
            binding!!.shimmerNewEpisode.visibility = View.VISIBLE
            binding!!.rvNewEpisode.visibility = View.GONE
        }
    }

    private fun lastEpisodeParser(video: JSONArray) {
        try {
            val modelDataLastUp = ArrayList<HomeLastUploadModel>()
            binding!!.rvNewEpisode.visibility = View.VISIBLE
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val urlThumbnail = jsonObject.getString(Api.JSON_episode_thumb)
                val id = jsonObject.getString(Api.JSON_id_anim)
                val titleName = jsonObject.getString(Api.JSON_name_anim)
                val episode = jsonObject.getString(Api.JSON_episode_anim)
                modelDataLastUp.add(HomeLastUploadModel(urlThumbnail, id, titleName, episode))
            }
            val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val adapterLastUp = HomeLastUpAdapter(modelDataLastUp, requireActivity(), R.layout.rv_newepisode)
            binding!!.rvNewEpisode.adapter = adapterLastUp
            binding!!.rvNewEpisode.layoutManager = linearLayoutManager
        } catch (e: JSONException) {
            Log.e("ERROR JSON:", e.toString())
        }
    }

    /*End Of Request Section*/

    private fun scrollToTop() {
        binding!!.dashboardScroll.fullScroll(View.FOCUS_UP)
        commonViewModel.changeDashboardScrolledToTop()
    }

    override fun onPause() {
        if (bannerScrolling != null) {
            bannerScrolling!!.removeCallbacks(bannerRunnable!!)
        }
        RequestQueueVolley.getInstance(requireContext())!!.cancelRequestByTAG(listOf("BANNER", "ANIMELAST", "EPISODELAST"))
        super.onPause()
    }
}