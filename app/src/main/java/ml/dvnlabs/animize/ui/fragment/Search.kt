/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.search_list_model
import ml.dvnlabs.animize.model.search_list_pack_model
import ml.dvnlabs.animize.ui.recyclerview.list.search_list_adapter
import ml.dvnlabs.animize.ui.recyclerview.packagelist.search_package_adapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Search : Fragment() {
    private var listView: RecyclerView? = null
    private var packview: DiscreteScrollView? = null
    private var modeldata: ArrayList<search_list_model>? = null
    private var pack_models: ArrayList<search_list_pack_model>? = null
    private var adapter: search_list_adapter? = null
    private var package_adapter: search_package_adapter? = null
    private var srch_txt: EditText? = null
    private var src_error: TextView? = null
    private var title_pack: TextView? = null
    private var title_ep: TextView? = null
    private var srch_txt_get: String? = null
    private var srch_iv: ImageView? = null
    private var isReadyForNextReq = false
    private var search_runnable: Runnable? = null
    private var search_handler: Handler? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        listView = view.findViewById(R.id.search_listview)
        packview = view.findViewById(R.id.search_package_listview)
        srch_txt = view.findViewById(R.id.srch_txt_edit)
        src_error = view.findViewById(R.id.src_error_txt)
        srch_iv = view.findViewById(R.id.error_v_srch)
        title_ep = view.findViewById(R.id.search_ep_title)
        title_pack = view.findViewById(R.id.search_pack_title)
        title_pack!!.visibility = View.GONE
        title_ep!!.visibility = View.GONE
        modeldata = ArrayList()
        pack_models = ArrayList()
        initHandler()
        searchTextListener()
        // Inflate the layout for this fragment
        return view
    }

    private fun initHandler() {
        search_runnable = Runnable { getSearch() }
        search_handler = Handler()
    }

    private fun searchTextListener() {
        srch_txt!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == 66) {
                srch_txt!!.clearFocus()
                hideKeyboard(v)
                return@OnKeyListener true //this is required to stop sending key event to parent
            }
            false
        })
        srch_txt!!.addTextChangedListener(object : TextWatcher {
            var timing_text = 0
            var handler = Handler()
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                search_handler!!.removeCallbacks(search_runnable)
                src_error!!.visibility = View.GONE
                srch_txt_get = s.toString()
                val searchTextCount = srch_txt!!.text.length
                if (modeldata!!.isNotEmpty() && pack_models!!.isNotEmpty() && searchTextCount == 0) {
                    modeldata!!.clear()
                    pack_models!!.clear()
                    adapter!!.notifyDataSetChanged()
                    package_adapter!!.notifyDataSetChanged()
                    //System.out.println("TRIGGER");
                }
                if (searchTextCount > 0) {
                    if (modeldata!!.isNotEmpty() && pack_models!!.isNotEmpty()) {
                        modeldata!!.clear()
                        pack_models!!.clear()
                        adapter!!.notifyDataSetChanged()
                        package_adapter!!.notifyDataSetChanged()
                        //System.out.println("TRIGGER2");
                    }
                    /*search_text_count = search_text_count % 2;
                    if(search_text_count == 0 || isReadyForNextReq){
                        getSearch();
                    }*/search_handler!!.postDelayed(search_runnable, 2000)
                } else {
                    title_pack!!.visibility = View.GONE
                    title_ep!!.visibility = View.GONE
                    src_error!!.visibility = View.VISIBLE
                    val errormsg = resources.getString(R.string.search_not_provided)
                    src_error!!.text = errormsg
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getSearch() {
        if (srch_txt_get != "") {
            if (modeldata!!.isNotEmpty()) {
                modeldata!!.clear()
            }
            if (pack_models!!.isNotEmpty()) {
                pack_models!!.clear()
            }
            val url = Api.url_search + srch_txt_get
            println("TXT:$url")
            APINetworkRequest(requireActivity(), search, url, CODE_GET_REQUEST, null)
            val url_searchpack = Api.url_search_pack + srch_txt_get
            APINetworkRequest(requireActivity(), search_pack, url_searchpack, CODE_GET_REQUEST, null)
        } else {
            src_error!!.visibility = View.VISIBLE
            val errorMessage = resources.getString(R.string.search_not_provided)
            src_error!!.text = errorMessage
        }
    }

    private var search: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String) {
            isReadyForNextReq = true
            title_ep!!.visibility = View.VISIBLE
            srch_iv!!.visibility = View.GONE
            src_error!!.visibility = View.GONE
            jsonParserSearch(data)
        }

        override fun onFetchFailure(msg: String) {
            isReadyForNextReq = false
            title_ep!!.visibility = View.GONE
            srch_iv!!.visibility = View.VISIBLE
            src_error!!.visibility = View.VISIBLE
            src_error!!.text = msg
        }

        override fun onFetchStart() {
            title_ep!!.visibility = View.GONE
            isReadyForNextReq = false
        }
    }
    var search_pack: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String) {
            isReadyForNextReq = true
            title_pack!!.visibility = View.VISIBLE
            searchPackageJSONParser(data)
        }

        override fun onFetchFailure(msg: String) {
            isReadyForNextReq = false
            title_pack!!.visibility = View.GONE
        }

        override fun onFetchStart() {
            isReadyForNextReq = false
            title_pack!!.visibility = View.GONE
        }
    }

    private fun jsonParserSearch(data: String) {
        try {
            val `object` = JSONObject(data)
            if (!`object`.getBoolean("error")) {
                showVideo(`object`.getJSONArray("anim"))
            } else {
                src_error!!.visibility = View.VISIBLE
                src_error!!.text = "Your keyword seem wrong,please try another keyword\n"
                src_error!!.text = `object`.getString("message")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun searchPackageJSONParser(data: String) {
        try {
            val `object` = JSONObject(data)
            if (!`object`.getBoolean("error")) {
                showPack(`object`.getJSONArray("package"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showPack(pack: JSONArray) {
        try {
            for (i in 0 until pack.length()) {
                val jsonObject = pack.getJSONObject(i)
                val pkgid = jsonObject.getString("package_anim")
                val title = jsonObject.getString("name_catalogue")
                val now = jsonObject.getString("now_ep_anim")
                val total = jsonObject.getString("total_ep_anim")
                val rating = jsonObject.getString("rating")
                val cover = jsonObject.getString("cover")
                pack_models!!.add(search_list_pack_model(pkgid, title, now, total, rating, cover))
            }
            package_adapter = search_package_adapter(pack_models, activity, R.layout.rv_package_minimal)
            packview!!.adapter = package_adapter
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showVideo(video: JSONArray) {
        listView!!.visibility = View.VISIBLE
        try {
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val url_tb = jsonObject.getString(Api.JSON_episode_thumb)
                val id = jsonObject.getString(Api.JSON_id_anim)
                val title_name = jsonObject.getString(Api.JSON_name_anim)
                val episode = jsonObject.getString(Api.JSON_episode_anim)
                //Log.e("INF::",title_name);
                modeldata!!.add(search_list_model(url_tb, id, title_name, episode))
            }
            adapter = search_list_adapter(modeldata, activity, R.layout.search_list_view)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            listView!!.layoutManager = layoutManager
            listView!!.adapter = adapter
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //listView.addItemDecoration(itemDecorator);
    }

    private fun hideKeyboard(view: View) {
        val manager = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val CODE_POST_REQUEST = 1025
        fun newInstance(): Search {
            return Search()
        }
    }
}