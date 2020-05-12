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
import android.os.Parcelable
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
import ml.dvnlabs.animize.model.video_list_model
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
    private val recyclerViewState: Parcelable? = null
    var listView: RecyclerView? = null
    var modeldata: ArrayList<video_list_model>? = null
    var adapter: video_list_adapter? = null
    var progressBar: AVLoadingIndicatorView? = null
    var textload: TextView? = null
    var texterrorload: TextView? = null
    var btn_retry: Button? = null
    var iv_error: ImageView? = null
    var swipe_list: SwipeRefreshLayout? = null
    private var page_list = 1
    private var handler: Handler? = null
    private var isRefreshing = false
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        handler = Handler()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lastup_list, container, false)
        listView = view.findViewById<View>(R.id.PlayVideoList) as RecyclerView
        swipe_list = view.findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        btn_retry = view.findViewById<View>(R.id.loading_retry) as Button
        progressBar = view.findViewById<View>(R.id.progressBar) as AVLoadingIndicatorView
        textload = view.findViewById<View>(R.id.loading_text) as TextView
        texterrorload = view.findViewById<View>(R.id.loading_errortxt) as TextView
        iv_error = view.findViewById<View>(R.id.error_image) as ImageView
        iv_error!!.visibility = View.GONE
        //textload.setVisibility(View.VISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
        texterrorload!!.visibility = View.GONE
        btn_retry!!.visibility = View.GONE
        btn_retry!!.setOnClickListener(this)
        layoutManager = LinearLayoutManager(activity)
        val a = "INIT"
        Log.e("INF", a)
        //Call initialize like init array()
        initialize()
        getList()
        swipe_list!!.setOnRefreshListener {
            isRefreshing = true
            //listView.setVisibility(View.GONE);
            //adapter.notifyDataSetChanged();
            modeldata!!.clear()
            page_list = 1
            getList()
            //getLoaderManager().restartLoader(0,null,lastup_video_list.this);
            btn_retry!!.visibility = View.GONE
            texterrorload!!.visibility = View.GONE
            iv_error!!.visibility = View.GONE
            //getJson();
            // Your code here

            // To keep animateView for 4 seconds
            Handler().postDelayed({ // Stop animateView (This will be after 3 seconds)
                swipe_list!!.isRefreshing = false
                page_list = page_list + 1
            }, 2000) // Delay in millis
        }
        swipe_list!!.setColorSchemeColors(
                resources.getColor(android.R.color.holo_blue_bright),
                resources.getColor(android.R.color.holo_green_light),
                resources.getColor(android.R.color.holo_orange_light),
                resources.getColor(android.R.color.holo_red_light)
        )
        return view
    }

    private fun initialize() {
        modeldata = ArrayList()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        //getLoaderManager().initLoader(0,null,lastup_video_list.this);
    }

    override fun onStop() {
        super.onStop()
        if (!modeldata!!.isEmpty()) {
            modeldata!!.clear()
        }
    }

    override fun onClick(view: View) {
        retryLoad()
    }

    //VOLLEY NETWORKING
    fun getList() {
        try {
            println("NOW PAGE::$page_list")
            val url = Api.url_page + page_list
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
                iv_error!!.visibility = View.GONE
                btn_retry!!.visibility = View.GONE
                texterrorload!!.visibility = View.GONE
            }
            if (`object`.getBoolean("error") && `object`.getString("message") == "page not found") {
                page_list = 1
                println("ALL DATA CAUGHT UP!")
                //progressBar.setVisibility(View.GONE);
                //textload.setVisibility(View.GONE);
                iv_error!!.visibility = View.GONE
                btn_retry!!.visibility = View.GONE
                texterrorload!!.visibility = View.GONE
            }
        } catch (e: JSONException) {
            Log.e("JSONE", e.message!!)
        }
    }

    var getvideolist: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String) {
            videoListJSON(data)
        }

        override fun onFetchFailure(msg: String) {
            (activity as DashboardActivity?)!!.snackError(msg, 1)
            iv_error!!.visibility = View.VISIBLE
            texterrorload!!.visibility = View.VISIBLE
            btn_retry!!.visibility = View.VISIBLE
        }

        override fun onFetchStart() {
        }
    }

    private fun retryLoad() {
        iv_error!!.visibility = View.GONE
        texterrorload!!.visibility = View.GONE
        getList()
    }

    private fun showVideo(video: JSONArray) {
        listView!!.layoutManager = layoutManager
        listView!!.visibility = View.VISIBLE

        //recyclerViewState = layoutManager.onSaveInstanceState();
        if (page_list == 1) {
            modeldata!!.clear()
        }
        try {
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val url_tb = jsonObject.getString(Api.JSON_episode_thumb)
                val id = jsonObject.getString(Api.JSON_id_anim)
                val title_name = jsonObject.getString(Api.JSON_name_anim)
                val episode = jsonObject.getString(Api.JSON_episode_anim)
                modeldata!!.add(video_list_model(url_tb, id, title_name, episode))
            }
            if (page_list == 1) {
                adapter = video_list_adapter(modeldata, activity, listView)
                listView!!.adapter = adapter
            }
            Log.e("INFO COUNT ADAPT:", adapter!!.itemCount.toString())
            if (page_list > 1) {
                adapter!!.notifyItemChanged(0, adapter!!.itemCount)
                listView!!.invalidate()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (!isRefreshing) {
            onItemScrolled()
            page_list += 1
        }
        if (isRefreshing) {
            isRefreshing = false
            page_list = 1
            onItemScrolled()
            println("REFRESHING")
        }
    }

    private fun onItemScrolled() {
        listView!!.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                modeldata!!.add(video_list_model(null, null, null, null))
                //adapter.notifyItemInserted(modeldata.size());
                adapter!!.notifyDataSetChanged()
                handler!!.postDelayed({
                    //remove progress item
                    modeldata!!.removeAt(modeldata!!.size - 1)
                    adapter!!.notifyItemRemoved(modeldata!!.size)
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