package ml.dvnlabs.animize.model;

public class bannerlist_model {
    private String banner_image,banner_title,banner_url;

    public bannerlist_model(String banner_image,String banner_title,String banner_url){
        this.banner_image = banner_image;
        this.banner_title = banner_title;
        this.banner_url = banner_url;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public String getBanner_title() {
        return banner_title;
    }

    public String getBanner_url() {
        return banner_url;
    }
}
