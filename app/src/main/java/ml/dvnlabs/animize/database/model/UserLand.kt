/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.database.model

class UserLand(id_user: String?, name_user: String?, var email: String?, tokeen: String?) {
    var idUser: String? = id_user
    var token: String? = tokeen
    var nameUser: String? = name_user

    companion object {
        const val table_name = "userland"
        const val col_id_user = "id_user"
        const val col_name_user = "name_user"
        const val col_access_token = "token"
        const val col_email = "email"
        const val CREATE_TABLE = "CREATE TABLE " + table_name + "(" + col_id_user + " TEXT, " +
                col_name_user + " TEXT, " + col_email + " TEXT, " + col_access_token + " TEXT)"
    }
}