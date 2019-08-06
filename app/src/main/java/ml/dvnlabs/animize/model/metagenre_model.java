package ml.dvnlabs.animize.model;

public class metagenre_model {
    private String title,count;
    public metagenre_model(String title,String count){
        this.title = title;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public String getTitle() {
        return title;
    }
}
