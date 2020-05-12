/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.driver;

public class Api {
    //private static final String url_base = "http://192.168.1.200/animize/";
    private static final String url_base = "https://api.dvnlabs.xyz/animize/";
    public static final String api_animlist = url_base+"anim/list/all";
    public static final String api_animplay = url_base+"anim/detail/play/";
    public static final String url_video = "http://api.dvnlabs.ml/animize/video/json.php?id=";
    public static final String url_search =url_base+"anim/search/name=";
    public static final String url_search_pack =url_base+"anim/search/package/";
    public static final String url_page = url_base+"anim/list/page/";
    public static final String url_playlist_play = url_base+"anim/detail/playlist/";
    public static final String url_createuser = url_base+"user/create";
    public static final String url_loginuser = url_base+"user/login/";
    public static final String url_decode_login = url_base+"user/login/decode";
    public static final String url_packagepage = url_base+"anim/list/package/page/";
    public static final String url_packageinfo = url_base+"package/info/";
    public static final String url_genremeta = url_base+"genre/meta";
    public static final String url_genrelist = url_base+"genre/list/";
    public static final String url_sourcelist = url_base+"source/list/";
    public static final String url_commentlist = url_base+"comment/list/";
    public static final String url_commentthread = url_base+"comment/thread/";
    public static final String url_commentadd = url_base+"comment/add";
    public static final String url_banner = url_base+"banner/get/";


    //JSON TAG
    public static final String JSON_id_anim = "id_anim";
    public static final String JSON_name_anim = "name_anim";
    public static final String JSON_episode_anim = "episode_anim";
    public static final String JSON_episode_thumb = "thumbnail";
    public static final String JSON_source = "source";

}
