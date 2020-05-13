/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wang.avi.AVLoadingIndicatorView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.VideoListModel
import ml.dvnlabs.animize.ui.activity.DashboardActivity
import ml.dvnlabs.animize.ui.recyclerview.EndlessRecyclerScrollListener
import ml.dvnlabs.animize.ui.recyclerview.list.video_list_adapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LastUpAnime : Fragment(), View.OnClickListener {
    private var listView: RecyclerView? = null
    var modelData: ArrayList<VideoListModel>? = null
    var adapter: video_list_adapter? = null
    private var progressBar: AVLoadingIndicatorView? = null
    private var textload: TextView? = null
    var textErrorLoad: TextView? = null
    var buttonRetry: Button? = null
    var ivError: ImageView? = null
    private var swipeList: SwipeRefreshLayout? = null
    private var pageList = 1
    private var handler: Handler? = null
    private var isRefreshing = false
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        handler = Handler()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lastup_list, container, false)
        listView = view.findViewById<View>(R.id.PlayVideoList) as RecyclerView
        swipeList = view.findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        buttonRetry = view.findViewById<View>(R.id.loading_retry) as Button
        progressBar = view.findViewById<View>(R.id.progressBar) as AVLoadingIndicatorView
        textload = view.findViewById<View>(R.id.loading_text) as TextView
        textErrorLoad = view.findViewById<View>(R.id.loading_errortxt) as TextView
        ivError = view.findViewById<View>(R.id.error_image) as ImageView
        ivError!!.visibility = View.GONE
        textErrorLoad!!.visibility = View.GONE
        buttonRetry!!.visibility = View.GONE
        buttonRetry!!.setOnClickListener(this)
        layoutManager = LinearLayoutManager(activity)
        val a = "INIT"
        Log.e("INF", a)
        //Call initialize like init array()
        initialize()
        getList()
        swipeList!!.setOnRefreshListener {
            isRefreshing = true

            modelData!!.clear()
            pageList = 1
            getList()

            buttonRetry!!.visibility = View.GONE
            textErrorLoad!!.visibility = View.GONE
            ivError!!.visibility = View.GONE

            // To keep animateView for 4 seconds
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                swipeList!!.isRefreshing = false
                pageList += 1
            }, 2000) // Delay in millis
        }
        swipeList!!.setColorSchemeColors(
                resources.getColor(android.R.color.holo_blue_bright),
                resources.getColor(android.R.color.holo_green_light),
                resources.getColor(android.R.color.holo_orange_light),
                resources.getColor(android.R.color.holo_red_light)
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
                ivError!!.visibility = View.GONE
                buttonRetry!!.visibility = View.GONE
                textErrorLoad!!.visibility = View.GONE
            }
            if (`object`.getBoolean("error") && `object`.getString("message") == "page not found") {
                pageList = 1
                println("ALL DATA CAUGHT UP!")
                ivError!!.visibility = View.GONE
                buttonRetry!!.visibility = View.GONE
                textErrorLoad!!.visibility = View.GONE
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
            ivError!!.visibility = View.VISIBLE
            textErrorLoad!!.visibility = View.VISIBLE
            buttonRetry!!.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
        }
    }

    private fun retryLoad() {
        ivError!!.visibility = View.GONE
        textErrorLoad!!.visibility = View.GONE
        getList()
    }

    private fun showVideo(video: JSONArray) {
        listView!!.layoutManager = layoutManager
        listView!!.visibility = View.VISIBLE

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
                adapter = video_list_adapter(modelData, activity, listView)
                listView!!.adapter = adapter
            }
            Log.e("INFO COUNT ADAPT:", adapter!!.itemCount.toString())
            if (pageList > 1) {
                adapter!!.notifyItemChanged(0, adapter!!.itemCount)
                listView!!.invalidate()
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
        listView!!.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager!!) {
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