package ml.dvnlabs.animize.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ExpandedBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {
    public ExpandedBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(final CoordinatorLayout parent, final V child, final int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
        /*
            Unfortunately its not good enough to just call setState(STATE_EXPANDED); after super.onLayoutChild
            The reason is that an animateView plays after calling setState. This can cause some graphical issues with other layouts
            Instead we need to use setInternalState, however this is a private method.
            The trick is to utilise onRestoreInstance to call setInternalState immediately and indirectly
         */
    }
    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(parent, child, event);
        } catch (NullPointerException ignored) {
            return false;
        }
    }
}
