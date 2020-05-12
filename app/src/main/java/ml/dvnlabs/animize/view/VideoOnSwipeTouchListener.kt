/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.view

import android.content.Context
import android.os.Handler
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import ml.dvnlabs.animize.ui.activity.StreamActivity
import kotlin.math.abs

open class VideoOnSwipeTouchListener(ctx: Context, playerView: PlayerView, player: SimpleExoPlayer) : OnTouchListener {
    private val gestureDetector: GestureDetector
    private val playerView: PlayerView
    private val player: SimpleExoPlayer
    var counter_seek = 0
        private set
    private val mContext: Context
    private val handler: Handler
    private val runnable: Runnable
    var isCounting = false
        private set
    private var status = ""
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if ((player.playbackState == Player.STATE_READY || player.playbackState == Player.STATE_BUFFERING) && !(mContext as StreamActivity).isLocked) {
                val nowTime = player.currentPosition
                isCounting = true
                if (distanceX < 0) {
                    if (status == "REWIND") {
                        counter_seek = 0
                    }
                    counter_seek += 1
                    player.seekTo(counter_seek * 1000 + nowTime)
                    status = "FORWARD"
                } else if (distanceX > 0) {
                    if (status == "FORWARD") {
                        counter_seek = 0
                    }
                    counter_seek += 1
                    player.seekTo(nowTime - counter_seek * 1000)
                    status = "REWIND"
                }
            }
            handler.postDelayed(runnable, 2000)
            onCounting(counter_seek)
            return false
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (playerView.isControllerVisible) {
                playerView.hideController()
            } else {
                playerView.showController()
            }
            return super.onSingleTapUp(e)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    fun onSwipeTop() {}
    fun onSwipeBottom() {}

    open fun onCounting(count: Int) {
        //return isCounting;
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
        this.playerView = playerView
        this.player = player
        mContext = ctx
        handler = Handler()
        runnable = Runnable {
            isCounting = false
            counter_seek = 0
        }
    }
}