package ml.dvnlabs.animize.model;

public class api_usermodel {
    private String username,name_user;
    public api_usermodel(String user,String name){
        this.username = user;
        this.name_user = name;
    }

    public String getName_user() {
        return name_user;
    }

    public String getUsername() {
        return username;
    }
}