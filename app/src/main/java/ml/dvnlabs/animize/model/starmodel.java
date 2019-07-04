package ml.dvnlabs.animize.model;

public class starmodel {
    private String packageid,name,total_ep,rating,mal;

    public starmodel(String packageid,String name,String total_ep,String rating,String mal){
        this.packageid = packageid;
        this.name = name;
        this.total_ep = total_ep;
        this.rating = rating;
        this.mal = mal;
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
