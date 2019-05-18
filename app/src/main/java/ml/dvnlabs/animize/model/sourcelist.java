package ml.dvnlabs.animize.model;

public class sourcelist {
    private String ids,by_user,source;

    public sourcelist(String id,String by,String sourc){
        this.ids = id;
        this.by_user = by;
        this.source = sourc;
    }

    public String getBy_user() {
        return by_user;
    }

    public String getIds() {
        return ids;
    }

    public String getSource() {
        return source;
    }
}
