package ml.dvnlabs.animize.model;

public class playlist_model {
    private String url_image;
    private String title;
    private String episode;
    private String id_anim;
    private String package_an;

    public playlist_model(String ur, String tit, String ep, String id, String pack){
        this.episode = ep;
        this.url_image = ur;
        this.id_anim = id;
        this.package_an = pack;
        this.title = tit;
    }

    public String getEpisode() {
        return episode;
    }

    public String getId_anim() {
        return id_anim;
    }

    public String getPackage_an() {
        return package_an;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl_image() {
        return url_image;
    }
}
