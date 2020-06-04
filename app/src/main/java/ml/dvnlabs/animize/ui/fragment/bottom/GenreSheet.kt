/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.bottom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.databinding.FragmentGenreSheetBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.network.APINetworkRequest
import ml.dvnlabs.animize.driver.util.network.RequestQueueVolley
import ml.dvnlabs.animize.driver.util.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.GenrePackageList
import ml.dvnlabs.animize.ui.recyclerview.packagelist.GenrePackageListAdapter
import ml.dvnlabs.animize.view.AutoGridLayoutManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class GenreSheet : BottomSheetDialogFragment() {
    private var modelDataPackage: ArrayList<GenrePackageList>? = null
    private var adapterLastPackage: GenrePackageListAdapter? = null
    private var layoutManager: AutoGridLayoutManager? = null

    private var binding : FragmentGenreSheetBinding? = null


    var genre: String? = null

    override fun onDetach() {
        RequestQueueVolley.getInstance(requireActivity())!!.cancelRequestByTAG(genre!!)
        super.onDetach()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater.inflate(R.layout.fragment_genre_sheet,container,false)
        binding = FragmentGenreSheetBinding.inflate(inflater)
        if (arguments != null) {
            genre = requireArguments().getString("genre")
            modelDataPackage = ArrayList()
            getGenrePackage()
        }
        return binding!!.root
    }


    private fun getGenrePackage() {
        binding!!.genreName.text = genre!!
        val url = Api.url_genrelist + genre
        APINetworkRequest(requireActivity(), genrePackage, url, CODE_GET_REQUEST, null,genre!!)
    }

    private var genrePackage: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            binding!!.genreListPackage.visibility = View.VISIBLE
            binding!!.genrePackagelistLoadsing.visibility = View.GONE
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
            binding!!.genreListPackage.visibility = View.GONE
            binding!!.genrePackagelistLoadsing.visibility = View.VISIBLE
        }
    }

    private fun genrePackageList(anim: JSONArray) {
        try {
            modelDataPackage!!.clear()
            for (i in 0 until anim.length()) {
                val `object` = anim.getJSONObject(i)
                val packages = `object`.getString("package_anim")
                val nameanim = `object`.getString("name_catalogue")
                val nowep = `object`.getString("now_ep_anim")
                val totep = `object`.getString("total_ep_anim")
                val rate = `object`.getString("rating")
                val mal = `object`.getString("mal_id")
                val cover = `object`.getString("cover")
                modelDataPackage!!.add(GenrePackageList(packages, nameanim, nowep, totep, rate, mal, cover))
            }
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            layoutManager = AutoGridLayoutManager(context, 500)
            adapterLastPackage = GenrePackageListAdapter(modelDataPackage, requireActivity(), R.layout.rv_genrepackage)
            binding!!.genreListPackage.layoutManager = layoutManager
            binding!!.genreListPackage.adapter = adapterLastPackage
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
    }
}