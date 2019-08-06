package ml.dvnlabs.animize.model;

public class recentlist_model {
    private String package_id,package_name,anmid,url_cover;
    private String timestamp,modified;
    private String episode;
    public recentlist_model(String package_id,String package_name,String anmid,String url_cover,String episode,String timestamp,String modified){
        this.anmid = anmid;
        this.url_cover=url_cover;
        this.package_id = package_id;
        this.package_name = package_name;
        this.timestamp = timestamp;
        this.modified = modified;
        this.episode = episode;
    }

    public String getUrl_cover() {
        return url_cover;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getPackage_id() {
        return package_id;
    }

    public String getAnmid() {
        return anmid;
    }

    public String getEpisode() {
        return episode;
    }

    public String getModified() {
        return modified;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
