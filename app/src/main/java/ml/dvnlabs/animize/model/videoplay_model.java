package ml.dvnlabs.animize.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class videoplay_model {
    private String name_anim;
    private String episode;
    private String  total_ep_anim;
    private String rating;
    private String sysnop;
    private String pack;
    private String source_url;
    private String url_thmb;
    private List<String> genres;

    public videoplay_model(String name, String ep, String total, String rate, String synop, String pkg, String url,List<String>genre,String url_thm){
        this.episode = ep;
        this.name_anim = name;
        this.rating = rate;
        this.source_url = url;
        this.sysnop = synop;
        this.total_ep_anim = total;
        this.pack = pkg;
        this.genres = genre;
        this.url_thmb = url_thm;
    }

    public String getUrl_thmb() {
        return url_thmb;
    }

    public String getSource_url() {
        return source_url;
    }

    public String getEpisode() {
        return episode;
    }

    public String getName_anim() {
        return name_anim;
    }

    public String getRating() {
        return rating;
    }

    public String getSysnop() {
        return sysnop;
    }

    public String getTotal_ep_anim() {
        return total_ep_anim;
    }

    public String getPack() {
        return pack;
    }

    public List<String> getGenres() {
        return this.genres;
    }
}
