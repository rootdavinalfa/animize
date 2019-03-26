package ml.dvnlabs.animize.model;

public class search_list_model {
    private String url_imagetitle;
    private String title_nm;
    private String ep_num;
    private String idn;

    public search_list_model(String ur,String id,String tn,String en){
        this.ep_num = en;
        this.title_nm = tn;
        this.url_imagetitle = ur;
        this.idn = id;

    }

    public String getEp_num() {
        return ep_num;
    }

    public String getTitle_nm() {
        return title_nm;
    }

    public String getUrl_imagetitle() {
        return url_imagetitle;
    }

    public String getIdn() {
        return idn;
    }
}
