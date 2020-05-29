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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentLastPackageBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.PackageList
import ml.dvnlabs.animize.ui.recyclerview.EndlessRecyclerScrollListener
import ml.dvnlabs.animize.ui.recyclerview.packagelist.NewPackageAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LastPackageAnime : Fragment() {
    private var modelData : ArrayList<PackageList>? = null
    private var binding : FragmentLastPackageBinding? = null
    private var adapterLastAnime : NewPackageAdapter? = null
    private var handler : Handler? = null
    private var layoutManager : LinearLayoutManager? = null
    private var pageList = 1
    init {
        handler = Handler()
        modelData = ArrayList()
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_last_package,container,false)
        binding = FragmentLastPackageBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(requireContext())
        lastAnimeRequest
    }

    private val lastAnimeRequest: Unit
        get() {
            val url = Api.url_packagepage + pageList
            APINetworkRequest(requireActivity(), lastAnimeRequestListener, url, CODE_GET_REQUEST, null)
        }

    private val lastAnimeRequestListener: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    lastAnimeParser(`object`.getJSONArray("anim"))
                }
                if (`object`.getBoolean("error")) {
                    pageList = 1
                    println("ALL DATA CAUGHT UP!")
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

    private fun lastAnimeParser(anim: JSONArray) {
        if (pageList == 1) {
            modelData!!.clear()
        }
        try {
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
                modelData!!.add(PackageList(packages, nameanim, nowep, totep, rate, mal, genres, cover))
            }
            if (pageList == 1){
                adapterLastAnime = NewPackageAdapter(requireContext(),modelData!!)
                binding!!.rvLastPackage.layoutManager = layoutManager
                binding!!.rvLastPackage.adapter = adapterLastAnime
            }else if (pageList > 1){
                adapterLastAnime!!.notifyItemChanged(0, adapterLastAnime!!.itemCount)
                binding!!.rvLastPackage.invalidate()
            }
            pageList += 1
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
        onItemScrolled()
    }

    private fun onItemScrolled() {
        binding!!.rvLastPackage.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                modelData!!.add(PackageList("", "", "", "","","",null,""))
                adapterLastAnime!!.notifyDataSetChanged()
                handler!!.postDelayed({
                    //remove progress item
                    modelData!!.removeAt(modelData!!.size - 1)
                    adapterLastAnime!!.notifyItemRemoved(modelData!!.size)
                    lastAnimeRequest

                }, 500)
            }
        })
    }
}