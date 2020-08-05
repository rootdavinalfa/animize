/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.database.User
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.ui.fragment.Register
import ml.dvnlabs.animize.util.MigrationHelper
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : AppCompatActivity() {
    var container: RelativeLayout? = null
    private var emailTF: EditText? = null
    private var passwordTF: EditText? = null
    private var loginButton: MaterialButton? = null
    private var registerButton: MaterialButton? = null
    private var progressDialogs: ProgressDialog? = null

    private val animizeDB: AnimizeDatabase by inject()

    private var tokeen: String = ""


    private val TAG = MainActivity::class.java.simpleName
    private var migratedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        migratedPref = this.getSharedPreferences("dbStatus", Context.MODE_PRIVATE)
        //initializa();
        Log.e("INITIALIZE:", "dvnlabs.xyz 2020,Animize Loader.Animize Team 2019.")
        Log.e("MESSAGE:", """
     メインプログラムではなく、サイドプロジェクト専用のプログラムです。
     このプログラムはペースの遅いアップデートになると述べてください。
     """.trimIndent())
        Log.e("ENGLISH:", """
     This program just for side project,not main project.
     Please be concern this program will be slow paced update.
     """.trimIndent())
    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    private fun initialize() = CoroutineScope(Dispatchers.Main).launch {
        var migrated = false
        migratedPref?.let {
            migrated = it.getBoolean("migrated", false)
        }

        if (migrated) {
            checkUser()
        } else {
            MigrationHelper.setupTaskImmediately(this@MainActivity)
            WorkManager.getInstance(this@MainActivity).getWorkInfosByTagLiveData(MigrationHelper.TAG).distinctUntilChanged().observe(this@MainActivity, androidx.lifecycle.Observer {
                val currentStatus = it?.getOrNull(0)
                lifecycleScope.launch {
                    if (currentStatus?.state?.isFinished!! && animizeDB.userDAO().countUser() == 0) {
                        checkUser()
                    } else {
                        checkUser()
                    }
                }
            })
        }
    }

    private fun checkUser() = CoroutineScope(Dispatchers.Main).launch {
        //Check is user exist or not,if exist don't initialize view for mainActivity
        if (animizeDB.userDAO().countUser() == 0) {
            Log.e(this@MainActivity.javaClass.simpleName, "USER NOT FOUND!")
            setTheme(R.style.AppTheme)
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            setWindowFlag(this@MainActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            setContentView(R.layout.activity_main)
            initViews()
        } else {
            Log.i(this@MainActivity.javaClass.simpleName, "USING USER!")
            val intent = Intent(this@MainActivity, AnimizeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        emailTF = findViewById(R.id.mainUserField)
        passwordTF = findViewById(R.id.mainPasswordField)
        loginButton = findViewById(R.id.mainButtonLogin)
        registerButton = findViewById(R.id.mainButtonRegister)

        loginButton!!.setOnClickListener {
            loginStep1()
        }

        registerButton!!.setOnClickListener {
            val register = Register()
            register.show(supportFragmentManager, "REGISTER")
        }
    }

    fun autoFillFromRegister(email: String, password: String) {
        emailTF!!.setText(email)
        passwordTF!!.setText(password)
    }

    private fun loginFunc(id: String, name: String, email: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            animizeDB.userDAO().newUser(User(
                    idUser = id,
                    email = email,
                    nameUser = name,
                    accessToken = tokeen
            ))
        }
        val intent = Intent(this, AnimizeActivity::class.java)
        startActivity(intent)
    }

    //LOGIN Function
    private fun loginStep1(): Unit {
        try {
            val url = Api.url_loginuser
            val params = HashMap<String, String>()
            params["email"] = emailTF!!.text.toString().trim { it <= ' ' }
            params["password"] = passwordTF!!.text.toString().trim { it <= ' ' }
            APINetworkRequest(this, fetchLoginListenerStep1, url, CODE_POST_REQUEST, params)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private var fetchLoginListenerStep1: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            progressDialogs!!.hide()
            jsonLogin(data!!)
        }

        override fun onFetchFailure(msg: String?) {
            //errorAuth()
            progressDialogs!!.hide()
            alert {
                title = "Credential Error!"
                message = "Email / Password wrong,please provide a correct one!"
            }
            Log.e(TAG, msg!!)
        }

        override fun onFetchStart() {
            progressDialogs = indeterminateProgressDialog("Loading", "Signing in....")
            progressDialogs!!.show()
        }
    }

    private fun errorAuth() {
        val toast = Toast.makeText(this, "Email/Password wrong!", Toast.LENGTH_SHORT)
        toast.show()
    }


    private fun jsonLogin(data: String) {
        try {
            val token: String
            val `object` = JSONObject(data)
            if (!`object`.getBoolean("error")) {
                token = `object`.getString("jwt")
                tokeen = token
                loginStep2()
                //Intent intent = new Intent(MainActivity.this,dashboard_activity.class);
                //intent.putExtra("token",token);
                //startActivity(intent);
            } else {
                val toast = Toast.makeText(this, "Please Enter required field!", Toast.LENGTH_SHORT)
                toast.show()
            }
        } catch (e: JSONException) {
            Log.e(TAG, e.message!!)
        }
    }

    private fun loginStep2() {
        try {
            val url = Api.url_decode_login
            val params = HashMap<String, String>()
            params["token"] = tokeen
            APINetworkRequest(this, fetchLoginListenerStep2, url, CODE_POST_REQUEST, params)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    var fetchLoginListenerStep2: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            progressDialogs!!.hide()
            loginDecode(data!!)
        }

        override fun onFetchFailure(msg: String?) {
            Log.e(TAG, msg!!)
            errorAuth()
        }

        override fun onFetchStart() {
            progressDialogs = indeterminateProgressDialog("Credential Exchange,this may take a long process!", "Handshaking!")
            progressDialogs!!.show()
        }
    }

    private fun loginDecode(st: String) {
        try {
            println("LOGIN DECODE:$st")
            val str = JSONObject(st)
            if (!str.getBoolean("error")) {
                val parse = str.getJSONObject("parse")
                val data = parse.getJSONObject("data")
                val idUser = data.getString("id_user")
                val nameUser = data.getString("name_user")
                val emails = data.getString("email")
                loginFunc(idUser, nameUser, emails)

            }
        } catch (e: JSONException) {
            Log.e("EXCEPTION JSON:", e.toString())
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
        private const val CODE_POST_REQUEST = 1025

        @JvmStatic
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}