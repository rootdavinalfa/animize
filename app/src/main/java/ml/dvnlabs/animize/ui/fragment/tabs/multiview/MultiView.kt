/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.tabs.multiview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.GenrePackageList
import ml.dvnlabs.animize.ui.recyclerview.packagelist.genre_packagelist_adapter
import ml.dvnlabs.animize.view.AutoGridLayoutManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MultiView : DialogFragment() {
    private var modeldatapackage: ArrayList<GenrePackageList>? = null
    private var adapterlastpackage: genre_packagelist_adapter? = null
    private var LayoutManager: AutoGridLayoutManager? = null
    private var rv_listgenre: RecyclerView? = null
    private var genreloading: RelativeLayout? = null
    var genre: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_multiview, container, false)
        rv_listgenre = view.findViewById(R.id.genre_list_package)
        genreloading = view.findViewById(R.id.genre_packagelist_loads)
        rv_listgenre!!.visibility = View.GONE
        if (arguments != null) {
            genre = requireArguments().getString("genre")
            modeldatapackage = ArrayList()
            getGenrePackage()
        }
        return view
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    private fun getGenrePackage() {
        val url = Api.url_genrelist + genre
        APINetworkRequest(requireActivity(), genrePackage, url, CODE_GET_REQUEST, null)
    }

    private var genrePackage: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            rv_listgenre!!.visibility = View.VISIBLE
            genreloading!!.visibility = View.GONE
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    genrePackageList(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {
            rv_listgenre!!.visibility = View.GONE
            genreloading!!.visibility = View.VISIBLE
        }
    }

    private fun genrePackageList(anim: JSONArray) {
        try {
            modeldatapackage!!.clear()
            for (i in 0 until anim.length()) {
                val `object` = anim.getJSONObject(i)
                val packages = `object`.getString("package_anim")
                val nameanim = `object`.getString("name_catalogue")
                val nowep = `object`.getString("now_ep_anim")
                val totep = `object`.getString("total_ep_anim")
                val rate = `object`.getString("rating")
                val mal = `object`.getString("mal_id")
                val cover = `object`.getString("cover")
                modeldatapackage!!.add(GenrePackageList(packages, nameanim, nowep, totep, rate, mal, cover))
            }
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            LayoutManager = AutoGridLayoutManager(context, 500)
            adapterlastpackage = genre_packagelist_adapter(modeldatapackage, activity, R.layout.rv_genrepackage)
            rv_listgenre!!.layoutManager = LayoutManager
            rv_listgenre!!.adapter = adapterlastpackage
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024

        @JvmStatic
        fun newInstance(): MultiView {
            return MultiView()
        }
    }
}