/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.branch.referral.Branch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.Anime
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.databinding.ActivityPackageBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.RequestQueueVolley
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.PackageInfo
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.fragment.information.CoverView
import ml.dvnlabs.animize.ui.recyclerview.packagelist.PackageListAdapter
import ml.dvnlabs.animize.ui.recyclerview.staggered.PackageMetaGenreAdapter
import ml.dvnlabs.animize.view.AutoGridLayoutManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class PackageView : AppCompatActivity() {
    private var playlistModels: ArrayList<PlaylistModel>? = null
    private var modelinfo: ArrayList<PackageInfo>? = null
    var adapter: PackageListAdapter? = null
    var pkganim: String? = null

    private val animizeDB: AnimizeDatabase by inject()

    private var branch: Branch? = null

    private lateinit var binding: ActivityPackageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initD()
    }

    private fun initD() = CoroutineScope(Dispatchers.Main).launch {
        if (animizeDB.userDAO().countUser() != 0) {
            binding = ActivityPackageBinding.inflate(layoutInflater)
            setContentView(binding.root)

            pkganim = ""
            initialize()
            val intent = intent
            if (getIntent().getStringExtra("package") != null) {
                //setIdanim(intent.getStringExtra("id_anim"));
                pkganim = intent.getStringExtra("package")
                //intent.removeExtra("id_anim");
            }
            //assert(pkganim != null)
            if (pkganim!!.isNotEmpty()) {
                animizeDB.animeDAO().newAnime(Anime(
                        packageID = pkganim!!,
                        packageName = null,
                        episodeTotal = 0,
                        updatedOn = System.currentTimeMillis()
                ))
                getInfo()
            }
            readStarStatus()
            branch = Branch.getInstance()
        } else {
            val intent = Intent(this@PackageView, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        RequestQueueVolley.getInstance(this)!!.cancelRequestByTAG("PLAYLIST")
        RequestQueueVolley.getInstance(this)!!.cancelRequestByTAG("INFO_PACKAGE")
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("RestrictedApi")
    private fun initialize() {
        setSupportActionBar(binding.packageViewToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.packageViewToolbar.title = ""
        modelinfo = ArrayList()
        binding.packageviewContainer.visibility = View.GONE
        binding.packageViewShare.setOnClickListener {
            if (modelinfo != null) {
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                var urlShare: String? = null
                try {
                    urlShare = "https://animize.app.link/share/package?pack=" + pkganim + "&titname=" + URLEncoder.encode(modelinfo!![0].getName(), "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, urlShare)
                //Here we're setting the title of the content
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Share " + modelinfo!![0].getName() + " to your friend!")
                sendIntent.type = "text/plain"
                // Show the ShareSheet
                startActivity(Intent.createChooser(sendIntent, null))
            }
        }

        binding.packageViewStar.setOnClickListener {
            lifecycleScope.launch {
                if (animizeDB.animeDAO().isAnimeStarred(pkganim!!)) {
                    changeStar("UNSTAR")
                } else {
                    changeStar("STAR")
                }
            }
        }
    }

    /*Function for refreshing activity data due to usage of multiview fragment*/
    fun refreshActivity(pkgid: String?) {
        pkganim = pkgid
        if (pkganim!!.isNotEmpty()) {
            if (modelinfo!!.size > 0) {
                modelinfo!!.clear()
            }
            binding.packageviewLoadingContainer.visibility = View.VISIBLE
            binding.packageviewContainer.visibility = View.GONE
            binding.packageviewBarlayout.visibility = View.GONE
            getInfo()
        }
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            finish()
            val intent = Intent(this@PackageView, AnimizeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            if (animizeDB.userDAO().countUser() != 0) {
                // Branch init
                if (branch != null) {
                    branch?.initSession({ referringParams, error ->
                        if (error == null) {
                            // params are the deep linked params associated with the link      that the user clicked -> was re-directed to this app
                            // params will be empty if no data found
                            try {
                                if (referringParams != null) {
                                    if (referringParams.getBoolean("+clicked_branch_link")) {
                                        pkganim = referringParams.getString("pack")
                                        getInfo()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Log.e("BRANCH SDKE", error.message)
                        }
                    }, this@PackageView.intent.data, this@PackageView)
                }
            } else {
                val intent = Intent(this@PackageView, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getPlayList() {
        val url = Api.url_playlist_play + pkganim
        APINetworkRequest(this, getplaylist, url, CODE_GET_REQUEST, null, "PLAYLIST")
    }

    private fun getInfo() {
        val url = Api.url_packageinfo + pkganim
        APINetworkRequest(this, getInfo, url, CODE_GET_REQUEST, null, "INFO_PACKAGE")
    }

    private var getInfo: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    parserInfo(`object`.getJSONArray("anim"))
                } else {
                    window.statusBarColor = Color.RED
                    binding.packageviewBarlayout.visibility = View.GONE
                    binding.packageviewContainer.visibility = View.GONE
                    binding.packageviewLoadingContainer.visibility = View.GONE
                    binding.packageviewLoading.visibility = View.GONE
                    binding.packageviewNotfoundcont.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {
            binding.packageviewLoading.visibility = View.VISIBLE
            binding.packageviewContainer.visibility = View.GONE
            binding.packageviewBarlayout.visibility = View.GONE
            binding.packageviewNotfoundcont.visibility = View.GONE
        }
    }
    private var getplaylist: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    binding.packageviewLoadingContainer.visibility = View.GONE
                    binding.packageviewLoading.visibility = View.GONE
                    binding.packageviewNotfoundcont.visibility = View.GONE
                    binding.packageviewBarlayout.visibility = View.VISIBLE
                    binding.packageviewContainer.visibility = View.VISIBLE
                    parsePlayList(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {}
    }

    private fun parserInfo(info: JSONArray) {
        try {
            modelinfo!!.clear()
            for (i in 0 until info.length()) {
                val `object` = info.getJSONObject(i)
                val packages = `object`.getString("package_anim")
                val nameAnim = `object`.getString("name_catalogue")
                val synopsis = `object`.getString("synopsis")
                val totalEP = `object`.getString("total_ep_anim")
                val rate = `object`.getString("rating")
                val mal = `object`.getString("mal_id")
                val cover = `object`.getString("cover")
                val genreJson = `object`.getJSONArray("genre")
                val genres: MutableList<String> = ArrayList()
                for (j in 0 until genreJson.length()) {
                    genres.add(genreJson.getString(j))
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                modelinfo!!.add(PackageInfo(packages, nameAnim, synopsis, totalEP, rate, mal, genres, cover))
            }
            binding.packageViewToolbar.title = modelinfo!![0].getName()
            binding.packageViewTitle.text = modelinfo!![0].getName()
            binding.packageViewSynopsis.text = modelinfo!![0].synopsis
            binding.packageViewMal.text = "MAL ID: ${modelinfo!![0].mal}"
            val adapterGenre = PackageMetaGenreAdapter(modelinfo!![0].genre, this, R.layout.rv_staggered)
            val spanStaggered = if (adapterGenre.itemCount < 7) 1 else 2
            val layoutManager = StaggeredGridLayoutManager(spanStaggered, StaggeredGridLayoutManager.HORIZONTAL)
            binding.packageViewGenreList.layoutManager = layoutManager
            binding.packageViewGenreList.adapter = adapterGenre
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover).transform(RoundedCorners(15))
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(binding.packageViewCover)
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover)
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600))
                    .into(binding.packageViewBackDrop)
            binding.packageViewCover.setOnClickListener {
                val fm = supportFragmentManager
                val alertDialog = CoverView()
                alertDialog.setUrl(modelinfo!![0].cover)
                alertDialog.show(fm, "coverview")
            }
            binding.packageViewRating.text = "Rating ${modelinfo!![0].rate}"
            val animeID = "Package ID: ${modelinfo!![0].pack}"
            binding.packageViewAnmID.text = animeID
            showRecent()
            invalidateOptionsMenu()
            getPlayList()
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    private fun showRecent() {
        lifecycleScope.launch {
            if (animizeDB.recentPlayedDAO().getRecentByPackageID(pkganim!!) != null) {
                readRecent()
            } else {
                binding.packageViewResume.visibility = View.GONE
            }
        }
    }

    private fun parsePlayList(playlist: JSONArray) {
        try {
            playlistModels = ArrayList()
            val layoutManager = AutoGridLayoutManager(this, 500)
            binding.packageviewList.layoutManager = layoutManager
            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val urlImg = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val idAnim = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                playlistModels!!.add(PlaylistModel(urlImg, title, episode, idAnim, pkg))
            }
            adapter = PackageListAdapter(playlistModels, this, R.layout.rv_episode_item)
            binding.packageviewList.adapter = adapter
            lifecycleScope.launch {
                if (animizeDB.animeDAO().getAnimeByPackageID(modelinfo!![0].pack) != null) {
                    animizeDB.animeDAO().updateAnime(
                            packageID = modelinfo!![0].pack,
                            episodeTotal = modelinfo!![0].tot.toInt(),
                            updatedOn = System.currentTimeMillis(),
                            currentEpisode = playlistModels!!.size,
                            packageName = modelinfo!![0].getName()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun readStarStatus() {
        withContext(Dispatchers.IO) {
            val isStarred = animizeDB.animeDAO().isAnimeStarred(pkganim!!)
            withContext(Dispatchers.Main) {
                if (isStarred) {
                    binding.packageViewStar.chipIcon = this@PackageView.getDrawable(R.drawable.ic_star)
                    binding.packageViewStar.text = "Starred"
                } else {
                    binding.packageViewStar.chipIcon = this@PackageView.getDrawable(R.drawable.ic_star_nofill)
                    binding.packageViewStar.text = "Not Starred"
                }
            }
        }
    }

    private suspend fun changeStar(change: String) {
        withContext(Dispatchers.IO) {
            if (change == "UNSTAR") {
                animizeDB.animeDAO().changeStarred(pkganim!!, false)
            } else if (change == "STAR") {
                animizeDB.animeDAO().changeStarred(pkganim!!, true)
            }
            val isStarred = animizeDB.animeDAO().isAnimeStarred(pkganim!!)
            withContext(Dispatchers.Main) {
                val status: String = if (isStarred) {
                    binding.packageViewStar.chipIcon = this@PackageView.getDrawable(R.drawable.ic_star)
                    binding.packageViewStar.text = "Starred"
                    "Add to Star Success"
                } else {
                    binding.packageViewStar.chipIcon = this@PackageView.getDrawable(R.drawable.ic_star_nofill)
                    binding.packageViewStar.text = "Not Starred"
                    "Remove Star Success"
                }
                Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun readRecent() {
        withContext(Dispatchers.IO) {
            val recentLand = animizeDB.recentPlayedDAO().getRecentByPackageID(pkganim!!)
            withContext(Dispatchers.Main) {
                binding.packageViewResume.visibility = View.VISIBLE
                val ep = "Episode: " + (recentLand?.episode ?: 1)
                binding.packageViewResume.text = ep
                binding.packageViewResume.setOnClickListener {
                    val intent = Intent(applicationContext, StreamActivity::class.java)
                    if (recentLand != null) {
                        intent.putExtra("id_anim", recentLand.animeID)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            }
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
    }
}