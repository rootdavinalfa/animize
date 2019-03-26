package ml.dvnlabs.animize.model;

public class videoplay_model {
    private String name_anim;
    private String episode;
    private String  total_ep_anim;
    private String rating;
    private String sysnop;
    private String pack;
    private String source_url;

    public videoplay_model(String name,String ep,String total,String rate,String synop,String pkg,String url){
        this.episode = ep;
        this.name_anim = name;
        this.rating = rate;
        this.source_url = url;
        this.sysnop = synop;
        this.total_ep_anim = total;
        this.pack = pkg;
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
