/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.card.MaterialCardView
import com.wang.avi.AVLoadingIndicatorView
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.InitInternalDBHelper
import ml.dvnlabs.animize.database.PackageStarDBHelper
import ml.dvnlabs.animize.database.RecentPlayDBHelper
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.network.APINetworkRequest
import ml.dvnlabs.animize.driver.util.network.RequestQueueVolley
import ml.dvnlabs.animize.driver.util.network.listener.FetchDataListener
import ml.dvnlabs.animize.event.PlayerBusError
import ml.dvnlabs.animize.event.PlayerBusStatus
import ml.dvnlabs.animize.model.CommentMainModel
import ml.dvnlabs.animize.model.VideoPlayModel
import ml.dvnlabs.animize.player.PlaybackStatus
import ml.dvnlabs.animize.player.PlayerManager
import ml.dvnlabs.animize.ui.fragment.comment.MainComment
import ml.dvnlabs.animize.ui.fragment.comment.ThreadComment
import ml.dvnlabs.animize.ui.fragment.tabs.PlaylistFragment
import ml.dvnlabs.animize.view.VideoOnSwipeTouchListener
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class StreamActivity : AppCompatActivity() {
    private val CODE_GET_REQUEST = 1024

    private var packageStarDBHelper: PackageStarDBHelper? = null
    private var initInternalDBHelper: InitInternalDBHelper? = null
    private var recentPlayDBHelper: RecentPlayDBHelper? = null

    private var updateRecent: Runnable? = null
    private var handlerPlayerRecent: Handler? = null

    private var playerView: PlayerView? = null
    private var isFullscreen = false

    @kotlin.jvm.JvmField
    var isLocked = false

    private val isInit = true
    var modeldata: ArrayList<VideoPlayModel>? = null

    private var playerManager: PlayerManager? = null

    var media_height = 0
    var media_width = 0
    private var idanim: String? = null
    private var id_source: String? = null
    private var url: String? = null

    //Layout
    private var mainView: LinearLayout? = null
    private var mainViewDetails: MaterialCardView? = null
    private var mainViewMore: MaterialCardView? = null

    //Information Layout
    private var infoContainer: RelativeLayout? = null
    private var infoImg: ImageView? = null
    private var infoLoading: AVLoadingIndicatorView? = null
    private var infoText: TextView? = null

    //Playerview
    private var fs_btn: ImageView? = null
    private var video_artwork: ImageView? = null
    private var locker: ImageView? = null
    private var ply_name: TextView? = null
    private var ply_episod: TextView? = null
    private var video_seektime: TextView? = null
    private var video_buffer: AVLoadingIndicatorView? = null

    //Details
    var detailsAdd: ImageView? = null
    var detailsCover: ImageView? = null
    var detailsTitle: TextView? = null
    var detailsEpisode: TextView? = null
    var detailsIDAnim: TextView? = null
    var detailsSynopsis: TextView? = null
    var detailsGenre: TextView? = null
    var detailsHead: LinearLayout? = null
    var detailsScroll: LinearLayout? = null
    var detailsMore: ExpandableLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        modeldata = ArrayList()
        initialSetup()

        val intent = intent
        if (getIntent().getStringExtra("id_anim") != null) {
            setIdAnim(intent.getStringExtra("id_anim"))
        }

        playerManager = PlayerManager(this)
        if (PlayerManager.service == null) {
            playerManager!!.bind()
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        packageStarDBHelper = PackageStarDBHelper(this)
        initInternalDBHelper = InitInternalDBHelper(this)
        recentPlayDBHelper = RecentPlayDBHelper(this)
    }

    override fun onStop() {
        //playerManager.unbind();
        //playerView.setPlayer(null);
        EventBus.getDefault().unregister(this)
        handlerPlayerRecent!!.removeCallbacks(updateRecent!!)
        super.onStop()
    }

    override fun onDestroy() {
        val pref = applicationContext.getSharedPreferences("aPlay", 0)
        super.onDestroy()
        if (pref != null) {
            Log.e("CLEARING:", "CLEAR SharedPreference")
            val editor = pref.edit()
            editor.clear()
            editor.apply()
        }
        handlerPlayerRecent!!.removeCallbacks(updateRecent!!)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        val buttonFullscreen: Drawable?
        val imageView = findViewById<ImageView>(R.id.exo_fullscreen_icon)
        val threadComment = supportFragmentManager.findFragmentByTag("COMMENT_THREAD") as ThreadComment?
        if (threadComment != null && threadComment.isVisible) {
            println("THREAD COMMENT UNVISIBLING")
            closeReplyFragment()
        } else {
            if (isFullscreen) {
                buttonFullscreen = ResourcesCompat.getDrawable(resources, R.drawable.ic_fullscreen_expand, null)
                imageView.setImageDrawable(buttonFullscreen)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                playerView!!.useController = true
                isLocked = false
            }
            if (!isFullscreen) {
                if (PlayerManager.service != null && playerManager!!.isServiceBound) {
                    playerManager!!.unbind()
                    if (!modeldata!!.isEmpty()) {
                        modeldata!!.clear()
                    }
                    //idanim = null;
                }
                val pref = applicationContext.getSharedPreferences("aPlay", 0)
                if (pref != null) {
                    Log.e("CLEARING:", "CLEAR SharedPreference")
                    val editor = pref.edit()
                    editor.clear()
                    editor.apply()
                }

                if (handlerPlayerRecent != null) {
                    handlerPlayerRecent!!.removeCallbacks(updateRecent!!)
                }

                val queue = RequestQueueVolley(this)
                queue.cancelRequest()
                super.onBackPressed()
                //finish();
                //unbindService(serviceConnection);
                //Intent intent = new Intent(this, dashboard_activity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentOrient = resources.configuration.orientation
        if (currentOrient == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUIFullscreen()
        } else {
            showSystemUIFullscreen()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUIFullscreen() {
        val media = playerView!!.layoutParams as FrameLayout.LayoutParams
        media_height = media.height
        media_width = media.width
        media.height = FrameLayout.LayoutParams.MATCH_PARENT
        media.width = FrameLayout.LayoutParams.MATCH_PARENT
        playerView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        showLocker()
        isFullscreen = true
    }

    @SuppressLint("InlinedApi")
    private fun showSystemUIFullscreen() {
        val media = playerView!!.layoutParams as FrameLayout.LayoutParams
        media.height = media_height
        media.width = FrameLayout.LayoutParams.MATCH_PARENT
        playerView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        hideLocker()
        isFullscreen = false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun btnFullscreen(v: View?) {
        var buttonFullscreen: Drawable?
        if (isFullscreen) {
            buttonFullscreen = ResourcesCompat.getDrawable(resources, R.drawable.ic_fullscreen_expand, null)
            fs_btn!!.setImageDrawable(buttonFullscreen)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (!isFullscreen) {
            buttonFullscreen = ResourcesCompat.getDrawable(resources, R.drawable.ic_fullscreen_skrink, null)
            fs_btn!!.setImageDrawable(buttonFullscreen)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }


    private fun initialSetup() {
        mainView = findViewById(R.id.stream_main)
        mainViewDetails = findViewById(R.id.stream_view_details)
        mainViewMore = findViewById(R.id.stream_view_more)

        infoContainer = findViewById(R.id.stream_load_container)
        infoImg = findViewById(R.id.stream_error_img)
        infoLoading = findViewById(R.id.stream_loading)
        infoText = findViewById(R.id.stream_error_txt)

        playerView = findViewById(R.id.animplay_views)
        fs_btn = findViewById(R.id.exo_fullscreen_icon)
        video_buffer = findViewById(R.id.exo_buffering)
        video_artwork = findViewById(R.id.exo_artwork)
        video_seektime = findViewById(R.id.exo_seektime)
        locker = findViewById(R.id.exo_controller_lock)
        ply_name = findViewById(R.id.player_name)
        ply_episod = findViewById(R.id.player_episode)
        ply_name!!.isSelected = true

        detailsAdd = findViewById(R.id.aplay_add_star)
        detailsEpisode = findViewById(R.id.details_episode)
        detailsGenre = findViewById(R.id.details_genres)
        detailsIDAnim = findViewById(R.id.details_idanime)
        detailsSynopsis = findViewById(R.id.details_synopsis)
        detailsTitle = findViewById(R.id.details_title)
        detailsHead = findViewById(R.id.details_head)
        detailsMore = findViewById(R.id.details_more)
        detailsCover = findViewById(R.id.details_cover)
        detailsScroll = findViewById(R.id.details_scroll)
        detailsHead!!.setOnClickListener {
            detailsMore!!.toggle()
        }
        detailsScroll!!.setOnClickListener {
            detailsMore!!.toggle()
        }
        detailsAdd!!.setOnClickListener {
            changeStar()
        }

        mainView!!.visibility = View.GONE
        infoContainer!!.visibility = View.VISIBLE
        infoImg!!.visibility = View.GONE
        infoText!!.visibility = View.GONE
        infoLoading!!.visibility = View.VISIBLE


        val fm = supportFragmentManager.beginTransaction().replace(R.id.playlist_fragment, PlaylistFragment())
        fm.commit()
        val fm1 = supportFragmentManager.beginTransaction().replace(R.id.comment_fragment, MainComment())
        fm1.commit()
    }

    private fun buffering() {
        video_buffer!!.show()
    }

    private fun notBuffering() {
        video_buffer!!.hide()
    }

    fun setIdAnim(idanim: String?) {
        this.idanim = idanim
    }

    @Subscribe
    fun onEvent(status: PlayerBusStatus) {
        if (status.status == PlaybackStatus.LOADING) {
            buffering()
        } else {
            notBuffering()
        }
    }

    @Subscribe
    fun onEvent(error: PlayerBusError) {
        val textError = findViewById<TextView>(R.id.exo_error_message)
        textError.text = error.error
        println("ERROR:" + error.error)
    }

    fun getVideo() {
        val urlNEW = Api.api_animplay + idanim
        APINetworkRequest(this, getVideo, urlNEW, CODE_GET_REQUEST, null)
    }

    private var getVideo: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            mainView!!.visibility = View.VISIBLE
            infoContainer!!.visibility = View.GONE
            infoImg!!.visibility = View.VISIBLE
            infoText!!.visibility = View.VISIBLE
            infoLoading!!.visibility = View.GONE
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    showVideo(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            mainView!!.visibility = View.GONE
            infoContainer!!.visibility = View.VISIBLE
            infoImg!!.visibility = View.VISIBLE
            infoText!!.visibility = View.VISIBLE
            infoLoading!!.visibility = View.GONE
            infoText!!.text = msg!!
        }

        override fun onFetchStart() {
            mainView!!.visibility = View.GONE
            infoContainer!!.visibility = View.VISIBLE
            infoImg!!.visibility = View.GONE
            infoText!!.visibility = View.GONE
            infoLoading!!.visibility = View.VISIBLE
        }
    }

    private fun showVideo(video: JSONArray) {
        if (modeldata!!.isNotEmpty()) {
            modeldata!!.clear()
        }
        try {
            for (i in 0 until video.length()) {
                val jsonObject = video.getJSONObject(i)
                val ur = jsonObject.getString(Api.JSON_source)
                val nm = jsonObject.getString("name_catalogue")
                val epi = jsonObject.getString(Api.JSON_episode_anim)
                val tot = jsonObject.getString("total_ep_anim")
                val rat = jsonObject.getString("rating")
                val pack = jsonObject.getString("package_anim")
                val syi = jsonObject.getString("synopsis")
                val cover = jsonObject.getString("cover")
                val genreJson = jsonObject.getJSONArray("genres")
                val genres: MutableList<String> = ArrayList()
                for (j in 0 until genreJson.length()) {
                    genres.add(genreJson.getString(j))
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                val thmb = jsonObject.getString("thumbnail")
                //Log.e("DATA: ",nm+tot);
                modeldata!!.add(VideoPlayModel(nm, epi, tot, rat, syi, pack, ur, genres, thmb, cover))
                val sb = StringBuilder()
                for (gnr in 0 until genres.size) {
                    sb.append(genres[gnr])
                    if (genres.size - 1 > gnr) {
                        sb.append(",")
                    }
                }
                detailsTitle!!.text = nm
                detailsGenre!!.text = sb.toString()
                detailsEpisode!!.text = "${getString(R.string.episode_text)} $epi"
                detailsIDAnim!!.text = idanim
                detailsSynopsis!!.text = syi
                Glide.with(this)
                        .applyDefaultRequestOptions(RequestOptions()
                                .placeholder(R.drawable.ic_picture_light)
                                .error(R.drawable.ic_picture_light))
                        .load(cover)
                        .transition(DrawableTransitionOptions()
                                .crossFade())
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(10, 0)))
                        .into(detailsCover!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        url = modeldata!![0].source_url

        ply_name!!.text = modeldata!![0].name_anim
        val epe = "${getString(R.string.episode_text)} : ${modeldata!![0].episode} ${getString(R.string.string_of)} ${modeldata!![0].total_ep_anim}"
        ply_episod!!.text = epe

        if (modeldata != null) {
            readUser()
            readStar()
            sendPKG(modeldata!![0].pack, idanim!!)
            commentFragment(idanim!!)
            playerConstant()
        }
    }

    private fun commentFragment(anim: String) {
        val f = supportFragmentManager.findFragmentById(R.id.comment_fragment) as MainComment?
        f!!.receiveData(anim)
    }

    fun showReplyFragment(model: ArrayList<CommentMainModel>?) {
        mainViewDetails!!.visibility = View.GONE
        mainViewMore!!.visibility = View.GONE
        val se = ThreadComment.newInstance(model!!, idanim!!)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(R.id.comment_fragment, se, "COMMENT_THREAD")
                .addToBackStack("COMMENT_THREAD").commit()
        //se!!.rece(model);
    }

    fun closeReplyFragment() {
        mainViewDetails!!.visibility = View.VISIBLE
        mainViewMore!!.visibility = View.VISIBLE
        val fragmentManager = supportFragmentManager
        // Check to see if the fragment is already showing.
        val simpleFragment = fragmentManager
                .findFragmentById(R.id.comment_fragment) as ThreadComment?
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(simpleFragment).commit()
        }
    }

    private fun sendPKG(pkg: String, anim: String) {
        val f = supportFragmentManager.findFragmentById(R.id.playlist_fragment) as PlaylistFragment?
        f!!.receiveData(pkg, anim)
    }

    private fun playerConstant() {
        PlayerManager.service!!.playOrPause(url)
        playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        playerView!!.useArtwork = true
        playerView!!.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        playerView!!.useController = true
        locker!!.setOnClickListener {
            if (isLocked && isFullscreen) {
                locker!!.setImageResource(R.drawable.ic_locked)
                playerView!!.useController = true
                //Toast.makeText(animplay_activity.this,"Controller unLocked!",Toast.LENGTH_SHORT).show();
                isLocked = false
            } else if (!isLocked && isFullscreen) {
                locker!!.setImageResource(R.drawable.ic_unlocked)
                playerView!!.useController = false
                //Toast.makeText(animplay_activity.this,"Controller Locked!",Toast.LENGTH_SHORT).show();
                isLocked = true
            }
        }
        playerView!!.setControllerVisibilityListener { visibility ->
            if (visibility == 0 && isFullscreen) {
                hideLocker()
                playerView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else if (visibility > 0 && isFullscreen) {
                showLocker()
                playerView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }
        playerView!!.setOnTouchListener(object : VideoOnSwipeTouchListener(this, playerView!!, PlayerManager.service!!.exoPlayer!!) {
            private var counting = 0
            private val handler = Handler()
            override fun onSwipeRight() {
                if (!isLocked) {
                    val seekTimes = "Forward: $counting Seconds"
                    video_seektime!!.text = seekTimes
                    showSeekTime()
                    unVisible()
                }
                //Toast.makeText(animplay_activity.this, "right", Toast.LENGTH_SHORT).show();
            }

            override fun onSwipeLeft() {
                if (!isLocked) {
                    val seektimes = "Rewind: $counting Seconds"
                    video_seektime!!.text = seektimes
                    showSeekTime()
                    unVisible()
                }
                //Toast.makeText(animplay_activity.this, "left", Toast.LENGTH_SHORT).show();
            }

            private fun showSeekTime() {
                video_seektime!!.animate()
                        .translationY(video_seektime!!.height.toFloat())
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                video_seektime!!.visibility = View.VISIBLE
                            }
                        })
                video_seektime!!.visibility = View.VISIBLE
            }

            private fun unVisible() {
                handler.postDelayed({
                    video_seektime!!.animate()
                            .translationY(video_seektime!!.height.toFloat())
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    video_seektime!!.visibility = View.GONE
                                }
                            })
                    //video_seektime.setVisibility(View.GONE);
                }, 3000)
            }

            override fun onCounting(count: Int) {
                counting = count
                //super.onCounting(count);
            }
        })
        playerView!!.defaultArtwork = resources.getDrawable(R.drawable.ic_astronaut, null)
        playerView!!.player = PlayerManager.service!!.exoPlayer
        Glide.with(this).applyDefaultRequestOptions(RequestOptions()
                .placeholder(R.drawable.ic_picture_light).error(R.drawable.ic_picture_light))
                .load(modeldata!![0].url_thmb)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()).into(video_artwork!!)
        if (handlerPlayerRecent != null) {
            handlerPlayerRecent!!.removeCallbacks(updateRecent!!)
        }
        readRecent()
    }

    private fun showLocker() {
        locker!!.animate()
                .translationY(locker!!.height.toFloat())
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        locker!!.visibility = View.VISIBLE
                    }
                })
    }

    private fun hideLocker() {
        locker!!.animate()
                .translationY(locker!!.height.toFloat())
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        locker!!.visibility = View.GONE
                    }
                })
    }

    @UiThread
    private fun readStar() {
        GlobalScope.launch {
            val starred = packageStarDBHelper!!.isStarred(modeldata!![0].pack)
            if (starred) {
                detailsAdd!!.setImageResource(R.drawable.ic_star)
            } else {
                detailsAdd!!.setImageResource(R.drawable.ic_add)
            }
        }
    }

    @UiThread
    private fun changeStar() {
        var status: String
        GlobalScope.run {
            val packageAnim = modeldata!![0].pack
            val starred = packageStarDBHelper!!.isStarred(packageAnim)
            status = if (starred) {
                packageStarDBHelper!!.unStar(packageAnim)
                detailsAdd!!.setImageResource(R.drawable.ic_add)
                "Remove Star Success"
            } else {
                packageStarDBHelper!!.addStar(packageAnim)
                detailsAdd!!.setImageResource(R.drawable.ic_star)
                "Add to Star Success"
            }
            Toast.makeText(this@StreamActivity, status, Toast.LENGTH_SHORT).show()
        }
    }

    @UiThread
    private fun readUser() {
        GlobalScope.run {
            val user = initInternalDBHelper!!.user
            if (user != null) {
                val preferences = applicationContext.getSharedPreferences("aPlay", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("idUser", user.idUser)
                editor.putString("token", user.token)
                editor.apply()
            }
        }
    }


    //Recent Method

    private fun updateRecent() {
        if (PlayerManager.service != null) {
            val playbackState = PlayerManager.service!!.exoPlayer!!.playbackState
            if (playbackState == Player.STATE_READY || PlayerManager.service!!.exoPlayer!!.playbackState == Player.STATE_BUFFERING) {
                updateRecent = Runnable {
                    updatingRecent()
                }
                handlerPlayerRecent = Handler()
                handlerPlayerRecent!!.postDelayed(updateRecent!!, 2000)
            }
        }
    }

    private fun getCurrentPlayTime(): Long {
        return PlayerManager.service!!.exoPlayer!!.currentPosition
    }

    private fun seekPlayer(position: Long) {
        PlayerManager.service!!.exoPlayer!!.seekTo(position)
    }

    @UiThread
    private fun readRecent() {
        GlobalScope.run {
            val recent = recentPlayDBHelper!!.readRecent(idanim!!)
            var seeker: Long = 0
            if (recent != null) {
                if (getCurrentPlayTime() < recent.timestamp) {
                    seeker = recent.timestamp
                }
            }
            seekPlayer(seeker)
            updateRecent()
        }
    }

    private fun updatingRecent() {
        GlobalScope.run {
            if (recentPlayDBHelper!!.isRecentAvail(idanim!!)) {
                val packageId = modeldata!![0].pack
                val packageName = modeldata!![0].name_anim
                val episode = Integer.valueOf(modeldata!![0].episode)
                val urlCover = modeldata!![0].cover
                val timestamp = getCurrentPlayTime()
                recentPlayDBHelper!!.updateRecent(packageId, packageName, idanim!!, episode, urlCover, timestamp, PlayerManager.service!!.exoPlayer!!.duration)
            } else {
                val packageId = modeldata!![0].pack
                val packageName = modeldata!![0].name_anim
                val episode = Integer.valueOf(modeldata!![0].episode)
                val urlCover = modeldata!![0].cover
                val timestamp = getCurrentPlayTime()
                recentPlayDBHelper!!.addRecent(packageId, packageName, idanim, episode, urlCover, timestamp, PlayerManager.service!!.exoPlayer!!.duration)
            }
            updateRecent()
        }
    }
}