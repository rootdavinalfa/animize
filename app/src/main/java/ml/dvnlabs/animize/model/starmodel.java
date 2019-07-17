package ml.dvnlabs.animize.model;

public class starmodel {
    private String packageid,name,total_ep,rating,mal,cover;

    public starmodel(String packageid,String name,String total_ep,String rating,String mal,String cover){
        this.packageid = packageid;
        this.name = name;
        this.total_ep = total_ep;
        this.rating = rating;
        this.mal = mal;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getPackageid() {
        return packageid;
    }

    public String getMal() {
        return mal;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getTotal_ep() {
        return total_ep;
    }
}
