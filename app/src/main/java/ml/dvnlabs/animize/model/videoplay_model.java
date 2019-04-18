package ml.dvnlabs.animize.model;

import android.os.Parcel;
import android.os.Parcelable;

public class videoplay_model implements Parcelable {
    private String name_anim;
    private String episode;
    private String  total_ep_anim;
    private String rating;
    private String sysnop;
    private String pack;
    private String source_url;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public videoplay_model createFromParcel(Parcel in) {
            return new videoplay_model(in);
        }

        public videoplay_model[] newArray(int size) {
            return new videoplay_model[size];
        }
    };

    public videoplay_model(String name, String ep, String total, String rate, String synop, String pkg, String url){
        this.episode = ep;
        this.name_anim = name;
        this.rating = rate;
        this.source_url = url;
        this.sysnop = synop;
        this.total_ep_anim = total;
        this.pack = pkg;
    }
    public videoplay_model(Parcel in){
        this.episode = in.readString();
        this.name_anim = in.readString();
        this.rating = in.readString();
        this.source_url = in.readString();
        this.sysnop = in.readString();
        this.total_ep_anim = in.readString();
        this.pack = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(episode);
        dest.writeString(name_anim);
        dest.writeString(rating);
        dest.writeString(source_url);
        dest.writeString(sysnop);
        dest.writeString(total_ep_anim);
        dest.writeString(pack);
    }


    @Override
    public String toString() {
        return null;
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
}
