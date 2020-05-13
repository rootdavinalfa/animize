/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.popup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wang.avi.AVLoadingIndicatorView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.SourceList
import ml.dvnlabs.animize.ui.recyclerview.list.sourcelist_adapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class sourceSelector : BottomSheetDialogFragment(){
    private val CODE_GET_REQUEST = 1024
    private var sourcess: ArrayList<SourceList>? = null
    private var listview: RecyclerView? = null
    private var loading: AVLoadingIndicatorView? = null
    private var errors: TextView? = null
    private var infocontainer: RelativeLayout? = null
    private var adapter: sourcelist_adapter? = null

    private var mcontext: Context? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sheet_source, container, false)
        listview = view.findViewById(R.id.sourceslist_list)
        loading = view.findViewById(R.id.sourceslist_loading)
        errors = view.findViewById(R.id.sourceslist_error)
        infocontainer = view.findViewById(R.id.sourceslist_infocontainer)
        listview!!.visibility = View.GONE
        infocontainer!!.visibility = View.VISIBLE
        loading!!.visibility = View.VISIBLE

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    fun dismissselector() {
        dismiss()
    }
    fun language(lang: String, idanim: String, context: Context) {
        this.mcontext = context
        val url = Api.url_sourcelist + idanim + "/lang=" + lang
        println("URL SOURCES GET: $url")
        getContext()?.let { APINetworkRequest(it, fetchSource, url, CODE_GET_REQUEST, null) }
    }
    private var fetchSource : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    listview!!.visibility =View.VISIBLE
                    infocontainer!!.visibility=View.GONE
                    loading!!.visibility =View.GONE
                    errors!!.visibility=View.GONE
                    m_fetchsource(`object`.getJSONArray("anim"))
                } else {
                    listview!!.visibility =View.GONE
                    infocontainer!!.visibility =View.VISIBLE
                    loading!!.visibility=View.GONE
                    errors!!.visibility=View.VISIBLE
                }
            } catch (e: JSONException) {
                Log.e("ERROR:", e.toString())
            }
        }
        override fun onFetchFailure(msg: String?) {
            Log.e("WEB ERROR:", msg!!)
        }
        override fun onFetchStart() {
        }
    }
    private fun m_fetchsource(source: JSONArray) {
        try {
            val linearLayoutManager = LinearLayoutManager(context)
            sourcess = ArrayList()
            for (i in 0 until source.length()) {
                val `object` = source.getJSONObject(i)
                val id = `object`.getString("id_source")
                val by_user = `object`.getString("by_user")
                val sources = `object`.getString("source")
                sourcess!!.add(SourceList(id, by_user, sources))
            }
            adapter = sourcelist_adapter(sourcess, mcontext, R.layout.rv_sources)
            listview!!.layoutManager =linearLayoutManager
            listview!!.adapter=adapter


        } catch (e: JSONException) {
            Log.e("ERROR", e.toString())
        }

    }
}