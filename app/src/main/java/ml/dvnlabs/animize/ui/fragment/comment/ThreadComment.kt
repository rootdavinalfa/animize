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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.APIUserModel
import ml.dvnlabs.animize.model.CommentMainModel
import ml.dvnlabs.animize.ui.activity.StreamActivity
import ml.dvnlabs.animize.ui.recyclerview.comment.CommentThreadAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ThreadComment private constructor(models: ArrayList<CommentMainModel>, idanim: String) : Fragment() {
    private var commentuser: TextView? = null
    private var content: TextView? = null
    private val models: ArrayList<CommentMainModel>?
    private var repliedmodel: ArrayList<CommentMainModel>? = null
    private var closebutton: ImageView? = null
    private var addsreply: ImageView? = null
    private var adapter: CommentThreadAdapter? = null
    private var listreply: RecyclerView? = null
    private var replybox: EditText? = null
    private var pref: SharedPreferences? = null
    private val id_anim: String
    private var parentcomment: String? = null
    private var id_user: String? = null
    private var token: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment_thread, container, false)
        commentuser = view.findViewById(R.id.comments_thread_users)
        content = view.findViewById(R.id.comments_thread_content)
        closebutton = view.findViewById(R.id.comments_thread_close)
        listreply = view.findViewById(R.id.comments_thread_replied)
        addsreply = view.findViewById(R.id.comments_thread_adds)
        addsreply!!.setOnClickListener { addReply() }
        replybox = view.findViewById(R.id.comments_thread_edittext)
        closebutton!!.setOnClickListener { (requireActivity() as StreamActivity).closeReplyFragment() }
        receiveData()
        commenteditlistener()
        return view
    }

    //Listener for commentedit
    private fun commenteditlistener() {
        addsreply!!.visibility = View.GONE
        replybox!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //addsbtn.setVisibility(View.GONE);
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (replybox!!.text.length > 0) {
                    addsreply!!.visibility = View.VISIBLE
                } else {
                    addsreply!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                //addsbtn.setVisibility(View.VISIBLE);
            }
        })
    }

    private fun receiveData() {
        if (models != null) {
            val users = models[0].userModels
            val name = users[0].name_user
            val contents = models[0].contents
            println(name + contents)
            commentuser!!.text = name
            content!!.text = contents
            parentcomment = models[0].idComment
            requestReply()
        }
    }

    private fun requestReply() {
        val url = Api.url_commentthread + id_anim + "/parent/" + models!![0].idComment
        APINetworkRequest(requireContext(), fetchReply, url, 1024, null)
    }

    private fun setFetchReply(reply: JSONArray) {
        try {
            repliedmodel = ArrayList()
            for (i in 0 until reply.length()) {
                val `object` = reply.getJSONObject(i)
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
                repliedmodel!!.add(CommentMainModel(ids, status, content, usermodels))
            }
            adapter = CommentThreadAdapter(repliedmodel, requireContext(), R.layout.rv_comments_thread)
            val lnm = LinearLayoutManager(context)
            listreply!!.layoutManager = lnm
            listreply!!.adapter = adapter

            //GET preferences
            token = pref!!.getString("token", null)
            id_user = pref!!.getString("idUser", null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private var fetchReply: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    val comment = `object`.getJSONArray("comment") //Get array comment
                    val commentobj = comment.getJSONObject(0) //Get object on array 0
                    setFetchReply(commentobj.getJSONArray("reply"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            Log.e("ERROR:", msg!!)
        }

        override fun onFetchStart() {}
    }

    private fun addReply() {
        val url = Api.url_commentadd
        val content = replybox!!.text.toString()
        val params = HashMap<String, String>()
        params["id_user"] = id_user!!
        params["content"] = content
        params["id_anim"] = id_anim
        params["token"] = token!!
        params["parent"] = parentcomment!!
        APINetworkRequest(requireContext(), addReply, url, 1025, params)
    }

    private var addReply: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            replybox!!.text.clear()

            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    requestReply()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            Log.e("ERROR", msg!!)
        }

        override fun onFetchStart() {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = context.getSharedPreferences("aPlay", 0)
    }

    companion object {
        fun newInstance(models: ArrayList<CommentMainModel>, idanim: String): ThreadComment {
            return ThreadComment(models, idanim)
        }
    }

    init {
        this.models = models
        id_anim = idanim
    }
}