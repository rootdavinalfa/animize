package ml.dvnlabs.animize.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoOnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private int counter_seek;
    private Context mContext;
    private android.os.Handler handler;
    private boolean isCounting = false;

    public VideoOnSwipeTouchListener(Context ctx, PlayerView playerView,SimpleExoPlayer player){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.playerView = playerView;
        this.player = player;
        this.mContext = ctx;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //counter_seek = 0;
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceX < 0){
                counter_seek = counter_seek + 1;
                player.seekTo(counter_seek*1000);
                isCounting = true;
                //Log.e("PLAYER:","Forward "+" Second : "+counter_seek);
            }else if (distanceX > 0){
                counter_seek = counter_seek -1;
                if (counter_seek < 0){
                    counter_seek = 0;
                    player.seekTo(counter_seek);
                }else {
                    player.seekTo(counter_seek*1000);
                }
                isCounting = true;
                //Log.e("PLAYER:","Rewind "+" Second : "+counter_seek);
            }
            onCounting(counter_seek);
            //Log.e("SCROLL:",String.valueOf(Math.round(distanceX)));
            return false;
            //return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (playerView.isControllerVisible()){
                playerView.hideController();
            }
            else {
                playerView.showController();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    public int getCounter_seek() {
        return counter_seek;
    }
    public void onCounting(int count){
        //return isCounting;
    }

    public boolean isCounting() {
        return isCounting;
    }
}