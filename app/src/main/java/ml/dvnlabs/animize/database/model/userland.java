package ml.dvnlabs.animize.database.model;

public class userland {
    public static final String table_name = "userland";
    public static final String col_id_user = "id_user";
    public static final String col_name_user = "name_user";
    public static final String col_access_token = "token";
    public static final String col_email = "email";

    private String id;
    private String token;
    private String name;
    private String email;

    public static final String CREATE_TABLE = "CREATE TABLE " +table_name+"("+col_id_user+" TEXT, "+
            col_name_user + " TEXT, "+col_email+" TEXT, "+col_access_token+" TEXT)";

    public userland(){

    }
    public userland(String id_user,String name_user,String email,String tokeen){
        this.email = email;
        this.id = id_user;
        this.name = name_user;
        this.token = tokeen;

    }

    public String getNameUser() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getIdUser() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNameUser(String name) {
        this.name = name;
    }

    public void setIdUser(String id) {
        this.id = id;
    }
}
