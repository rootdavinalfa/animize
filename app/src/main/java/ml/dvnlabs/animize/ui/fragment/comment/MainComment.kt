/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.fragment.comment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.APIUserModel
import ml.dvnlabs.animize.model.CommentMainModel
import ml.dvnlabs.animize.ui.recyclerview.comment.CommentMainAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainComment : Fragment() {
    private var commentMainModels: ArrayList<CommentMainModel>? = null
    private var commentar: RecyclerView? = null
    private var adapter: CommentMainAdapter? = null
    private var contain: LinearLayout? = null
    private var loads: RelativeLayout? = null
    private var commentedit: EditText? = null
    private var addsbtn: ImageView? = null
    private var pref: SharedPreferences? = null
    private var idanim: String? = null
    private var token: String? = null
    private var id_user: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)
        commentar = view.findViewById(R.id.aplay_fragment_comment_rv)
        contain = view.findViewById(R.id.aplay_fragment_comment_container)
        loads = view.findViewById(R.id.aplay_fragment_comment_loading)
        commentedit = view.findViewById(R.id.aplay_comments_edittext)
        addsbtn = view.findViewById(R.id.aplay_comments_adds)
        commentEditListener()
        addsbtn!!.setOnClickListener { addComments() }
        contain!!.visibility = View.GONE
        loads!!.visibility = View.VISIBLE
        return view
    }

    private fun fetchingComment() {
        val url = Api.url_commentlist + idanim
        APINetworkRequest(requireContext(), fetchComment, url, 1024, null)
    }

    fun receiveData(id_anim: String?) {
        idanim = id_anim
        fetchingComment()
    }

    //Listener for commentedit
    private fun commentEditListener() {
        addsbtn!!.visibility = View.GONE
        commentedit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (commentedit!!.text.isNotEmpty()) {
                    addsbtn!!.visibility = View.VISIBLE
                } else {
                    addsbtn!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    //SHOW COMMENT AFTER GET String data from web
    private fun showingComment(comment: JSONArray) {
        try {
            commentMainModels = ArrayList()
            for (i in 0 until comment.length()) {
                val `object` = comment.getJSONObject(i)
                val ids = `object`.getString("id_comment")
                val status = `object`.getString("status")
                val content = `object`.getString("content")
                val user = `object`.getJSONArray("user")
                val usermodels = ArrayList<APIUserModel>()
                for (j in 0 until user.length()) {
                    val jsonObject = user.getJSONObject(j)
                    val username = jsonObject.getString("username")
                    val nameus = jsonObject.getString("name_user")
                    usermodels.add(APIUserModel(username, nameus))
                }
                commentMainModels!!.add(CommentMainModel(ids, status, content, usermodels))
            }
            adapter = CommentMainAdapter(commentMainModels, requireContext(), R.layout.rv_comments)
            val lnm = LinearLayoutManager(context)
            commentar!!.layoutManager = lnm
            commentar!!.adapter = adapter

            //Get token and iduser
            token = pref!!.getString("token", null)
            id_user = pref!!.getString("idUser", null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private var fetchComment: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            contain!!.visibility = View.VISIBLE
            loads!!.visibility = View.GONE
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    showingComment(`object`.getJSONArray("comment"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            contain!!.visibility = View.VISIBLE
            loads!!.visibility = View.GONE
        }

        override fun onFetchStart() {}
    }

    private fun addComments() {
        val url = Api.url_commentadd
        val content = commentedit!!.text.toString()
        val params = HashMap<String, String>()
        params["id_user"] = id_user!!
        params["content"] = content
        params["id_anim"] = idanim!!
        params["token"] = token!!
        APINetworkRequest(requireContext(), addComment, url, 1025, params)
    }

    private var addComment: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            commentedit!!.text.clear()

            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    fetchingComment()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            Log.e("ERROR:", msg!!)
        }

        override fun onFetchStart() {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = context.getSharedPreferences("aPlay", 0)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MainComment {
            return MainComment()
        }
    }
}