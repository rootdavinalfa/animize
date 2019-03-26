package ml.dvnlabs.animize.driver;

public class Api {
    public static final String url_base = "https://api.dvnlabs.ml/animize/Api.php?apicall=";
    public static final String api_animlist = url_base+"getAnimList";
    public static final String api_animplay = url_base+"getAnimPlay&id_anim=";
    public static final String url_video = "http://api.dvnlabs.ml/animize/video/json.php?id=";
    public static final String url_search =url_base+"search_anim&name=";
    public static final String url_page = url_base+"getAnimListpage&page=";
    public static final String url_playlist_play = url_base+"getanimplaylist&pkg=";
    public static final String url_createuser = url_base+"createuser";
    public static final String url_loginuser = url_base+"loginusr";
    public static final String url_decode_login = url_base+"logindecode";




    //JSON TAG
    public static final String JSON_id_anim = "id_anim";
    public static final String JSON_name_anim = "name_anim";
    public static final String JSON_episode_anim = "episode_anim";
    public static final String JSON_episode_thumb = "thumbnail";
    public static final String JSON_source = "source";

}
