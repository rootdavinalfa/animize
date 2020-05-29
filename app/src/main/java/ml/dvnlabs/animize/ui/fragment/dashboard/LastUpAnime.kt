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
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentLastupListBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.VideoListModel
import ml.dvnlabs.animize.ui.activity.DashboardActivity
import ml.dvnlabs.animize.ui.recyclerview.EndlessRecyclerScrollListener
import ml.dvnlabs.animize.ui.recyclerview.list.VideoListAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LastUpAnime : Fragment(), View.OnClickListener {
    var modelData: ArrayList<VideoListModel>? = null
    var adapter: VideoListAdapter? = null
    private var pageList = 1
    private var handler: Handler? = null
    private var isRefreshing = false
    private var layoutManager: LinearLayoutManager? = null

    private var binding : FragmentLastupListBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        handler = Handler()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lastup_list, container, false)
        binding = FragmentLastupListBinding.bind(view)

        binding!!.errorImage.visibility = View.GONE
        binding!!.loadingErrortxt.visibility = View.GONE
        binding!!.loadingRetry.visibility = View.GONE
        binding!!.loadingRetry.setOnClickListener(this)
        layoutManager = LinearLayoutManager(activity)

        //Call initialize like init array()
        initialize()
        getList()
        binding!!.swipeContainer.setOnRefreshListener {
            isRefreshing = true

            modelData!!.clear()
            pageList = 1
            getList()

            binding!!.loadingRetry.visibility = View.GONE
            binding!!.loadingErrortxt.visibility = View.GONE
            binding!!.errorImage.visibility = View.GONE

            // To keep animateView for 4 seconds
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                binding!!.swipeContainer.isRefreshing = false
                pageList += 1
            }, 2000) // Delay in millis
        }
        binding!!.swipeContainer.setColorSchemeColors(
                ContextCompat.getColor(requireContext(),android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(),android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(),android.R.color.holo_red_light)
        )
        return view
    }

    private fun initialize() {
        modelData = ArrayList()
    }

    override fun onStop() {
        super.onStop()
        if (modelData!!.isNotEmpty()) {
            modelData!!.clear()
        }
    }

    override fun onClick(view: View) {
        retryLoad()
    }

    //VOLLEY NETWORKING
    fun getList() {
        try {
            println("NOW PAGE::$pageList")
            val url = Api.url_page + pageList
            APINetworkRequest(requireActivity(), getvideolist, url, CODE_GET_REQUEST, null)
        } catch (e: Exception) {
            Log.e("ERROR", e.message!!)
        }
    }

    private fun videoListJSON(data: String) {
        try {
            val `object` = JSONObject(data)
            if (!`object`.getBoolean("error")) {
                showVideo(`object`.getJSONArray("anim"))
                binding!!.errorImage.visibility = View.GONE
                binding!!.loadingRetry.visibility = View.GONE
                binding!!.loadingErrortxt.visibility = View.GONE
            }
            if (`object`.getBoolean("error") && `object`.getString("message") == "page not found") {
                pageList = 1
                println("ALL DATA CAUGHT UP!")
                binding!!.errorImage.visibility = View.GONE
                binding!!.loadingRetry.visibility = View.GONE
                binding!!.loadingErrortxt.visibility = View.GONE
            }
        } catch (e: JSONException) {
            Log.e("JSONE", e.message!!)
        }
    }

    var getvideolist: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            videoListJSON(data!!)
        }

        override fun onFetchFailure(msg: String?) {
            (activity as DashboardActivity?)!!.snackError(msg!!, 1)
            binding!!.errorImage.visibility = View.VISIBLE
            binding!!.loadingRetry.visibility = View.VISIBLE
            binding!!.loadingErrortxt.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
        }
    }

    private fun retryLoad() {
        binding!!.errorImage.visibility = View.GONE
        binding!!.loadingErrortxt.visibility = View.GONE
        getList()
    }

    private fun showVideo(video: JSONArray) {
        binding!!.PlayVideoList.layoutManager = layoutManager
        binding!!.PlayVideoList.visibility = View.VISIBLE

        //recyclerViewState = layoutManager.onSaveInstanceState();
        if (pageList == 1) {
            modelData!!.clear()
        }
        try {
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val url_tb = jsonObject.getString(Api.JSON_episode_thumb)
                val id = jsonObject.getString(Api.JSON_id_anim)
                val title_name = jsonObject.getString(Api.JSON_name_anim)
                val episode = jsonObject.getString(Api.JSON_episode_anim)
                modelData!!.add(VideoListModel(url_tb, id, title_name, episode))
            }
            if (pageList == 1) {
                adapter = VideoListAdapter(modelData, requireActivity(), binding!!.PlayVideoList)
                binding!!.PlayVideoList.adapter = adapter
            }
            Log.e("INFO COUNT ADAPT:", adapter!!.itemCount.toString())
            if (pageList > 1) {
                adapter!!.notifyItemChanged(0, adapter!!.itemCount)
                binding!!.PlayVideoList.invalidate()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (!isRefreshing) {
            onItemScrolled()
            pageList += 1
        }
        if (isRefreshing) {
            isRefreshing = false
            pageList = 1
            onItemScrolled()
            println("REFRESHING")
        }
    }

    private fun onItemScrolled() {
        binding!!.PlayVideoList.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                modelData!!.add(VideoListModel(null, null, null, null))
                adapter!!.notifyDataSetChanged()
                handler!!.postDelayed({
                    //remove progress item
                    modelData!!.removeAt(modelData!!.size - 1)
                    adapter!!.notifyItemRemoved(modelData!!.size)
                    getList()

                }, 500)
            }
        })
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val CODE_POST_REQUEST = 1025
        fun newInstance(): LastUpAnime {
            return LastUpAnime()
        }
    }
}