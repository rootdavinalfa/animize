package ml.dvnlabs.animize.fragment.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import ml.dvnlabs.animize.R;

public class coverview extends DialogFragment {
    private ImageView img;
    private String url;
    public coverview(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_coverview,container,false);
        img = view.findViewById(R.id.coverview_img);
        return view;
    }
    public static coverview newInstance(){
        return new coverview();
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(url)
                .transition(new DrawableTransitionOptions()
                        .crossFade()).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(600,991)).into(img);
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.bannerdialog);
    }

    @Override
    public void onResume() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.bannerdialog);
        super.onResume();
    }
}
