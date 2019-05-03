package ml.dvnlabs.animize.recyclerview;

import android.content.Context;
import android.view.View;

import com.wang.avi.AVLoadingIndicatorView;

import androidx.recyclerview.widget.RecyclerView;
import ml.dvnlabs.animize.R;

public class progress_holder  extends RecyclerView.ViewHolder {
    public AVLoadingIndicatorView loading;

    public progress_holder(Context context,View view) {
        super(view);
        loading = (AVLoadingIndicatorView) view.findViewById(R.id.load_moreprogress);
    }
}
