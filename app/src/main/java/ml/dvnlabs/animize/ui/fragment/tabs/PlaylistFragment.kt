/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.tabs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.recyclerview.list.PlayListAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PlaylistFragment : Fragment(){
    var mcontext : Context? = null
    val CODE_GET_REQUEST : Int = 1024
    private var playlist_models: ArrayList<PlaylistModel>? = null
    var adapter: PlayListAdapter?= null
    private var listview: RecyclerView? = null
    var pkganim: String? = null;var id_anim:String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mcontext = context
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.ap_fragment_more_tabs, container, false)
        listview = view.findViewById(R.id.playlist_list)
        return  view
    }
    private fun getPlayList(){
        val url : String = Api.url_playlist_play + pkganim
        mcontext?.let { APINetworkRequest(it, getPlaylist, url, CODE_GET_REQUEST, null) }
    }
    private var getPlaylist : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    parsePlaylist(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        override fun onFetchFailure(msg: String?) {

        }

        override fun onFetchStart() {

        }
    }
    private fun parsePlaylist(playlist : JSONArray){
        try {
            playlist_models = ArrayList()
            val layoutManager = LinearLayoutManager(activity)
            listview!!.layoutManager = layoutManager

            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val url_img = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val id_an = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                playlist_models!!.add(PlaylistModel(url_img, title, episode, id_an, pkg))

            }
            adapter = PlayListAdapter(playlist_models!!, requireActivity(), R.layout.playlist_view, id_anim!!)
            listview!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun receiveData(pkg: String, anim: String) {
        pkganim = pkg
        id_anim = anim
        getPlayList()
    }
}