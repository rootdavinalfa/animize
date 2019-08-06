package ml.dvnlabs.animize.animator;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public class animateView {
    public static void slideDown(final View view) {
        view.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 1;
        view.setLayoutParams(layoutParams);

        view.measure(View.MeasureSpec.makeMeasureSpec(Resources.getSystem().getDisplayMetrics().widthPixels,
                View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED));

        final int height = view.getMeasuredHeight();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(1, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (height > value) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = value;
                    view.setLayoutParams(layoutParams);
                }else{
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        valueAnimator.start();
    }


    public static void slideUp(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                final int height = view.getHeight();
                ValueAnimator valueAnimator = ObjectAnimator.ofInt(height, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        if (value > 0) {
                            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                            layoutParams.height = value;
                            view.setLayoutParams(layoutParams);
                        }else{
                            view.setVisibility(View.GONE);
                        }
                    }
                });
                valueAnimator.start();
            }
        });

    }
}
