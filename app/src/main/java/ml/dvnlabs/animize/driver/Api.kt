/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.driver

object Api {
    //private const val url_base = "http://192.168.1.200/animize/"
    private const val url_base = "https://api.dvnlabs.xyz/animize/"
    const val api_animlist = url_base + "anim/list/all"
    const val api_animplay = url_base + "anim/detail/play/"
    const val url_video = "http://api.dvnlabs.ml/animize/video/json.php?id="
    const val url_search = url_base + "anim/search/name="
    const val url_search_pack = url_base + "anim/search/package/"
    const val url_page = url_base + "anim/list/page/"
    const val url_playlist_play = url_base + "anim/detail/playlist/"
    const val url_createuser = url_base + "user/create"
    const val url_loginuser = url_base + "user/login/"
    const val url_decode_login = url_base + "user/login/decode"
    const val url_packagepage = url_base + "anim/list/package/page/"
    const val url_packageinfo = url_base + "package/info/"
    const val url_genremeta = url_base + "genre/meta"
    const val url_genrelist = url_base + "genre/list/"
    const val url_sourcelist = url_base + "source/list/"
    const val url_commentlist = url_base + "comment/list/"
    const val url_commentthread = url_base + "comment/thread/"
    const val url_commentadd = url_base + "comment/add"
    const val url_banner = url_base + "banner/get/"

    //JSON TAG
    const val JSON_id_anim = "id_anim"
    const val JSON_name_anim = "name_anim"
    const val JSON_episode_anim = "episode_anim"
    const val JSON_episode_thumb = "thumbnail"
    const val JSON_source = "source"
}