package ml.dvnlabs.animize.driver;

public class Api {
    private static final String url_base = "https://api.dvnlabs.ml/animize/";
    public static final String api_animlist = url_base+"anim/list/all";
    public static final String api_animplay = url_base+"anim/detail/play/";
    public static final String url_video = "http://api.dvnlabs.ml/animize/video/json.php?id=";
    public static final String url_search =url_base+"anim/search/name=";
    public static final String url_page = url_base+"anim/list/page/";
    public static final String url_playlist_play = url_base+"anim/detail/playlist/";
    public static final String url_createuser = url_base+"user/create";
    public static final String url_loginuser = url_base+"user/login/";
    public static final String url_decode_login = url_base+"user/login/decode";




    //JSON TAG
    public static final String JSON_id_anim = "id_anim";
    public static final String JSON_name_anim = "name_anim";
    public static final String JSON_episode_anim = "episode_anim";
    public static final String JSON_episode_thumb = "thumbnail";
    public static final String JSON_source = "source";

}
